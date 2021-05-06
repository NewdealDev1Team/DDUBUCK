
package com.example.ddubuck.data.mypagechart

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
    @GET("get/User/Status/RecordData?userKey=1677486124")
    fun getRestsMypage(
    ) : Call<chartData>

}
