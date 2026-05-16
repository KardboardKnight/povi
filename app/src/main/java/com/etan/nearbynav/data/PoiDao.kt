package com.etan.nearbynav.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PoiDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCities(cities: List<City>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGasStations(stations: List<GasStation>)

    @Query("SELECT * FROM cities")
    suspend fun getAllCities(): List<City>

    @Query("SELECT * FROM gas_stations")
    suspend fun getAllGasStations(): List<GasStation>

    @Query("SELECT COUNT(*) FROM cities")
    suspend fun cityCount(): Int

    @Query("SELECT COUNT(*) FROM gas_stations")
    suspend fun gasStationCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRestaurants(restaurants: List<Restaurant>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPointsOfInterest(pois: List<PointOfInterest>)

    @Query("SELECT * FROM restaurants")
    suspend fun getAllRestaurants(): List<Restaurant>

    @Query("SELECT * FROM points_of_interest WHERE type = :type")
    suspend fun getPointsOfInterestByType(type: String): List<PointOfInterest>

    @Query("SELECT * FROM points_of_interest")
    suspend fun getAllPointsOfInterest(): List<PointOfInterest>

    @Query("DELETE FROM restaurants")
    suspend fun deleteAllRestaurants()

    @Query("DELETE FROM points_of_interest")
    suspend fun deleteAllPointsOfInterest()

    @Query("DELETE FROM cities")
    suspend fun deleteAllCities()

    @Query("DELETE FROM gas_stations")
    suspend fun deleteAllGasStations()
}