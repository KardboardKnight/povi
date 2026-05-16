package com.etan.nearbynav.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.etan.nearbynav.utils.BearingUtils
import com.etan.nearbynav.utils.CompassSensor
import com.etan.nearbynav.utils.PoiWithBearing
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ArrowUiState(
    val poiName: String = "",
    val distanceKm: Double = 0.0,
    val direction: String = "",
    // Bearing from your location to the POI (fixed, based on GPS)
    val bearingToPoi: Float = 0.0f,
    // Device compass heading (live)
    val deviceAzimuth: Float = 0.0f,
    // The arrow rotation — difference between bearing and azimuth
    val arrowRotation: Float = 0.0f
)

class ArrowViewModel(app: Application) : AndroidViewModel(app) {

    private val compassSensor = CompassSensor(app)

    private val _state = MutableStateFlow(ArrowUiState())
    val state: StateFlow<ArrowUiState> = _state.asStateFlow()

    fun setPoi(poi: PoiWithBearing) {
        _state.update {
            it.copy(
                poiName = poi.name,
                distanceKm = poi.distanceKm,
                direction = poi.direction,
                bearingToPoi = poi.bearing
            )
        }
        startCompass()
    }

    private fun startCompass() {
        viewModelScope.launch {
            compassSensor.azimuthFlow().collect { azimuth ->
                _state.update { state ->
                    // Arrow rotation = where the POI is, minus where the phone is pointing
                    val rotation = (state.bearingToPoi - azimuth + 360) % 360
                    state.copy(
                        deviceAzimuth = azimuth,
                        arrowRotation = rotation
                    )
                }
            }
        }
    }
}