package com.etan.nearbynav.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.etan.nearbynav.data.AppTheme

@Composable
fun SettingsScreen(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onImportDatabase: (android.net.Uri) -> Unit,
    importStatus: String,
    onBack: () -> Unit
) {
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { onImportDatabase(it) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        // Appearance section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Appearance", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))

                ThemeOption(
                    title = "Vintage",
                    description = "Parchment & brass — classic explorer",
                    selected = currentTheme == AppTheme.VINTAGE,
                    onClick = { onThemeSelected(AppTheme.VINTAGE) }
                )
                ThemeOption(
                    title = "Cyberpunk",
                    description = "Neon cyan & pink — high contrast dark",
                    selected = currentTheme == AppTheme.CYBERPUNK,
                    onClick = { onThemeSelected(AppTheme.CYBERPUNK) }
                )
                ThemeOption(
                    title = "Night",
                    description = "Dark green on black — night riding",
                    selected = currentTheme == AppTheme.NIGHT,
                    onClick = { onThemeSelected(AppTheme.NIGHT) }
                )
                ThemeOption(
                    title = "Nautical",
                    description = "Navy & gold — maritime chart",
                    selected = currentTheme == AppTheme.NAUTICAL,
                    onClick = { onThemeSelected(AppTheme.NAUTICAL) }
                )
                ThemeOption(
                    title = "Girlypop",
                    description = "Pink & lavender — cute and bold",
                    selected = currentTheme == AppTheme.GIRLYPOP,
                    onClick = { onThemeSelected(AppTheme.GIRLYPOP) }
                )
            }
        }

        // Database section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Database", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))

                FilledTonalButton(
                    onClick = { filePicker.launch("*/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import Database File")
                }

                if (importStatus.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (importStatus.startsWith("Database"))
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = importStatus,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (importStatus.startsWith("Database"))
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface)
                Text(description, style = MaterialTheme.typography.bodySmall,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (selected) {
                Text("✓", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}