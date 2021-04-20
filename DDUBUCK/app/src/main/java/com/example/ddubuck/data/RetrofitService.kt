package com.example.ddubuck.data


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.data.home.Api
import com.example.ddubuck.data.home.WalkRecord
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

class RetrofitService {

    fun createPost(walkRecord: WalkRecord){
        RetrofitClient.instance.createPost("foo",walkRecord.toJson()).enqueue(object : Callback<WalkRecord> {
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