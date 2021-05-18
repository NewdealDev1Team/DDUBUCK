package com.mapo.ddubuck.data.badge

import com.mapo.ddubuck.data.mypagechart.chartData



import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object RetrofitBadge{

    private const val BASE_URL = "http://3.37.6.181:3000/"

    val instance: BadgeService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(BadgeService::class.java)
    }

}

interface BadgeService{
//    http://3.37.6.181:3000/get/User/BadgeData?userKey=60574481
    @GET("get/User/BadgeData")
    fun getRestsBadge(@Query("userKey") userKey : Int) : Call<BadgeImage>

}
