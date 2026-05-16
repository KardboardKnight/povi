package com.etan.nearbynav.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etan.nearbynav.utils.BearingUtils
import kotlin.math.*

@Composable
fun CompassRose(
    selectedDirection: String,
    deviceAzimuth: Float = 0f,
    onDirectionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val directions = BearingUtils.allDirections

    // All colors pulled from the current theme
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val error = MaterialTheme.colorScheme.error

    val textMeasurer = rememberTextMeasurer()

    val labelFontFamily = MaterialTheme.typography.bodyLarge.fontFamily
        ?: androidx.compose.ui.text.font.FontFamily.Default

    Canvas(
        modifier = modifier
            .size(260.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dx = offset.x - center.x
                    val dy = offset.y - center.y
                    val dist = sqrt(dx * dx + dy * dy)
                    val radius = size.width / 2f
                    if (dist > radius * 0.25f && dist < radius) {
                        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 90
                        if (angle < 0) angle += 360
                        val index = ((angle + 22.5) / 45).toInt() % 8
                        onDirectionSelected(directions[index])
                    }
                }
            }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width / 2f - 4.dp.toPx()
        val innerRadius = radius - 10.dp.toPx()

        // Outer ring — gradient from primary to onPrimaryContainer
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primary.copy(alpha = 0.7f),
                    primary,
                    onPrimaryContainer
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )

        // Inner face — gradient from surface to surfaceVariant
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(surface, surfaceVariant),
                center = Offset(center.x - innerRadius * 0.2f, center.y - innerRadius * 0.2f),
                radius = innerRadius * 1.2f
            ),
            radius = innerRadius,
            center = center
        )

        // Tick marks
        for (i in 0 until 72) {
            val angle = Math.toRadians(i * 5.0)
            val isMajor = i % 9 == 0
            val tickOuter = if (isMajor) innerRadius else innerRadius - 3.dp.toPx()
            val tickInner = if (isMajor) innerRadius - 8.dp.toPx() else innerRadius - 5.dp.toPx()
            drawLine(
                color = if (isMajor) primary else outline,
                start = Offset(
                    center.x + (tickOuter * sin(angle)).toFloat(),
                    center.y - (tickOuter * cos(angle)).toFloat()
                ),
                end = Offset(
                    center.x + (tickInner * sin(angle)).toFloat(),
                    center.y - (tickInner * cos(angle)).toFloat()
                ),
                strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
            )
        }

        // Selected sector highlight
        directions.forEachIndexed { i, dir ->
            if (dir == selectedDirection) {
                val startAngle = i * 45f - 90f - 22.5f
                drawArc(
                    color = primary.copy(alpha = 0.18f),
                    startAngle = startAngle,
                    sweepAngle = 45f,
                    useCenter = true,
                    topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                    size = androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2)
                )
                drawArc(
                    color = primary,
                    startAngle = startAngle,
                    sweepAngle = 45f,
                    useCenter = true,
                    topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                    size = androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2),
                    style = Stroke(1.5.dp.toPx())
                )
            }
        }

        // Crosshair lines
        for (i in 0 until 4) {
            val angle = Math.toRadians(i * 90.0)
            drawLine(
                color = outline.copy(alpha = 0.4f),
                start = Offset(
                    center.x + (innerRadius * 0.15f * sin(angle)).toFloat(),
                    center.y - (innerRadius * 0.15f * cos(angle)).toFloat()
                ),
                end = Offset(
                    center.x + (innerRadius * 0.85f * sin(angle)).toFloat(),
                    center.y - (innerRadius * 0.85f * cos(angle)).toFloat()
                ),
                strokeWidth = 0.5.dp.toPx()
            )
        }

        // Direction labels
        directions.forEachIndexed { i, dir ->
            val isSelected = dir == selectedDirection
            val isCardinal = i % 2 == 0
            val labelAngle = Math.toRadians((i * 45 - 90).toDouble())
            val labelRadius = innerRadius * 0.68f
            val labelX = center.x + (labelRadius * cos(labelAngle)).toFloat()
            val labelY = center.y + (labelRadius * sin(labelAngle)).toFloat()
            val style = TextStyle(
                fontSize = if (isCardinal) 15.sp else 11.sp,
                color = when {
                    isSelected -> primary
                    isCardinal -> onSurface
                    else -> onSurfaceVariant
                },
                fontWeight = if (isSelected || isCardinal)
                    androidx.compose.ui.text.font.FontWeight.Bold
                else
                    androidx.compose.ui.text.font.FontWeight.Normal,
                fontFamily = labelFontFamily,
                letterSpacing = 1.sp
            )
            val measured = textMeasurer.measure(dir, style)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    labelX - measured.size.width / 2f,
                    labelY - measured.size.height / 2f
                )
            )
        }

        // North needle
        rotate(degrees = -deviceAzimuth, pivot = center) {
            val needleLength = innerRadius * 0.52f
            val needleWidth = innerRadius * 0.065f

            // Red north tip
            val northPath = Path().apply {
                moveTo(center.x, center.y - needleLength)
                lineTo(center.x + needleWidth, center.y)
                lineTo(center.x - needleWidth, center.y)
                close()
            }
            drawPath(northPath, color = error)
            drawPath(northPath, color = onPrimaryContainer, style = Stroke(1.dp.toPx()))

            // South tail
            val southPath = Path().apply {
                moveTo(center.x, center.y + needleLength)
                lineTo(center.x + needleWidth, center.y)
                lineTo(center.x - needleWidth, center.y)
                close()
            }
            drawPath(southPath, color = surfaceVariant)
            drawPath(southPath, color = onPrimaryContainer, style = Stroke(1.dp.toPx()))
        }

        // Center pivot
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primary.copy(alpha = 0.7f), onPrimaryContainer),
                center = center,
                radius = 10.dp.toPx()
            ),
            radius = 10.dp.toPx(),
            center = center
        )
        drawCircle(
            color = onPrimaryContainer,
            radius = 10.dp.toPx(),
            center = center,
            style = Stroke(1.dp.toPx())
        )
        drawCircle(color = surface, radius = 4.dp.toPx(), center = center)
    }
}