package com.example.ddubuck.data

import com.example.ddubuck.data.home.WalkRecord
import com.google.gson.JsonArray
import com.naver.maps.geometry.LatLng
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface MapAPI {
    @FormUrlEncoded
    @POST("set/User/RecordData")
    fun createPost(@FieldMap params:HashMap<String, Any>): Call<WalkRecord>
}
