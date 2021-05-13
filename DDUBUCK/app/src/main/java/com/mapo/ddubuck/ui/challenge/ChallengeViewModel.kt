package com.mapo.ddubuck.ui.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChallengeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is challenge Fragment"
    }
    val text: LiveData<String> = _text
}