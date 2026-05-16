package com.etan.nearbynav.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etan.nearbynav.utils.PoiType
import com.etan.nearbynav.utils.PoiWithBearing
import kotlin.math.roundToInt

@Composable
fun PoiCard(
    poi: PoiWithBearing,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = when (poi.type) {
                            PoiType.CITY -> "🏙"
                            PoiType.GAS_STATION -> "⛽"
                            PoiType.RESTAURANT -> "🍽"
                            PoiType.HOTEL -> "🏨"
                            PoiType.HOSPITAL -> "🏥"
                            PoiType.PHARMACY -> "💊"
                            PoiType.SUPERMARKET -> "🛒"
                            PoiType.ATM -> "💳"
                            PoiType.PARK -> "🌳"
                            PoiType.NATIONAL_FOREST -> "🌲"
                            PoiType.TOURIST_ATTRACTION -> "📸"
                            PoiType.CAMPGROUND -> "⛺"
                            PoiType.UNKNOWN -> "📍"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    poi.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = buildString {
                        append(when (poi.type) {
                            PoiType.CITY -> "City"
                            PoiType.GAS_STATION -> poi.brand.ifEmpty { "Gas Station" }
                            PoiType.RESTAURANT -> "Restaurant"
                            PoiType.HOTEL -> "Hotel"
                            PoiType.HOSPITAL -> "Hospital"
                            PoiType.PHARMACY -> "Pharmacy"
                            PoiType.SUPERMARKET -> "Supermarket"
                            PoiType.ATM -> "ATM"
                            PoiType.PARK -> "Park"
                            PoiType.NATIONAL_FOREST -> "National Forest"
                            PoiType.TOURIST_ATTRACTION -> "Attraction"
                            PoiType.CAMPGROUND -> "Campground"
                            PoiType.UNKNOWN -> "Point of Interest"
                        })
                        append("  ·  ${poi.distanceKm.roundToInt()} km ${poi.direction}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "›",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}