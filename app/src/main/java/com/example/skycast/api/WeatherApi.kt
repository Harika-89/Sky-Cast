package com.example.skycast.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/v1/current.json")
    suspend fun getWeather(
        @Query("key") apikey: String,
        @Query("q") city: String
    ): Response<WeatherModel>

    @GET("/v1/forecast.json")
    suspend fun getWeatherForecast(
        @Query("key") apikey: String,
        @Query("q") city: String,
        @Query("days") days: Int
    ): Response<ForecastResponse>
}