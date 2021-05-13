package com.mapo.ddubuck.data

import com.mapo.ddubuck.data.home.WalkRecord
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface MapAPI {
    @FormUrlEncoded
    @POST("set/User/RecordData")
    fun createPost(@FieldMap params:HashMap<String, Any>): Call<WalkRecord>

    @Multipart
    @POST("set/User/RecordData/ImageUpload")
    fun addAdditionalInfo(
        @Part("userKey") userKey:String,
        @Part("title") title:String,
        @Part("description") description:String,
        @Part imgFile: MultipartBody.Part
    ): Call<String>
}

