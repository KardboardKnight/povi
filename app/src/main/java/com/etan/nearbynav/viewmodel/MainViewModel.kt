package com.etan.nearbynav.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.etan.nearbynav.data.AppDatabase
import com.etan.nearbynav.data.DatabaseSeeder
import com.etan.nearbynav.location.LocationManager
import com.etan.nearbynav.utils.BearingUtils
import com.etan.nearbynav.utils.PoiType
import com.etan.nearbynav.utils.PoiWithBearing
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.etan.nearbynav.utils.DatabaseImporter
import com.etan.nearbynav.utils.CompassSensor
import com.etan.nearbynav.network.OverpassClient
import com.etan.nearbynav.data.City
import com.etan.nearbynav.data.GasStation
import com.etan.nearbynav.data.Restaurant
import com.etan.nearbynav.data.PointOfInterest
import com.etan.nearbynav.ui.screens.PoiToggles
import com.etan.nearbynav.data.AppTheme
import com.etan.nearbynav.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn


data class UiState(
    val hasLocation: Boolean = false,
    val currentLat: Double = 0.0,
    val currentLng: Double = 0.0,
    val selectedDirection: String = "N",
    val results: List<PoiWithBearing> = emptyList(),
    val allPois: List<PoiWithBearing> = emptyList(),
    val locationError: Boolean = false,
    val deviceAzimuth: Float = 0.0f,
    val isFetching: Boolean = false,
    val fetchStatus: String = ""
)

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.get(app)
    private val dao = db.poiDao()
    private val locationManager = LocationManager(app)

    private val compassSensor = CompassSensor(getApplication())

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _importStatus = MutableStateFlow("")
    val importStatus: StateFlow<String> = _importStatus.asStateFlow()

    private val _selectedPoi = MutableStateFlow<PoiWithBearing?>(null)
    val selectedPoi: StateFlow<PoiWithBearing?> = _selectedPoi.asStateFlow()

    private val settingsRepo = SettingsRepository(app)

    val appTheme: StateFlow<AppTheme> = settingsRepo.themeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppTheme.VINTAGE)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { settingsRepo.setTheme(theme) }
    }

    fun selectPoi(poi: PoiWithBearing) {
        _selectedPoi.value = poi
    }

    fun clearSelectedPoi() {
        _selectedPoi.value = null
    }

    init {
        viewModelScope.launch { seedIfNeeded() }
        startCompass()
    }

    private suspend fun seedIfNeeded() {
        if (dao.cityCount() == 0) {
            dao.insertCities(DatabaseSeeder.cities())
            dao.insertGasStations(DatabaseSeeder.gasStations())
        }
    }

    private fun startCompass() {
        viewModelScope.launch {
            compassSensor.azimuthFlow().collect { azimuth ->
                _uiState.update { it.copy(deviceAzimuth = azimuth) }
            }
        }
    }
    fun startLocationUpdates() {
        viewModelScope.launch {
            locationManager.locationFlow()
                .catch { _uiState.update { it.copy(locationError = true) } }
                .collect { location ->
                    val allPois = buildPoiList(location.latitude, location.longitude)
                    _uiState.update { state ->
                        state.copy(
                            hasLocation = true,
                            currentLat = location.latitude,
                            currentLng = location.longitude,
                            allPois = allPois,
                            results = filterByDirection(allPois, state.selectedDirection)
                        )
                    }
                }
        }
    }

    fun selectDirection(direction: String) {
        _uiState.update { state ->
            state.copy(
                selectedDirection = direction,
                results = filterByDirection(state.allPois, direction)
            )
        }
    }
    fun importDatabase(uri: android.net.Uri) {
        viewModelScope.launch {
            val success = DatabaseImporter.importDatabase(getApplication(), uri)
            if (success) {
                // Re-fetch dao from the newly opened database
                val newDao = AppDatabase.get(getApplication()).poiDao()

                // Validate the imported database has data
                val cityCount = newDao.cityCount()
                val stationCount = newDao.gasStationCount()

                if (cityCount == 0 && stationCount == 0) {
                    _importStatus.value = "Import failed — database appears empty"
                    return@launch
                }

                // Reload POIs using the new dao
                val state = _uiState.value
                if (state.hasLocation) {
                    val allPois = buildPoiListFromDao(newDao, state.currentLat, state.currentLng)
                    _uiState.update {
                        it.copy(
                            allPois = allPois,
                            results = filterByDirection(allPois, it.selectedDirection)
                        )
                    }
                }

                _importStatus.value = "Imported $cityCount cities and $stationCount gas stations"
            } else {
                _importStatus.value = "Import failed — make sure it's a valid .db file"
            }
        }
    }

    private suspend fun buildPoiList(lat: Double, lng: Double): List<PoiWithBearing> {
        val dao = AppDatabase.get(getApplication()).poiDao()
        val cities = dao.getAllCities().map { it.toPoiWithBearing(lat, lng, PoiType.CITY) }
        val stations = dao.getAllGasStations().map { it.toPoiWithBearing(lat, lng, PoiType.GAS_STATION) }
        val restaurants = dao.getAllRestaurants().map { city ->
            makePoi(city.name, PoiType.RESTAURANT, city.lat, city.lng, lat, lng, city.cuisine)
        }
        val pois = dao.getAllPointsOfInterest().map { poi ->
            val type = when (poi.type) {
                "hotel" -> PoiType.HOTEL
                "hospital" -> PoiType.HOSPITAL
                "pharmacy" -> PoiType.PHARMACY
                "supermarket" -> PoiType.SUPERMARKET
                "atm" -> PoiType.ATM
                "park" -> PoiType.PARK
                "national_forest" -> PoiType.NATIONAL_FOREST
                "tourist_attraction" -> PoiType.TOURIST_ATTRACTION
                "campground" -> PoiType.CAMPGROUND
                else -> PoiType.UNKNOWN
            }
            makePoi(poi.name, type, poi.lat, poi.lng, lat, lng)
        }
        return (cities + stations + restaurants + pois).sortedBy { it.distanceKm }
    }

    private fun makePoi(name: String, type: PoiType, poiLat: Double, poiLng: Double, fromLat: Double, fromLng: Double, brand: String = ""): PoiWithBearing {
        val bearing = BearingUtils.bearing(fromLat, fromLng, poiLat, poiLng)
        return PoiWithBearing(
            name = name,
            type = type,
            bearing = bearing,
            distanceKm = BearingUtils.distanceKm(fromLat, fromLng, poiLat, poiLng),
            direction = BearingUtils.toCardinal(bearing),
            brand = brand
        )
    }

    private fun City.toPoiWithBearing(lat: Double, lng: Double, type: PoiType): PoiWithBearing {
        val bearing = BearingUtils.bearing(lat, lng, this.lat, this.lng)
        return PoiWithBearing(
            name = name, type = type, bearing = bearing,
            distanceKm = BearingUtils.distanceKm(lat, lng, this.lat, this.lng),
            direction = BearingUtils.toCardinal(bearing)
        )
    }

    private fun GasStation.toPoiWithBearing(lat: Double, lng: Double, type: PoiType): PoiWithBearing {
        val bearing = BearingUtils.bearing(lat, lng, this.lat, this.lng)
        return PoiWithBearing(
            name = name, type = type, bearing = bearing,
            distanceKm = BearingUtils.distanceKm(lat, lng, this.lat, this.lng),
            direction = BearingUtils.toCardinal(bearing),
            brand = brand
        )
    }

    private suspend fun buildPoiListFromDao(dao: com.etan.nearbynav.data.PoiDao, lat: Double, lng: Double): List<PoiWithBearing> {
        val cities = dao.getAllCities().map { city ->
            PoiWithBearing(
                name = city.name,
                type = PoiType.CITY,
                bearing = BearingUtils.bearing(lat, lng, city.lat, city.lng),
                distanceKm = BearingUtils.distanceKm(lat, lng, city.lat, city.lng),
                direction = BearingUtils.toCardinal(
                    BearingUtils.bearing(lat, lng, city.lat, city.lng)
                )
            )
        }
        val stations = dao.getAllGasStations().map { station ->
            PoiWithBearing(
                name = station.name,
                type = PoiType.GAS_STATION,
                bearing = BearingUtils.bearing(lat, lng, station.lat, station.lng),
                distanceKm = BearingUtils.distanceKm(lat, lng, station.lat, station.lng),
                direction = BearingUtils.toCardinal(
                    BearingUtils.bearing(lat, lng, station.lat, station.lng)
                ),
                brand = station.brand
            )
        }
        return (cities + stations).sortedBy { it.distanceKm }
    }

    private fun filterByDirection(pois: List<PoiWithBearing>, direction: String) =
        pois.filter { it.direction == direction }

    fun fetchFromOverpass(radiusKm: Int, toggles: com.etan.nearbynav.ui.screens.PoiToggles) {
        val state = _uiState.value
        if (!state.hasLocation) {
            _uiState.update { it.copy(fetchStatus = "No GPS location yet") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isFetching = true, fetchStatus = "Querying Overpass...") }
            try {
                val radiusM = radiusKm * 1000
                val result = OverpassClient.query(state.currentLat, state.currentLng, radiusM, toggles)
                val dao = AppDatabase.get(getApplication()).poiDao()
                dao.deleteAllCities()
                dao.deleteAllGasStations()
                dao.deleteAllRestaurants()
                dao.deleteAllPointsOfInterest()
                dao.insertCities(result.cities)
                dao.insertGasStations(result.stations)
                dao.insertRestaurants(result.restaurants)
                dao.insertPointsOfInterest(result.pointsOfInterest)

                val allPois = buildPoiList(state.currentLat, state.currentLng)
                _uiState.update {
                    it.copy(
                        isFetching = false,
                        fetchStatus = "Loaded ${result.cities.size} cities, ${result.stations.size} stations, ${result.restaurants.size} restaurants, ${result.pointsOfInterest.size} other POIs",
                        allPois = allPois,
                        results = filterByDirection(allPois, it.selectedDirection)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isFetching = false, fetchStatus = "Fetch failed: ${e.message}")
                }
            }
        }
    }
}

