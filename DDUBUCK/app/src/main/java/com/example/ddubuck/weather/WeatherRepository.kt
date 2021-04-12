package com.example.ddubuck.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

class WeatherRepository {

    val gson = GsonBuilder().setLenient().create()

    val weatherService by lazy {
        Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherAPI::class.java)
    }

    val uvRaysService by lazy {
        Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/1360000/LivingWthrIdxService01/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(UVRaysAPI::class.java)

    }

    val dustService by lazy {
        Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(DustAPI::class.java)
    }


    var weatherClient = weatherService
    var uvRaysClient = uvRaysService
    var dustClient = dustService

    suspend fun getWeatherInfo(lat: String, lon: String, APPKEY: String) = weatherClient.getCurrentWeather(lat, lon, APPKEY)
    suspend fun getUVRaysInfo(serviceKey: String, numOfRows: Int, pageNo: Int, dataType: String, areaNo: String, time: String) =
            uvRaysClient.getCurrentUVRays(serviceKey, numOfRows, pageNo, dataType, areaNo, time)
    suspend fun getDustInfo(stationName: String, dataTerm: String, pageNo: Int, numOfRows: Int, returnType: String, serviceKey: String) = dustClient.getCurrentDust(stationName, dataTerm, pageNo, numOfRows, returnType, serviceKey)

}