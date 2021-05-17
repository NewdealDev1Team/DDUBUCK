package com.mapo.ddubuck.challenge.detail

import com.mapo.ddubuck.login.UserInfo
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ChallengeAPI {
    @GET("ChallengeData")
    fun getChallengeImage(
        @Query("userKey") userKey: String,
    ): Call<ChallengeDetail>

}
