package com.mapo.ddubuck.badge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BadgeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is badge Fragment"
    }
    val text: LiveData<String> = _text
}