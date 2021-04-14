package com.example.ddubuck.ui.home

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.concurrent.timer


class HomeMapViewModel : ViewModel() {
    private lateinit var timer : Timer
    val isRecordStarted = MutableLiveData<Boolean>()
    fun recorderTrigger(v:Boolean) {
        isRecordStarted.value = v
    }


    val walkDistance = MutableLiveData<Double>()
    fun recordDistance(v:Double) {
        walkDistance.value = v
    }

    val walkTime = MutableLiveData<Long>()
    fun recordTime(v:Long) {
        walkTime.postValue(v)
    }

    val walkCalorie = MutableLiveData<Double>()
    fun recordCalorie(v:Double) {
        walkCalorie.value = v
    }

    //값 반환

}