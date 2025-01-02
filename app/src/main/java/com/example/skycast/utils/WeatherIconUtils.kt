package com.example.skycast.utils

import com.example.skycast.R

fun getWeatherIcon(condition: String): Int {
    return when (condition.lowercase()) {
        "sunny" -> R.drawable.sun
        "rainy" -> R.drawable.rain
        "cloudy" -> R.drawable.cloudy_day
        "snow" -> R.drawable.snow
        "clear" -> R.drawable.crescent_moon
        "mist" -> R.drawable.mist
        "partly_cloudy" -> R.drawable.partly
        "overcast" -> R.drawable.clouds
        "patchy_rain_possible" -> R.drawable.rain
        "light_drizzle" -> R.drawable.drizzle
        "heavy_snow" -> R.drawable.heavy_snow
        "ice_pallets" -> R.drawable.ice_pallet
        "torrential_rain_shower" -> R.drawable.torrential_rain_shower
        else -> R.drawable.cloud
    }
}