package com.mapo.ddubuck

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    val toolbarTitle = MutableLiveData<String>()

    val showDrawer = MutableLiveData<Boolean>()
}