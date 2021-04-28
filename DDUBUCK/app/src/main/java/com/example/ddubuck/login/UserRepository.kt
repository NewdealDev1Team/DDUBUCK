package com.example.ddubuck.login

import com.example.ddubuck.weather.WeatherAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserRepository {
    val userInfo by lazy {
        Retrofit.Builder()
                .baseUrl("http://3.37.6.181:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserService::class.java)
    }
//
//    var userClient = userInfo.saveUserInfo()
//
//    suspend fun saveUserInfo(lat: String, lon: String, APPKEY: String) = userInfo

}