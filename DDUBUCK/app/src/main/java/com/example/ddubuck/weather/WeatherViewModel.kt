package com.example.ddubuck.weather

import android.provider.Settings.Global.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.ddubuck.R
import kotlinx.coroutines.Dispatchers

class WeatherViewModel : ViewModel() {
    val weatherRepository: WeatherRepository = WeatherRepository()
    private val APIKEY = "466c10bee7b689ae03ea8c7729ba0b1f"

    val weatherInfo = liveData(Dispatchers.IO) {
        val retrivedWeatherInfo = weatherRepository.getWeatherInfo("37.563598", "126.909227", APIKEY)
        emit(retrivedWeatherInfo)
    }

    val uvRaysInfo = liveData(Dispatchers.IO) {
        val retrivedUVRaysInfo = weatherRepository.getUVRaysInfo("WUS/HlSHsC8A/VZZlz1//4eSJiXcoh5gfR2EsoqdYGjhybgzun09KJKWZz+slJ85LzMZIIahT9UgeveNhce/yw==", 1, 1,  "JSON", "1100000000", "2021041209")
        emit(retrivedUVRaysInfo)
    }

    val dustInfo = liveData(Dispatchers.IO) {
        val retrivedDustInfo = weatherRepository.getDustInfo("서대문구", "DAILY", 1, 1, "json", "WUS/HlSHsC8A/VZZlz1//4eSJiXcoh5gfR2EsoqdYGjhybgzun09KJKWZz+slJ85LzMZIIahT9UgeveNhce/yw==")
        emit(retrivedDustInfo)
    }
}