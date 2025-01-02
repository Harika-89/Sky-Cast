package com.example.skycast.api

data class WeatherModel(
    val current: Current,
    val location: Location,
    val forecast: Forecast
)

data class Astro(
    val sunrise: String,
    val sunset: String
)