package com.etan.nearbynav.network


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import com.etan.nearbynav.data.City
import com.etan.nearbynav.data.GasStation
import com.etan.nearbynav.data.Restaurant
import com.etan.nearbynav.data.PointOfInterest
import com.etan.nearbynav.ui.screens.PoiToggles

object OverpassClient {

    private const val OVERPASS_URL = "https://overpass-api.de/api/interpreter"

    data class OverpassResult(
        val cities: List<City>,
        val stations: List<GasStation>,
        val restaurants: List<Restaurant>,
        val pointsOfInterest: List<PointOfInterest>
    )

    suspend fun query(lat: Double, lng: Double, radiusM: Int, toggles: com.etan.nearbynav.ui.screens.PoiToggles): OverpassResult =
        withContext(Dispatchers.IO) {
            val cities = if (toggles.cities) fetchCities(lat, lng, radiusM) else emptyList()
            val stations = if (toggles.gasStations) fetchStations(lat, lng, radiusM) else emptyList()
            val restaurants = if (toggles.restaurants) fetchAmenity(lat, lng, radiusM, "restaurant", "cuisine", "restaurant") else emptyList()
            val pois = mutableListOf<PointOfInterest>()
            if (toggles.hotels) pois += fetchPoi(lat, lng, radiusM, "tourism", "hotel", "hotel")
            if (toggles.hospitals) pois += fetchPoi(lat, lng, radiusM, "amenity", "hospital", "hospital")
            if (toggles.pharmacies) pois += fetchPoi(lat, lng, radiusM, "amenity", "pharmacy", "pharmacy")
            if (toggles.supermarkets) pois += fetchPoi(lat, lng, radiusM, "shop", "supermarket", "supermarket")
            if (toggles.atms) pois += fetchPoi(lat, lng, radiusM, "amenity", "atm", "atm")
            if (toggles.parks) pois += fetchPoi(lat, lng, radiusM, "leisure", "park", "park")
            if (toggles.parks) pois += fetchPoi(lat, lng, radiusM, "boundary", "national_park", "park")
            if (toggles.nationalForests) pois += fetchPoi(lat, lng, radiusM, "boundary", "forest", "national_forest")
            if (toggles.nationalForests) pois += fetchPoi(lat, lng, radiusM, "landuse", "forest", "national_forest")
            if (toggles.touristAttractions) pois += fetchPoi(lat, lng, radiusM, "tourism", "attraction", "tourist_attraction")
            if (toggles.touristAttractions) pois += fetchPoi(lat, lng, radiusM, "tourism", "viewpoint", "tourist_attraction")
            if (toggles.campgrounds) pois += fetchPoi(lat, lng, radiusM, "tourism", "camp_site", "campground")
            if (toggles.campgrounds) pois += fetchPoi(lat, lng, radiusM, "tourism", "caravan_site", "campground")
            OverpassResult(cities, stations, restaurants, pois)
        }

    private fun fetchCities(lat: Double, lng: Double, radiusM: Int): List<City> {
        val query = """
            [out:json][timeout:60];
            node["place"~"^(city|town|village)$"](around:$radiusM,$lat,$lng);
            out;
        """.trimIndent()

        val json = post(query)
        val elements = json.getJSONArray("elements")
        val cities = mutableListOf<City>()

        for (i in 0 until elements.length()) {
            val el = elements.getJSONObject(i)
            val tags = el.optJSONObject("tags") ?: continue
            val name = tags.optString("name").ifEmpty { continue }
            val elLat = el.optDouble("lat", Double.NaN)
            val elLng = el.optDouble("lon", Double.NaN)
            if (elLat.isNaN() || elLng.isNaN()) continue
            val population = tags.optString("population").toIntOrNull() ?: 0
            cities.add(City(name = name, lat = elLat, lng = elLng, population = population))
        }
        return cities
    }

