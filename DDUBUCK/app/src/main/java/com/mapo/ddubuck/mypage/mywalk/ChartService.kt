
package com.mapo.ddubuck.data.mypagechart

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object RetrofitChart{

    private const val BASE_URL = "http://3.37.6.181:3000/"

    val instance: chartService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(chartService::class.java)
    }

}

interface chartService{
//    http://3.37.6.181:3000/get/User/Status/RecordData?userKey=60574481
    @GET("get/User/Status/RecordData")
    fun getRestsMypage(@Query("userKey") userKey : Int) : Call<My>

}
