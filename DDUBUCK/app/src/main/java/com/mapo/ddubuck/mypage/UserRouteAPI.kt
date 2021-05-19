package com.mapo.ddubuck.mypage

import com.mapo.ddubuck.weather.WeatherResponse
import retrofit2.Call
import retrofit2.http.*

interface UserRouteAPI {
    @GET("RecommendData?")
    fun getUserRoute(
        @Query("userKey") userKey: String
    ): Call<UserRoute>

    @FormUrlEncoded
    @POST("RecommendData/Delete?")
    fun deleteUserRoute(
        @Field("userKey") userKey: String,
        @Field("created_at") createdAt: String
    ): Call<UserCourseDelete>
}