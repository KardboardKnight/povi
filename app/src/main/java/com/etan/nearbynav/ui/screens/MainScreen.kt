package com.etan.nearbynav.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.etan.nearbynav.ui.components.CompassRose
import com.etan.nearbynav.ui.components.PoiCard
import com.etan.nearbynav.viewmodel.UiState
import com.etan.nearbynav.utils.PoiWithBearing

@Composable
fun MainScreen(
    uiState: UiState,
    importStatus: String,
    onDirectionSelected: (String) -> Unit,
    onImportDatabase: (android.net.Uri) -> Unit,
    onPoiSelected: (PoiWithBearing) -> Unit,
    onOpenFetch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row: location status + import button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Location pill
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (uiState.hasLocation)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (uiState.locationError) "Location unavailable"
                    else if (uiState.hasLocation)
                        "%.4f, %.4f".format(uiState.currentLat, uiState.currentLng)
                    else "Acquiring GPS...",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (uiState.hasLocation)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledTonalButton(onClick = onOpenFetch) { Text("Fetch") }
            FilledTonalButton(onClick = onOpenSettings) { Text("Settings") }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Tap a direction",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        CompassRose(
            selectedDirection = uiState.selectedDirection,
            deviceAzimuth = uiState.deviceAzimuth,
            onDirectionSelected = onDirectionSelected
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(uiState.selectedDirection, style = MaterialTheme.typography.titleMedium)
            Text(
                "${uiState.results.size} result${if (uiState.results.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))

        if (!uiState.hasLocation) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No results in this direction",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.results) { poi ->
                    PoiCard(
                        poi = poi,
                        onClick = { onPoiSelected(poi) }
                    )
                }
            }
        }
    }
}