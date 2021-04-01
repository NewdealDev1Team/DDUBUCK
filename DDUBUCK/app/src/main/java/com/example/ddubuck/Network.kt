package com.example.ddubuck

import com.google.gson.GsonBuilder
import com.naver.maps.geometry.LatLng
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//http://ptsv2.com/t/oowru-1617254707


object RetrofitClient {
    private var instance: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()
    // 서버 주소
    private const val BASE_URL = "http://ptsv2.com/t/oowru-1617254707/"

    // SingleTon
    fun getInstance(): Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        return instance!!
    }
}

// 서버에서 호출할 메서드를 선언하는 인터페이스
// POST 방식으로 데이터를 주고 받을 때 넘기는 변수는 Field라고 해야한다.
interface RetrofitService {

    @FormUrlEncoded
    @POST("WalkRecord/Single")
    fun requestSingle(
        @Field("path") path:List<LatLng>,
        @Field("altitudes") altitudes:List<Float>,
        @Field("speeds") speeds:List<Float>,
        @Field("walkTime") walkTime:Long,
        @Field("stepCount") stepCount:Int,
        @Field("distance") distance:Double
    ) : Call<WalkRecord>

}