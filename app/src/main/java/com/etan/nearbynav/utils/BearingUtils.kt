package com.etan.nearbynav.utils

import kotlin.math.*

object BearingUtils {

    fun bearing(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double): Float {
        val dLon = Math.toRadians(toLng - fromLng)
        val lat1 = Math.toRadians(fromLat)
        val lat2 = Math.toRadians(toLat)
        val x = sin(dLon) * cos(lat2)
        val y = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        val bearing = Math.toDegrees(atan2(x, y))
        return ((bearing + 360) % 360).toFloat()
    }

    fun distanceKm(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(toLat - fromLat)
        val dLon = Math.toRadians(toLng - fromLng)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(fromLat)) * cos(Math.toRadians(toLat)) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    fun toCardinal(bearing: Float): String {
        val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        return directions[((bearing + 22.5) / 45).toInt() % 8]
    }

    val allDirections = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
}