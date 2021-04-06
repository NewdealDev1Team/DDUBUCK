package com.example.ddubuck.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherClient {
    companion object {
        private val retrofitClient: WeatherClient = WeatherClient()

        fun getInstance(): WeatherClient {
            return retrofitClient
        }
    }

    fun buildRetrofit(): WeatherAPI {
        val retrofit: Retrofit? = Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit!!.create(WeatherAPI :: class.java)
    }
}