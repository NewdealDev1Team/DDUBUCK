package com.example.ddubuck.data.home

import com.naver.maps.geometry.LatLng
import retrofit2.Call
import retrofit2.http.*

interface Api {

    //post로 서버에 데이터 넘기기
    @FormUrlEncoded
    @POST("setUserRecordData")
    fun createPost(
            @Field("userKey") user:String,
            @Field("record") record:String
    ): Call<WalkRecord>
}


//http://15.164.99.36:3000/getPublicData