package com.example.ddubuck.data.home

import android.util.Log
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import java.util.*

//var 로 바꿔놓은건 임시용이라서 그럼

data class WalkRecord (
        //경로 정보 모음
        val path : List<LatLng>,
        val altitudes : List<Float>,
        val speeds : List<Float>,
        //경과시간 (sec)
        val walkTime : Long,
        //발걸음 수
        val stepCount : Int,
        //거리 (m)
        val distance : Double,
        //산책 기록이 기록된 날짜
        val recordedDate : Date,
) {

    fun getCalorie(weight:Double) : Double {
        //https://github.com/IoT-Heroes/KidsCafeSolution_App/issues/2 참고해서 만들었습니다
        val speed = speeds.average()
        var met = when(speed) {
            in 0.0..0.09 -> 0.0
            in 0.0..4.0 -> 2.0 // 느리게 걷기
            in 4.0..8.0 -> 3.8 // 보통 걷기
            in 8.0..12.0 -> 4.0 // 빠르게 걷기
            else -> 5.0 // 전력질주
        }
        if(speed.isNaN()) {
            met=0.0
        }
        return (met * (3.5 * weight * (walkTime /60.0))) * 0.001 * 5
    }

    fun toJson() : String {
        return Gson().toJson(
            WalkRecord(path, altitudes, speeds, walkTime, stepCount, distance, recordedDate))
    }


}
