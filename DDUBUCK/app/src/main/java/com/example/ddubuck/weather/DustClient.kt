package com.example.ddubuck.weather

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class DustClient {
    companion object {
        private val retrofitClient: DustClient = DustClient()

        fun getInstance(): DustClient {
            return retrofitClient
        }
    }

    fun buildRetrofit(): DustAPI {
        var gson = GsonBuilder().setLenient().create()

        val retrofit: Retrofit? = Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/")
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

        return retrofit!!.create(DustAPI :: class.java)
    }
}