package com.example.ddubuck

import android.content.ContentValues.TAG
import android.util.Log
import com.google.gson.GsonBuilder
import com.naver.maps.geometry.LatLng
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//임시 네트워크 파일
//나중에 사용할 POST, GET을 테스트하기 위한 파일임

object RetrofitClient {
    private lateinit var instance: Retrofit
    private val gson = GsonBuilder().setLenient().create()
    // 서버 주소
    private const val BASE_URL = "https://ptsv2.com/t/469y9-1617327164/"

    // SingleTon
    fun getInstance(walkRecord: WalkRecord): Retrofit {
        instance = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = instance.create(RetrofitService::class.java)
        val callrequest = api.requestSingle("1", walkRecord)
        callrequest.enqueue(object : Callback<WalkRecord> {
            override fun onResponse(
                    call: Call<WalkRecord>,
                    response: Response<WalkRecord>
            ) {
                Log.d(TAG, "성공 : ${response.raw()}")
            }

            override fun onFailure(call: Call<WalkRecord>, t: Throwable) {
                Log.d(TAG, "실패 : $t")
            }
        })
        return instance
    }
}

// 서버에서 호출할 메서드를 선언하는 인터페이스
// POST 방식으로 데이터를 주고 받을 때 넘기는 변수는 Field라고 해야한다.
interface RetrofitService {

    @FormUrlEncoded
    @POST("post")
    fun requestSingle(
            @Field("key") key:String,
            @Body rec:WalkRecord,
    ) : Call<WalkRecord>

}