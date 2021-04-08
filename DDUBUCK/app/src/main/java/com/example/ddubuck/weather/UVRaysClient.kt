package com.example.ddubuck.weather

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UVRaysClient {
    companion object {
        private val retrofitClient: UVRaysClient = UVRaysClient()

        fun getInstance(): UVRaysClient {
            return retrofitClient
        }
    }

    fun buildRetrofit(): UVRaysAPI {
        var gson = GsonBuilder().setLenient().create()

        val retrofit: Retrofit? = Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/1360000/LivingWthrIdxService01/")
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

        return retrofit!!.create(UVRaysAPI :: class.java)
    }
}