package com.etan.nearbynav.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.etan.nearbynav.data.AppTheme

@Composable
fun NearbyNavTheme(
    appTheme: AppTheme = AppTheme.VINTAGE,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.VINTAGE -> VintageColorScheme
        AppTheme.CYBERPUNK -> CyberpunkColorScheme
        AppTheme.NIGHT -> NightColorScheme
        AppTheme.NAUTICAL -> NauticalColorScheme
        AppTheme.GIRLYPOP -> GirlypopColorScheme
    }

    val typography = when (appTheme) {
        AppTheme.VINTAGE -> VintageTypography
        AppTheme.CYBERPUNK -> CyberpunkTypography
        else -> ModernTypography
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}