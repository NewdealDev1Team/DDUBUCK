package com.example.ddubuck.ui.home

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.concurrent.timer


class HomeMapViewModel : ViewModel() {
    private lateinit var timer : Timer
    //시작 요청
    val isRecordStarted = MutableLiveData<Boolean>()
    fun recorderTrigger(v:Boolean) {
        isRecordStarted.value = v
    }

    //일시중지 요청
    //중지 요청
    //값 반환

}