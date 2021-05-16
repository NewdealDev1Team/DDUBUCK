package com.mapo.ddubuck.data.publicdata

import android.net.Uri
import com.mapo.ddubuck.data.home.CourseItem
import com.mapo.ddubuck.data.home.WalkRecord
import com.naver.maps.geometry.LatLng

data class RecommendPathDTO(
    val title : String,
    val description:String,
    val picture : String,
    val walkTime : Long,
    val distance : Double,
    val altitudes : Double,
    val path : List<PathPoint>
) {
    fun toWalkRecord() : WalkRecord{
        val latLngPath = mutableListOf<LatLng>()
        for (point in path) {
            latLngPath.add(LatLng(point.x, point.y))
        }
        return WalkRecord(
            latLngPath,
            altitudes,
            0.0,
            walkTime,
            0,
            distance,

        )
    }

    fun toCourseItem() : CourseItem{
        return CourseItem(
            false,
            Uri.parse(picture),
            title,
            description,
            this.toWalkRecord()
        )
    }
}

data class PathPoint(
    val x:Double,
    val y:Double
)