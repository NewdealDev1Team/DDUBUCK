package com.example.ddubuck.weather


import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class WeatherViewModel : ViewModel() {
    val weatherRepository: WeatherRepository = WeatherRepository()
    private val APIKEY = "466c10bee7b689ae03ea8c7729ba0b1f"

    fun weatherInfo(lat: String, lon: String) = liveData(Dispatchers.IO) {
        val retrivedWeatherInfo = weatherRepository.getWeatherInfo(lat, lon, APIKEY)
        emit(retrivedWeatherInfo)
    }

    val uvRaysInfo = liveData(Dispatchers.IO) {
        val retrivedUVRaysInfo = weatherRepository.getUVRaysInfo("WUS/HlSHsC8A/VZZlz1//4eSJiXcoh5gfR2EsoqdYGjhybgzun09KJKWZz+slJ85LzMZIIahT9UgeveNhce/yw==", 1, 1,  "JSON", "1100000000", "2021041409")
        emit(retrivedUVRaysInfo)
    }

    val dustInfo = liveData(Dispatchers.IO) {
        val retrivedDustInfo = weatherRepository.getDustInfo("마포구", "DAILY", 1, 1, "json", "WUS/HlSHsC8A/VZZlz1//4eSJiXcoh5gfR2EsoqdYGjhybgzun09KJKWZz+slJ85LzMZIIahT9UgeveNhce/yw==")
        emit(retrivedDustInfo)
    }
}