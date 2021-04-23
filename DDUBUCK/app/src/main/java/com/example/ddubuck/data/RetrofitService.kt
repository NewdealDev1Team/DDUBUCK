package com.example.ddubuck.data


import android.util.Log
import com.example.ddubuck.data.home.WalkRecord
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient{

    private const val BASE_URL = "http://3.37.6.181:3000/ "

    val instance: Api by lazy {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        retrofit.create(Api::class.java)
    }
}

class RetrofitService {
    fun createPost(walkRecord: WalkRecord){
        RetrofitClient.instance.createPost(
                "foo",
                walkRecord.path,
                walkRecord.altitude,
                walkRecord.speed,
                walkRecord.walkTime,
                walkRecord.stepCount,
                walkRecord.distance, ).enqueue(object : Callback<WalkRecord> {
            override fun onResponse(
                call: Call<WalkRecord>,
                response: Response<WalkRecord>
            ) {
                val responseText = "Response code: ${response.code()}\n"+
                    "body: ${response.body()}\n"
                Log.e("response", responseText)
            }

            override fun onFailure(call: Call<WalkRecord>, t: Throwable) {
                Log.e("ERROR", t.localizedMessage)
            }

        })
    }
}