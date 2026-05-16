package com.etan.nearbynav.data

object DatabaseSeeder {

    fun cities() = listOf(
        City(name = "Kansas City", lat = 39.0997, lng = -94.5786, population = 508090),
        City(name = "St. Louis", lat = 38.6270, lng = -90.1994, population = 301578),
        City(name = "Wichita", lat = 37.6872, lng = -97.3301, population = 397532),
        City(name = "Omaha", lat = 41.2565, lng = -95.9345, population = 486051),
        City(name = "Tulsa", lat = 36.1540, lng = -95.9928, population = 413066),
        City(name = "Springfield MO", lat = 37.2090, lng = -93.2923, population = 169176),
        City(name = "Des Moines", lat = 41.5868, lng = -93.6250, population = 214237),
        City(name = "Oklahoma City", lat = 35.4676, lng = -97.5164, population = 681054),
        City(name = "Lincoln NE", lat = 40.8136, lng = -96.7026, population = 295222),
        City(name = "Joplin", lat = 37.0842, lng = -94.5133, population = 51762)
    )

    fun gasStations() = listOf(
        GasStation(name = "Shell — I-70 & 40 Hwy", brand = "Shell", lat = 39.1150, lng = -94.4200),
        GasStation(name = "QT — Blue Ridge Cutoff", brand = "QuikTrip", lat = 39.0500, lng = -94.5200),
        GasStation(name = "Casey's — Lee's Summit", brand = "Casey's", lat = 38.9100, lng = -94.3800),
        GasStation(name = "Sinclair — Truman Rd", brand = "Sinclair", lat = 39.1000, lng = -94.5500),
        GasStation(name = "Phillips 66 — I-435 N", brand = "Phillips 66", lat = 39.2200, lng = -94.6100),
        GasStation(name = "Loves — I-70 Grain Valley", brand = "Love's", lat = 39.0100, lng = -94.1900),
        GasStation(name = "QT — Shawnee Mission", brand = "QuikTrip", lat = 39.0300, lng = -94.7200),
        GasStation(name = "Casey's — Liberty MO", brand = "Casey's", lat = 39.2500, lng = -94.4200),
        GasStation(name = "Shell — Lenexa KS", brand = "Shell", lat = 38.9600, lng = -94.7300),
        GasStation(name = "Pilot — I-70 Odessa", brand = "Pilot", lat = 38.9900, lng = -93.9500)
    )
}