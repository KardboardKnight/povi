package com.etan.nearbynav.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.*

class CompassSensor(context: Context) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun azimuthFlow(): Flow<Float> = callbackFlow {
        val rotationMatrix = FloatArray(9)
        val flatMatrix = FloatArray(9)
        val verticalMatrix = FloatArray(9)
        val flatOrientation = FloatArray(3)
        val verticalOrientation = FloatArray(3)

        // Smoothed gravity vector to detect tilt
        val gravity = FloatArray(3)
        var tiltRatio = 0f  // 0.0 = flat, 1.0 = vertical

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        // Low-pass filter to smooth gravity
                        gravity[0] = 0.8f * gravity[0] + 0.2f * event.values[0]
                        gravity[1] = 0.8f * gravity[1] + 0.2f * event.values[1]
                        gravity[2] = 0.8f * gravity[2] + 0.2f * event.values[2]

                        // Tilt is how much gravity is in X/Y vs Z
                        // When flat: Z ≈ 9.8, X/Y ≈ 0 → tiltRatio ≈ 0
                        // When vertical: Z ≈ 0, X/Y ≈ 9.8 → tiltRatio ≈ 1
                        val horizontal = sqrt(gravity[0].pow(2) + gravity[1].pow(2))
                        val vertical = abs(gravity[2])
                        tiltRatio = (horizontal / (horizontal + vertical + 0.001f))
                            .coerceIn(0f, 1f)
                    }

                    Sensor.TYPE_ROTATION_VECTOR -> {
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                        // Flat mapping — works best when phone is horizontal
                        SensorManager.remapCoordinateSystem(
                            rotationMatrix,
                            SensorManager.AXIS_X,
                            SensorManager.AXIS_Y,
                            flatMatrix
                        )
                        SensorManager.getOrientation(flatMatrix, flatOrientation)
                        val flatAzimuth = ((Math.toDegrees(flatOrientation[0].toDouble()) + 360) % 360).toFloat()

                        // Vertical mapping — works best when phone is upright
                        SensorManager.remapCoordinateSystem(
                            rotationMatrix,
                            SensorManager.AXIS_X,
                            SensorManager.AXIS_Z,
                            verticalMatrix
                        )
                        SensorManager.getOrientation(verticalMatrix, verticalOrientation)
                        val verticalAzimuth = ((Math.toDegrees(verticalOrientation[0].toDouble()) + 360) % 360).toFloat()

                        // Blend between flat and vertical based on tilt
                        // Use shortest path blending to avoid 0/360 wraparound issues
                        val azimuth = blendAngles(flatAzimuth, verticalAzimuth, tiltRatio)
                        trySend(azimuth)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationVector != null) {
            sensorManager.registerListener(
                listener, rotationVector, SensorManager.SENSOR_DELAY_UI
            )
        }
        if (accelerometer != null) {
            sensorManager.registerListener(
                listener, accelerometer, SensorManager.SENSOR_DELAY_UI
            )
        }

        awaitClose { sensorManager.unregisterListener(listener) }
    }

    // Blends two angles taking the shortest path around the circle
    private fun blendAngles(a: Float, b: Float, ratio: Float): Float {
        var diff = b - a
        // Normalize diff to -180..180 to always take the short way around
        while (diff > 180f) diff -= 360f
        while (diff < -180f) diff += 360f
        return ((a + diff * ratio) + 360f) % 360f
    }
}