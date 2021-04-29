package com.example.ddubuck.data.publicdata

import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("getPublicData")
    fun getRests(): Call<publicData>

}
