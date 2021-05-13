package com.mapo.ddubuck.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {
    val isSuccessfulLoginResponse = MutableLiveData<Boolean>()
    fun getResponseValue(value: Boolean) {
        isSuccessfulLoginResponse.value = value

    }
}