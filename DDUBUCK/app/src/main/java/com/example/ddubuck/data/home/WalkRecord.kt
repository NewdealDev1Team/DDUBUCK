package com.example.ddubuck.data.home

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import java.util.*

class WalkRecord(
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
    //이름
    //바디
    //해시태그
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(LatLng.CREATOR)!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readDouble(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(path)
        parcel.writeDouble(altitude)
        parcel.writeDouble(speed)
        parcel.writeLong(walkTime)
        parcel.writeInt(stepCount)
        parcel.writeDouble(distance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WalkRecord> {
        override fun createFromParcel(parcel: Parcel): WalkRecord {
            return WalkRecord(parcel)
        }

        override fun newArray(size: Int): Array<WalkRecord?> {
            return arrayOfNulls(size)
        }
    }


    fun getCalorie(weight: Double) : Double {
        //https://github.com/IoT-Heroes/KidsCafeSolution_App/issues/2 참고해서 만들었습니다
        var met = when(speed) {
            in 0.0..0.09 -> 0.0
            in 0.09..4.0 -> 2.0 // 느리게 걷기
            in 4.0..8.0 -> 3.8 // 보통 걷기
            in 8.0..12.0 -> 4.0 // 빠르게 걷기
            else -> 5.0 // 전력질주
        }
        if(speed.isNaN()) {
            met=0.0
        }
        return (met * (3.5 * weight * (walkTime /60.0))) * 0.001 * 5
    }

    fun pathToMap() : List<HashMap<String, Any>> {
        val list : MutableList<HashMap<String, Any>> = mutableListOf()
        path.forEach { v ->
            val map =  hashMapOf<String, Any>()
            map["x"] = v.latitude
            map["y"] = v.longitude
            list.add(map)
        }
        return list
    }
}
