package com.mapo.ddubuck.mypage

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapo.ddubuck.weather.WeatherViewModel

class MypageViewModel: ViewModel() {

    val username = MutableLiveData<String>()
    fun setUserValue(value: String) {
        username.value = value
    }

    val isImageUpdate = MutableLiveData<Boolean>()
    val isRouteChanged = MutableLiveData<Boolean>()
}