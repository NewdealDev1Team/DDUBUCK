package com.example.ddubuck.data

import com.example.ddubuck.data.home.WalkRecord
import com.google.gson.JsonArray
import com.naver.maps.geometry.LatLng
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    //post로 서버에 데이터 넘기기
    //TODO record 밖으로 빼내기
    @JvmSuppressWildcards
    //@FormUrlEncoded
    //@POST("set/User/RecordData")
    @POST("post")
    fun createPost(@FieldMap params:HashMap<String, Any>): Call<WalkRecord>
}


//http://15.164.99.36:3000/getPublicData