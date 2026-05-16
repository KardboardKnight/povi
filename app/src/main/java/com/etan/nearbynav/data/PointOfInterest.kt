package com.etan.nearbynav.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points_of_interest")
data class PointOfInterest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,   // "hotel", "hospital", "pharmacy", "supermarket", "parking", "atm", "ev_charger"
    val lat: Double,
    val lng: Double
)