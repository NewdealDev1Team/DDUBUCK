package com.example.ddubuck.weather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UVRaysAPI {
    @GET("getUVIdx?")
    fun getCurrentUVRays(
            @Query("serviceKey") serviceKey: String,
            @Query("numOfRows") numOfRows: Int,
            @Query("pageNo") pageNo: Int,
            @Query("dataType") dataType: String,
            @Query("areaNo") areaNo: String,
            @Query("time") time: String
    ): Call<UVRays>
}
