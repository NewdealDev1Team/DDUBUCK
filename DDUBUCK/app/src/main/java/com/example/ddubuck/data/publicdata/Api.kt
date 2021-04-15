package com.example.ddubuck.data.publicdata


import com.example.ddubuck.data.publicdata.Cafe
import com.example.ddubuck.data.publicdata.publicData
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("getPublicData")
    fun getRests(): Call<publicData>

//    @GET("getToiletData")
//    fun getToliets(): Call<ArrayList<PublicRestroomItem>>

    //post로 서버에 데이터 넘기기
//    @FormUrlEncoded
//    @POST("setLocationData")
//    fun createPost(
//        @Field("user") user:String,
//        @Field("latitude") latitude:Double,
//        @Field("longitude") longitude:Double
//    ): Call<CreatePostResponse>
}
