package com.example.ddubuck.login

import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @FormUrlEncoded
    @POST("set/User/InfoData")
    fun saveUserInfo(
            @Field("userKey") userKey: String,
            @Field("name") name: String,
            @Field("birth") birth: String,
            @Field("height") height: String,
            @Field("weight") weight: String,
            @Field("division") division: String
    ): Call<User>

    @GET("User/InfoData")
    fun getUserInfo(
            @Query("userKey") userKey: String
    ): Call<UserValidationInfo>

}