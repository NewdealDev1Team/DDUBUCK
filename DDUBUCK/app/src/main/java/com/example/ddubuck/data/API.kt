package com.example.ddubuck.data

import com.example.ddubuck.data.home.WalkRecord
import com.naver.maps.geometry.LatLng
import retrofit2.Call
import retrofit2.http.*

interface Api {

    //post로 서버에 데이터 넘기기
    //TODO record 밖으로 빼내기
    @FormUrlEncoded
    @POST("setUserRecordData")
    fun createPost(
            @Field("userKey")userKey:String,
            @Field("path") path:List<LatLng>,
            @Field("altitudes") altitudes:List<Float>,
            @Field("speeds") speeds:List<Float>,
            @Field("walkTime") walkTime:Long,
            @Field("stepCount") stepCount:Int,
            @Field("distance") distance:Double,
    ): Call<WalkRecord>
}


//http://15.164.99.36:3000/getPublicData