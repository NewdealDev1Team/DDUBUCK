package com.example.ddubuck.weather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DustAPI {
    @GET("getMsrstnAcctoRltmMesureDnsty?")
    suspend fun getCurrentDust(
            @Query("stationName") stationName: String,
            @Query("dataTerm") dataTerm: String,
            @Query("pageNo") pageNo: Int,
            @Query("numOfRows") numOfRows: Int,
            @Query("returnType") returnType: String,
            @Query("serviceKey") serviceKey: String
    ): Dust
}