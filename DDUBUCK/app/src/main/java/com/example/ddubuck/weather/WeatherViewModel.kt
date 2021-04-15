package com.example.ddubuck.weather


import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*

class WeatherViewModel : ViewModel() {
    val weatherRepository: WeatherRepository = WeatherRepository()
    private val APIKEY = "466c10bee7b689ae03ea8c7729ba0b1f"

    fun weatherInfo(lat: String, lon: String) = liveData(Dispatchers.IO) {
        val retrivedWeatherInfo = weatherRepository.getWeatherInfo(lat, lon, APIKEY)
        emit(retrivedWeatherInfo)
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    val uvRaysInfo = liveData(Dispatchers.IO) {
        val date = LocalDateTime.now().format(ofPattern("yyyyMMddkk")).toString()
        val retrivedUVRaysInfo = weatherRepository.getUVRaysInfo("WUS/HlSHsC8A/VZZlz1//4eSJiXcoh5gfR2EsoqdYGjhybgzun09KJKWZz+slJ85LzMZIIahT9UgeveNhce/yw==", 1, 1,  "JSON", "1100000000", date)
        emit(retrivedUVRaysInfo)
    }

    val dustInfo = liveData(Dispatchers.IO) {
        val retrivedDustInfo = weatherRepository.getDustInfo("마포구", "DAILY", 1, 1, "json", "WUS/HlSHsC8A/VZZlz1//4eSJiXcoh5gfR2EsoqdYGjhybgzun09KJKWZz+slJ85LzMZIIahT9UgeveNhce/yw==")
        emit(retrivedDustInfo)
    }
}