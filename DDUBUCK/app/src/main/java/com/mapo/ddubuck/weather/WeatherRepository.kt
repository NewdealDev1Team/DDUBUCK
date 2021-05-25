package com.mapo.ddubuck.weather

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class WeatherRepository {

    val gson = GsonBuilder().setLenient().create()

    var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val weatherService by lazy {
        Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(WeatherAPI::class.java)
    }

    val uvRaysService by lazy {
        Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/1360000/LivingWthrIdxService01/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(UVRaysAPI::class.java)

    }

    val dustService by lazy {
        Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(DustAPI::class.java)
    }


    var weatherClient = weatherService
    var uvRaysClient = uvRaysService
    var dustClient = dustService

    suspend fun getWeatherInfo(lat: String, lon: String, APPKEY: String) =
        weatherClient.getCurrentWeather(lat, lon, APPKEY)

    suspend fun getUVRaysInfo(
        serviceKey: String,
        numOfRows: Int,
        pageNo: Int,
        dataType: String,
        areaNo: String,
        time: String,
    ) =
        uvRaysClient.getCurrentUVRays(serviceKey, numOfRows, pageNo, dataType, areaNo, time)

    suspend fun getDustInfo(
        stationName: String,
        dataTerm: String,
        pageNo: Int,
        numOfRows: Int,
        returnType: String,
        serviceKey: String,
    ) = dustClient.getCurrentDust(stationName, dataTerm, pageNo, numOfRows, returnType, serviceKey)

}