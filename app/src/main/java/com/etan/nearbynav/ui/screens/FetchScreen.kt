package com.etan.nearbynav.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.etan.nearbynav.viewmodel.UiState

data class PoiToggles(
    val cities: Boolean = true,
    val gasStations: Boolean = true,
    val restaurants: Boolean = false,
    val hotels: Boolean = false,
    val hospitals: Boolean = false,
    val pharmacies: Boolean = false,
    val supermarkets: Boolean = false,
    val atms: Boolean = false,
    val parks: Boolean = false,
    val nationalForests: Boolean = false,
    val touristAttractions: Boolean = false,
    val campgrounds: Boolean = false
)

@Composable
fun FetchScreen(
    uiState: UiState,
    onFetch: (Int, PoiToggles) -> Unit,
    onBack: () -> Unit
) {
    var radiusKm by remember { mutableStateOf(100f) }
    var toggles by remember { mutableStateOf(PoiToggles()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Text("Fetch POI Data", style = MaterialTheme.typography.headlineMedium)

        // Current location
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (uiState.hasLocation)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (uiState.hasLocation)
                    "📍 %.4f, %.4f".format(uiState.currentLat, uiState.currentLng)
                else
                    "No GPS location yet — go back and wait for GPS",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (uiState.hasLocation)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Radius slider
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Search radius: ${radiusKm.toInt()} km",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Slider(
                    value = radiusKm,
                    onValueChange = { radiusKm = it },
                    valueRange = 25f..200f,
                    steps = 6
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("25 km", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("200 km", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // POI toggles
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Points of Interest",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                ToggleRow("Cities & Towns", toggles.cities) {
                    toggles = toggles.copy(cities = it)
                }
                ToggleRow("Gas Stations", toggles.gasStations) {
                    toggles = toggles.copy(gasStations = it)
                }
                ToggleRow("Restaurants", toggles.restaurants) {
                    toggles = toggles.copy(restaurants = it)
                }
                ToggleRow("Hotels & Motels", toggles.hotels) {
                    toggles = toggles.copy(hotels = it)
                }
                ToggleRow("Hospitals & Clinics", toggles.hospitals) {
                    toggles = toggles.copy(hospitals = it)
                }
                ToggleRow("Pharmacies", toggles.pharmacies) {
                    toggles = toggles.copy(pharmacies = it)
                }
                ToggleRow("Supermarkets", toggles.supermarkets) {
                    toggles = toggles.copy(supermarkets = it)
                }
                ToggleRow("ATMs", toggles.atms) {
                    toggles = toggles.copy(atms = it)
                }
                ToggleRow("Parks & Nature Reserves", toggles.parks) {
                    toggles = toggles.copy(parks = it) }
                ToggleRow("National Forests & Wilderness", toggles.nationalForests) {
                    toggles = toggles.copy(nationalForests = it) }
                ToggleRow("Tourist Attractions", toggles.touristAttractions) {
                    toggles = toggles.copy(touristAttractions = it) }
                ToggleRow("Campgrounds", toggles.campgrounds) {
                    toggles = toggles.copy(campgrounds = it) }
            }
        }

        // Info
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Fetches live data from OpenStreetMap. Large radius or many POI types may take 30–90 seconds. This will replace your current database.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        // Status
        if (uiState.fetchStatus.isNotEmpty()) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (uiState.fetchStatus.startsWith("Loaded") || uiState.fetchStatus.startsWith("Querying"))
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = uiState.fetchStatus,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (uiState.fetchStatus.startsWith("Loaded") || uiState.fetchStatus.startsWith("Querying"))
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (uiState.isFetching) {
            CircularProgressIndicator()
            Text(
                "Fetching from OpenStreetMap...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Button(
                onClick = { onFetch(radiusKm.toInt(), toggles) },
                enabled = uiState.hasLocation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fetch Data Now")
            }
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}