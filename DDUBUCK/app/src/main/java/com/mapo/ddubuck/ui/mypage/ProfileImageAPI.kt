package com.mapo.ddubuck.ui.mypage

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileImageAPI {

    @Multipart
    @POST("set/User/InfoData/ImageUpload")
    fun sendProfileImage(
        @Part("userKey") userKey: String,
        @Part profileImageFile: MultipartBody.Part
    ): Call<String>


}