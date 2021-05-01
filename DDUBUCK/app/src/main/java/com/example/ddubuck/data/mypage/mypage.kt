package com.example.ddubuck.data.mypage

import com.example.ddubuck.data.publicdata.publicData
import retrofit2.Call
import retrofit2.http.GET

interface mypage {
    @GET("get/User/Status/RecordData")
    fun getRests(): Call<publicData>
}

//http://3.37.6.181:3000/get/User/Status/RecordData