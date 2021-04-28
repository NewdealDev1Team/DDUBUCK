package com.example.ddubuck.login

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserService {

    @FormUrlEncoded
    @POST("/set/User/InfoData")
    fun saveUserInfo(
            @Field("userKey") userKey: String,
            @Field("name") name: String,
            @Field("birth") birth: String,
            @Field("height") height: String,
            @Field("weight") weight: String,
            @Field("division") division: String

    ): Call<User>

}