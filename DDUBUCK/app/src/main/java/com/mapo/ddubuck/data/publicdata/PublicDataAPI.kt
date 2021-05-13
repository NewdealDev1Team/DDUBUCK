package com.example.ddubuck.data.publicdata

import retrofit2.Call
import retrofit2.http.*

interface PublicDataAPI {
    //http://3.37.6.181:3000/get/Master/PublicData?x=37.546037&y=126.955869
    @GET("get/Master/PublicData/")
    fun getResult(@Query("x") x : Double,
                  @Query("y") y : Double): Call<PublicData>
}
