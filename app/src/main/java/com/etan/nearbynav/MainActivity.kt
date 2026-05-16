package com.etan.nearbynav

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.etan.nearbynav.ui.screens.ArrowScreen
import com.etan.nearbynav.ui.screens.MainScreen
import com.etan.nearbynav.ui.theme.NearbyNavTheme
import com.etan.nearbynav.utils.PoiWithBearing
import com.etan.nearbynav.viewmodel.ArrowViewModel
import com.etan.nearbynav.viewmodel.MainViewModel
import com.etan.nearbynav.ui.screens.FetchScreen
import androidx.compose.runtime.saveable.rememberSaveable
import com.etan.nearbynav.ui.screens.SettingsScreen
import com.etan.nearbynav.data.AppTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val arrowViewModel: ArrowViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            mainViewModel.startLocationUpdates()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()

        setContent {
            val appTheme by mainViewModel.appTheme.collectAsState()
            var showFetchScreen by rememberSaveable { mutableStateOf(false) }
            var showSettingsScreen by rememberSaveable { mutableStateOf(false) }
            val selectedPoi by mainViewModel.selectedPoi.collectAsState()

            NearbyNavTheme(appTheme = appTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val uiState by mainViewModel.uiState.collectAsState()
                    val importStatus by mainViewModel.importStatus.collectAsState()
                    val arrowState by arrowViewModel.state.collectAsState()

                    when {
                        selectedPoi != null -> ArrowScreen(
                            state = arrowState,
                            onBack = { mainViewModel.clearSelectedPoi() }
                        )
                        showFetchScreen -> FetchScreen(
                            uiState = uiState,
                            onFetch = { radius, toggles ->
                                mainViewModel.fetchFromOverpass(radius, toggles)
                            },
                            onBack = { showFetchScreen = false }
                        )
                        showSettingsScreen -> SettingsScreen(
                            currentTheme = appTheme,
                            onThemeSelected = mainViewModel::setTheme,
                            onImportDatabase = mainViewModel::importDatabase,
                            importStatus = importStatus,
                            onBack = { showSettingsScreen = false }
                        )
                        else -> MainScreen(
                            uiState = uiState,
                            importStatus = importStatus,
                            onDirectionSelected = mainViewModel::selectDirection,
                            onImportDatabase = mainViewModel::importDatabase,
                            onPoiSelected = { poi ->
                                arrowViewModel.setPoi(poi)
                                mainViewModel.selectPoi(poi)
                            },
                            onOpenFetch = { showFetchScreen = true },
                            onOpenSettings = { showSettingsScreen = true }
                        )
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            mainViewModel.startLocationUpdates()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}