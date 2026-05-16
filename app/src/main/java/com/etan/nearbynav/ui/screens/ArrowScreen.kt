package com.etan.nearbynav.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etan.nearbynav.viewmodel.ArrowUiState
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArrowScreen(
    state: ArrowUiState,
    onBack: () -> Unit
) {
    // Smoothly animate the arrow rotation
    val animatedRotation by animateFloatAsState(
        targetValue = state.arrowRotation,
        animationSpec = tween(durationMillis = 200, easing = LinearEasing),
        label = "arrow_rotation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Navigating to") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // POI name
            Text(
                text = state.poiName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Distance and direction
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${state.distanceKm.roundToInt()} km away",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "·",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = state.direction,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Arrow
            val primaryColor = MaterialTheme.colorScheme.primary
            val surfaceColor = MaterialTheme.colorScheme.surface
            val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
            val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
            val errorColor = MaterialTheme.colorScheme.error
            val onPrimaryContainerColor = MaterialTheme.colorScheme.onPrimaryContainer

            Canvas(modifier = Modifier.size(260.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(surfaceColor, surfaceVariantColor),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )

                drawCircle(
                    color = primaryColor,
                    radius = radius,
                    center = center,
                    style = Stroke(6.dp.toPx())
                )

                rotate(degrees = animatedRotation, pivot = center) {
                    drawArrow(center = center, radius = radius * 0.75f, color = errorColor)
                }

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.7f), onPrimaryContainerColor),
                        center = center,
                        radius = 10.dp.toPx()
                    ),
                    radius = 10.dp.toPx(),
                    center = center
                )
                drawCircle(color = surfaceColor, radius = 4.dp.toPx(), center = center)
            }

            Spacer(Modifier.height(8.dp))

            // Compass heading readout
            Text(
                text = "Heading: ${state.deviceAzimuth.roundToInt()}°",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Bearing to target: ${state.bearingToPoi.roundToInt()}°",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun DrawScope.drawArrow(
    center: Offset,
    radius: Float,
    color: Color
) {
    val arrowPath = Path().apply {
        // Tip of arrow (pointing up before rotation)
        moveTo(center.x, center.y - radius)
        // Right side of arrowhead
        lineTo(center.x + radius * 0.25f, center.y - radius * 0.35f)
        // Right inner
        lineTo(center.x + radius * 0.1f, center.y - radius * 0.35f)
        // Right tail
        lineTo(center.x + radius * 0.1f, center.y + radius * 0.6f)
        // Bottom right of tail
        lineTo(center.x + radius * 0.22f, center.y + radius * 0.6f)
        // Bottom center (tail base)
        lineTo(center.x, center.y + radius * 0.85f)
        // Bottom left of tail
        lineTo(center.x - radius * 0.22f, center.y + radius * 0.6f)
        // Left tail
        lineTo(center.x - radius * 0.1f, center.y + radius * 0.6f)
        // Left inner
        lineTo(center.x - radius * 0.1f, center.y - radius * 0.35f)
        // Left side of arrowhead
        lineTo(center.x - radius * 0.25f, center.y - radius * 0.35f)
        close()
    }

    drawPath(path = arrowPath, color = color)
}