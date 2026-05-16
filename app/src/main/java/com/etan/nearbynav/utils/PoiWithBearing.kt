package com.etan.nearbynav.utils

data class PoiWithBearing(
    val name: String,
    val type: PoiType,
    val bearing: Float,
    val distanceKm: Double,
    val direction: String,
    val brand: String = ""
)