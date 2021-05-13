package com.mapo.ddubuck.data


import android.util.Log
import com.mapo.ddubuck.data.home.WalkRecord
import com.mapo.ddubuck.data.publicdata.PublicDataAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File

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
    //ScalarsConverterFactory.create()

    val recordInstance: MapAPI by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        retrofit.create(MapAPI::class.java)
    }


    val publicDataInstance : PublicDataAPI by lazy {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(PublicDataAPI::class.java)
    }
}


class RetrofitService {

    fun createRecord(userKey : String, walkRecord: WalkRecord){
        val map = hashMapOf<String, Any>()
        map["userKey"] = userKey
        map["altitude"] = walkRecord.altitude
        map["speed"] = walkRecord.speed
        map["stepCount"] = walkRecord.stepCount
        map["distance"] = walkRecord.distance
        map["path"] = Gson().toJson(walkRecord.pathToMap())
        map["calorie"] = walkRecord.getCalorie(65.0)

        RetrofitClient.mapInstance.createPost(map)
                .enqueue(object : Callback<WalkRecord> {
            override fun onResponse(
                call: Call<WalkRecord>,
                response: Response<WalkRecord>
            ) {
                val responseText = "Response code: ${response.code()}\n"+
                    "body: ${response.body()}\n" + response.message() + "${response.headers()}"
                println(responseText)
            }

            override fun onFailure(call: Call<WalkRecord>, t: Throwable) {
                Log.e("ERROR", t.localizedMessage)
            }
        })
    }

    fun addAdditionalInfo(userKey:String,title:String, description:String, imgPath: String) {
        val file = File(imgPath)
        var fileName = imgPath.replace("@", "").replace(".", "")

        fileName = "$fileName.png"

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val imgFile: MultipartBody.Part =
            MultipartBody.Part.createFormData("imgFile", fileName, requestBody)

        Log.d("DATA", "$title, $description, $imgPath, ${imgFile.body()}")

        RetrofitClient.recordInstance.addAdditionalInfo(
            userKey,
            title,
            description,
            imgFile,
        ).enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    val responseText = "Response code: ${response.code()}\n"+
                            "body: ${response.body()}\n" + response.message() + "${response.headers()}"
                    println(responseText)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("ERROR", t.localizedMessage)
                }
            })
    }

}