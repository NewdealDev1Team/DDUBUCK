package com.example.ddubuck.weather

import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherAPI {

    @GET("weather?")
    fun getCurrentWeather(
            @Query("lat") lat: String,
            @Query("lon") lon: String,
            @Query("APPID") APPID: String
    ): Call<WeatherResponse>



}

