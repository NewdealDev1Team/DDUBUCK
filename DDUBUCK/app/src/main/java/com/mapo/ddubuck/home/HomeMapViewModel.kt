package com.mapo.ddubuck.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapo.ddubuck.data.home.CourseItem
import com.naver.maps.geometry.LatLng


class HomeMapViewModel : ViewModel() {

    val isRecordStarted = MutableLiveData<Boolean>()
    fun recorderTrigger(v: Boolean) {
        isRecordStarted.value = v
    }

    val isRecordPaused = MutableLiveData<Boolean>()
    fun pauseTrigger(v:Boolean) {
        isRecordPaused.value=v
    }

    val isCourseWalk = MutableLiveData<Boolean>()
    fun courseWalkTrigger(v:Boolean) {
        isCourseWalk.value=v
    }

    val courseProgressPath = MutableLiveData<List<LatLng>>()
    fun passProgressData(v:List<LatLng>) {
        courseProgressPath.value = v
    }

    val coursePath = MutableLiveData<List<LatLng>>()
    fun passPathData(v:List<LatLng>) {
        coursePath.value = v
    }


    val walkDistance = MutableLiveData<Double>()
    fun recordDistance(v: Double) {
        walkDistance.value = v
    }

    val walkTime = MutableLiveData<Long>()
    fun recordTime(v: Long) {
        walkTime.postValue(v)
    }

    val walkCalorie = MutableLiveData<Double>()
    fun recordCalorie(v: Double) {
        walkCalorie.value = v
    }

    val position = MutableLiveData<LatLng>()
    fun recordPosition(v: LatLng) {
        position.value = v
    }

    val recommendPath = MutableLiveData<List<CourseItem>>()

    val vibrationControl = MutableLiveData<Boolean>()
    fun vibrate(v:Boolean) {
        vibrationControl.value = v
    }

    val walkState = MutableLiveData<Int>()

    //dp대응
    val bottomSheetHeight = MutableLiveData<Int>()


    //마이페이지 나의 기록
    val recordMywalk = MutableLiveData<Boolean>()


}
//boolean observer
//"stepCount": 210, 오늘 걸은 걸음 수
//"walkTime": 12, 산책시간
//"calorie": 50, 칼로리
//"completedCount": 1, 완주횟수

