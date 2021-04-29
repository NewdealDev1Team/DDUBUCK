package com.example.ddubuck.data


import android.util.Log
import com.example.ddubuck.data.home.WalkRecord
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient{
    private const val BASE_URL = "http://3.37.6.181:3000/"
    //private const val BASE_URL = "https://ptsv2.com/t/lgx93-1619424022/"
    val mapInstance: MapAPI by lazy {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        retrofit.create(MapAPI::class.java)
    }
}


class RetrofitService {
    fun createRecordPost(walkRecord: WalkRecord){
        val map = hashMapOf<String, Any>()
        map["altitude"] = walkRecord.altitude
        map["speed"] = walkRecord.speed
        map["stepCount"] = walkRecord.stepCount
        map["distance"] = walkRecord.distance
        map["path"] = Gson().toJson(walkRecord.pathToMap())

        RetrofitClient.mapInstance.createPost(map)
                .enqueue(object : Callback<WalkRecord> {
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