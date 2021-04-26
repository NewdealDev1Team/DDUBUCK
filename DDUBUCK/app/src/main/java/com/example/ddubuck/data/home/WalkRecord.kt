package com.example.ddubuck.data.home

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.JsonAdapter
import com.naver.maps.geometry.Coord
import com.naver.maps.geometry.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

//var 로 바꿔놓은건 임시용이라서 그럼

data class WalkRecord(
        //경로 정보 모음
        //경로
        val path: List<LatLng>,
        //고도평균
        val altitude: Double,
        //
        val speed: Double,
        //경과시간 (sec)
        val walkTime: Long,
        //발걸음 수
        val stepCount: Int,
        //거리 (m)
        val distance: Double,
        //산책 기록이 기록된 날짜
        val recordedDate: Date,
        //이름
        //바디
        //해시태그
        /*
        paths=[
            {x:123,y:123},{x:123,y:123}
        ]
        이 형식으로 보내기

         */

) {

    fun getCalorie(weight: Double) : Double {
        //https://github.com/IoT-Heroes/KidsCafeSolution_App/issues/2 참고해서 만들었습니다
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

    fun pathToMap() : List<HashMap<String,Any>> {
        val list : MutableList<HashMap<String,Any>> = mutableListOf()
        path.forEach { v ->
            val map =  hashMapOf<String, Any>()
            map["x"] = v.latitude
            map["y"] = v.longitude
            list.add(map)
        }
        return list
    }

    fun getJson() : JsonElement {
        val json = JsonObject()
        json.addProperty("path", Gson().toJson(pathToMap()))
        json.addProperty("altitude", altitude)
        json.addProperty("speed", speed)
        json.addProperty("walkTime", walkTime)
        json.addProperty("stepCount", stepCount)
        json.addProperty("distance", distance)
        return json
    }
}
