package com.etan.nearbynav.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gas_stations")
data class GasStation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val brand: String,
    val lat: Double,
    val lng: Double
)