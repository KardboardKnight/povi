package com.etan.nearbynav.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.etan.nearbynav.data.AppDatabase
import com.etan.nearbynav.data.City
import com.etan.nearbynav.data.GasStation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object DatabaseImporter {

    suspend fun importDatabase(context: Context, uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Copy the uploaded file to a temp location
                val tempFile = File(context.cacheDir, "import_temp.db")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                // Open it as a plain SQLite database
                val importDb = SQLiteDatabase.openDatabase(
                    tempFile.absolutePath,
                    null,
                    SQLiteDatabase.OPEN_READONLY
                )

                // Read cities
                val cities = mutableListOf<City>()
                val cityCursor = importDb.rawQuery("SELECT name, lat, lng, population FROM cities", null)
                while (cityCursor.moveToNext()) {
                    cities.add(City(
                        name = cityCursor.getString(0),
                        lat = cityCursor.getDouble(1),
                        lng = cityCursor.getDouble(2),
                        population = cityCursor.getInt(3)
                    ))
                }
                cityCursor.close()

                // Read gas stations
                val stations = mutableListOf<GasStation>()
                val stationCursor = importDb.rawQuery("SELECT name, brand, lat, lng FROM gas_stations", null)
                while (stationCursor.moveToNext()) {
                    stations.add(GasStation(
                        name = stationCursor.getString(0),
                        brand = stationCursor.getString(1),
                        lat = stationCursor.getDouble(2),
                        lng = stationCursor.getDouble(3)
                    ))
                }
                stationCursor.close()
                importDb.close()
                tempFile.delete()

                // Clear existing data and insert the new records into Room's database
                val dao = AppDatabase.get(context).poiDao()
                dao.deleteAllCities()
                dao.deleteAllGasStations()
                dao.insertCities(cities)
                dao.insertGasStations(stations)

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}