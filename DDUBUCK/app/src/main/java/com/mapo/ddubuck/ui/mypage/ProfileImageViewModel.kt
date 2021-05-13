package com.mapo.ddubuck.ui.mypage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileImageViewModel: ViewModel() {
    val isImageChanged = MutableLiveData<Boolean>()
    fun setisChangedValue(value: Boolean) {
        isImageChanged.value = value
    }
}