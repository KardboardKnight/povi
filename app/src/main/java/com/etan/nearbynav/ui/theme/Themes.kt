package com.etan.nearbynav.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// — VINTAGE (existing) —
val VintageColorScheme = lightColorScheme(
    primary = Brass,
    onPrimary = AgedWhite,
    primaryContainer = ParchmentDark,
    onPrimaryContainer = BrassDark,
    secondary = LeatherLight,
    onSecondary = AgedWhite,
    secondaryContainer = ParchmentDeep,
    onSecondaryContainer = Leather,
    tertiary = WornGreen,
    onTertiary = AgedWhite,
    background = Parchment,
    onBackground = InkBrown,
    surface = AgedWhite,
    onSurface = InkBrown,
    surfaceVariant = ParchmentDark,
    onSurfaceVariant = LeatherLight,
    error = FadedRed,
    onError = AgedWhite,
    errorContainer = Color(0xFFF5D5D5),
    onErrorContainer = FadedRed,
    outline = ParchmentDeep,
    outlineVariant = ParchmentDark
)

// — CYBERPUNK —
val NeonCyan = Color(0xFF00FFFF)
val NeonPink = Color(0xFFFF0090)
val NeonYellow = Color(0xFFFFFF00)
val CyberDark = Color(0xFF0A0A0F)
val CyberSurface = Color(0xFF12121A)
val CyberSurfaceVariant = Color(0xFF1A1A28)
val CyberOnSurface = Color(0xFFE0E0FF)
val CyberDim = Color(0xFF404060)
val CyberRed = Color(0xFFFF003C)

val CyberpunkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberDark,
    primaryContainer = Color(0xFF003333),
    onPrimaryContainer = NeonCyan,
    secondary = NeonPink,
    onSecondary = CyberDark,
    secondaryContainer = Color(0xFF330020),
    onSecondaryContainer = NeonPink,
    tertiary = NeonYellow,
    onTertiary = CyberDark,
    background = CyberDark,
    onBackground = CyberOnSurface,
    surface = CyberSurface,
    onSurface = CyberOnSurface,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberDim,
    error = CyberRed,
    onError = CyberOnSurface,
    outline = NeonCyan.copy(alpha = 0.4f),
    outlineVariant = CyberSurfaceVariant
)

// — NIGHT —
val NightBackground = Color(0xFF0A0E1A)
val NightSurface = Color(0xFF141B2D)
val NightPrimary = Color(0xFF4CAF50)
val NightPrimaryLight = Color(0xFF81C784)
val NightAccent = Color(0xFF00E676)
val NightOnSurface = Color(0xFFE0E8E0)
val NightDim = Color(0xFF4A5568)
val NightRed = Color(0xFFEF5350)

val NightColorScheme = darkColorScheme(
    primary = NightPrimary,
    onPrimary = Color(0xFF003300),
    primaryContainer = Color(0xFF1B3A1B),
    onPrimaryContainer = NightPrimaryLight,
    secondary = NightAccent,
    onSecondary = Color(0xFF003300),
    secondaryContainer = Color(0xFF1A2E1A),
    onSecondaryContainer = NightAccent,
    background = NightBackground,
    onBackground = NightOnSurface,
    surface = NightSurface,
    onSurface = NightOnSurface,
    surfaceVariant = Color(0xFF1E2A3A),
    onSurfaceVariant = NightDim,
    error = NightRed,
    onError = Color.White,
    outline = NightDim,
    outlineVariant = Color(0xFF2D3748)
)

// — NAUTICAL —
val NavyDeep = Color(0xFF0A1628)
val NavyMid = Color(0xFF1A2E4A)
val NavyLight = Color(0xFF2E4A6E)
val RopeGold = Color(0xFFD4A843)
val RopeGoldLight = Color(0xFFE8C068)
val SeaFoam = Color(0xFFE8F4F0)
val ChartWhite = Color(0xFFF0F4F8)
val MarineRed = Color(0xFFB22222)

val NauticalColorScheme = darkColorScheme(
    primary = RopeGold,
    onPrimary = NavyDeep,
    primaryContainer = NavyMid,
    onPrimaryContainer = RopeGoldLight,
    secondary = SeaFoam,
    onSecondary = NavyDeep,
    secondaryContainer = NavyLight,
    onSecondaryContainer = SeaFoam,
    background = NavyDeep,
    onBackground = ChartWhite,
    surface = NavyMid,
    onSurface = ChartWhite,
    surfaceVariant = NavyLight,
    onSurfaceVariant = RopeGoldLight,
    error = MarineRed,
    onError = ChartWhite,
    outline = NavyLight,
    outlineVariant = NavyMid
)
// — GIRLYPOP —
val BlossomPink = Color(0xFFFF6B9D)
val BlossomPinkLight = Color(0xFFFF9EC4)
val BlossomPinkDark = Color(0xFFD4457A)
val PetalSurface = Color(0xFFFFF0F5)
val PetalBackground = Color(0xFFFCE4EC)
val LavenderMist = Color(0xFFE8D5F5)
val LavenderDeep = Color(0xFF9C27B0)
val RoseGold = Color(0xFFB76E79)
val CandyRed = Color(0xFFE91E8C)
val CreamWhite = Color(0xFFFFFAFD)
val PlumInk = Color(0xFF3D0030)

val GirlypopColorScheme = lightColorScheme(
    primary = BlossomPink,
    onPrimary = CreamWhite,
    primaryContainer = LavenderMist,
    onPrimaryContainer = BlossomPinkDark,
    secondary = RoseGold,
    onSecondary = CreamWhite,
    secondaryContainer = Color(0xFFFFE4EE),
    onSecondaryContainer = PlumInk,
    tertiary = LavenderDeep,
    onTertiary = CreamWhite,
    background = PetalBackground,
    onBackground = PlumInk,
    surface = PetalSurface,
    onSurface = PlumInk,
    surfaceVariant = LavenderMist,
    onSurfaceVariant = RoseGold,
    error = CandyRed,
    onError = CreamWhite,
    errorContainer = Color(0xFFFFD6E7),
    onErrorContainer = BlossomPinkDark,
    outline = BlossomPinkLight,
    outlineVariant = LavenderMist
)