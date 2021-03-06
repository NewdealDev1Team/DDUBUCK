package com.mapo.ddubuck.userinfo

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserInfoService {

    @FormUrlEncoded
    @POST("set/User/Status/InfoData")
    fun saveUserBodyInfo(
            @Field("userKey") userKey: String,
            @Field("birth") birth: String,
            @Field("height") height: Double,
            @Field("weight") weight: Double,
    ): Call<UserBody>
}