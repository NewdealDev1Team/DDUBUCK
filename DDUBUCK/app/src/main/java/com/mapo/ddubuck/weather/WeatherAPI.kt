package com.mapo.ddubuck.weather

import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherAPI {

    @GET("weather?")
    suspend fun getCurrentWeather(
            @Query("lat") lat: String,
            @Query("lon") lon: String,
            @Query("APPID") APPID: String
    ): WeatherResponse

}

