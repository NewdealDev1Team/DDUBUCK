package com.example.ddubuck.login

import android.provider.Settings.System.getString
import com.example.ddubuck.R
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverAPI {
    @GET("v1/nid/me")
    fun getUserInfo(
            @Header("Authorization") Authorization: String,
    ): Call<UserInfo>

}
