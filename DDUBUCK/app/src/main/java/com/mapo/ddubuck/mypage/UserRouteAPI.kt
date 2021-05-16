package com.mapo.ddubuck.mypage

import com.mapo.ddubuck.weather.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserRouteAPI {
    @GET("RecommendData?")
    fun getUserRoute(
        @Query("userKey") userKey: String
    ): Call<UserRoute>

}