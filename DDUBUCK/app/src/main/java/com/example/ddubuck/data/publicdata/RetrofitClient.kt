package com.example.ddubuck.data.publicdata

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient{

    private const val BASE_URL = "http://3.37.6.181:3000/"

    val instance: Api by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        retrofit.create(Api::class.java)
    }
}


