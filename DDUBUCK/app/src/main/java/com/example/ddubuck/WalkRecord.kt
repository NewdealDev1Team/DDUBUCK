package com.example.ddubuck

import com.naver.maps.geometry.LatLng

data class WorkRecord (
    //경로 정보 모음
    val path : List<LatLng>,
    val altitudes : List<Float>,
    val speeds : List<Float>,
    //경과시간 (1초씩 +)
    val walkTime : Long,
    //발걸음 수
    val footStepCount : Long,
    //거리 (m)
    val distance : Double,
) {
    fun getCalorie() : Double {
        val calorie = 0.0

        return calorie
    }

}