    private fun fetchAmenity(lat: Double, lng: Double, radiusM: Int, amenity: String, tagKey: String, type: String): List<Restaurant> {
        val query = """
        [out:json][timeout:60];
        node["amenity"="$amenity"](around:$radiusM,$lat,$lng);
        out;
    """.trimIndent()
        val json = post(query)
        val elements = json.getJSONArray("elements")
        val results = mutableListOf<Restaurant>()
        for (i in 0 until elements.length()) {
            val el = elements.getJSONObject(i)
            val tags = el.optJSONObject("tags") ?: continue
            val name = tags.optString("name").ifEmpty { continue }
            val elLat = el.optDouble("lat", Double.NaN)
            val elLng = el.optDouble("lon", Double.NaN)
            if (elLat.isNaN() || elLng.isNaN()) continue
            val tag = tags.optString(tagKey).ifEmpty { type }
            results.add(Restaurant(name = name, cuisine = tag, lat = elLat, lng = elLng))
        }
        return results
    }

    private fun fetchPoi(lat: Double, lng: Double, radiusM: Int, key: String, value: String, type: String): List<PointOfInterest> {
        val query = """
        [out:json][timeout:60];
        (
          node["$key"="$value"](around:$radiusM,$lat,$lng);
          way["$key"="$value"](around:$radiusM,$lat,$lng);
        );
        out center;
    """.trimIndent()
        val json = post(query)
        val elements = json.getJSONArray("elements")
        val results = mutableListOf<PointOfInterest>()
        for (i in 0 until elements.length()) {
            val el = elements.getJSONObject(i)
            val tags = el.optJSONObject("tags") ?: continue
            val name = tags.optString("name").ifEmpty { value.replaceFirstChar { it.uppercase() } }
            val elLat: Double
            val elLng: Double
            if (el.optString("type") == "way") {
                val center = el.optJSONObject("center") ?: continue
                elLat = center.optDouble("lat", Double.NaN)
                elLng = center.optDouble("lon", Double.NaN)
            } else {
                elLat = el.optDouble("lat", Double.NaN)
                elLng = el.optDouble("lon", Double.NaN)
            }
            if (elLat.isNaN() || elLng.isNaN()) continue
            results.add(PointOfInterest(name = name, type = type, lat = elLat, lng = elLng))
        }
        return results
    }

    private fun fetchStations(lat: Double, lng: Double, radiusM: Int): List<GasStation> {
        val query = """
            [out:json][timeout:60];
            (
              node["amenity"="fuel"](around:$radiusM,$lat,$lng);
              way["amenity"="fuel"](around:$radiusM,$lat,$lng);
            );
            out center;
        """.trimIndent()

        val json = post(query)
        val elements = json.getJSONArray("elements")
        val stations = mutableListOf<GasStation>()

        for (i in 0 until elements.length()) {
            val el = elements.getJSONObject(i)
            val tags = el.optJSONObject("tags") ?: continue
            val name = tags.optString("name").ifEmpty {
                tags.optString("brand").ifEmpty { "Unnamed Station" }
            }
            val brand = tags.optString("brand").ifEmpty {
                tags.optString("operator").ifEmpty { "Unknown" }
            }
            val elLat: Double
            val elLng: Double
            if (el.optString("type") == "way") {
                val center = el.optJSONObject("center") ?: continue
                elLat = center.optDouble("lat", Double.NaN)
                elLng = center.optDouble("lon", Double.NaN)
            } else {
                elLat = el.optDouble("lat", Double.NaN)
                elLng = el.optDouble("lon", Double.NaN)
            }
            if (elLat.isNaN() || elLng.isNaN()) continue
            stations.add(GasStation(name = name, brand = brand, lat = elLat, lng = elLng))
        }
        return stations
    }

    private fun post(query: String): JSONObject {
        val encoded = "data=" + URLEncoder.encode(query, "UTF-8")
        val url = URL(OVERPASS_URL)
        val conn = url.openConnection() as java.net.HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        conn.setRequestProperty("User-Agent", "NearbyNav/1.0 (Android)")
        conn.connectTimeout = 30000
        conn.readTimeout = 90000
        conn.outputStream.use { it.write(encoded.toByteArray()) }
        val response = conn.inputStream.bufferedReader().readText()
        return JSONObject(response)
    }
}