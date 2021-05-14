package com.mapo.ddubuck.data.publicdata

import android.net.Uri
import com.mapo.ddubuck.data.home.CourseItem
import com.mapo.ddubuck.data.home.WalkRecord
import com.naver.maps.geometry.LatLng

/*
"recommendPath": [
        {
            "name": "홍익로~와우산로21길",
            "path": [
                {
                    "x": 37.556295,
                    "y": 126.924931
                },
                {
                    "x": 37.556295,
                    "y": 126.924931
                },
                {
                    "x": 37.556295,
                    "y": 126.924931
                },
                {
                    "x": 37.556048,
                    "y": 126.924555
                },
                {
                    "x": 37.555793,
                    "y": 126.924244
                },
                {
                    "x": 37.555393,
                    "y": 126.923557
                },
                {
                    "x": 37.554525,
                    "y": 126.922795
                },
                {
                    "x": 37.554357,
                    "y": 126.922452
                }
            ],
            "picture": "https://ddubuk.s3.ap-northeast-2.amazonaws.com/recommendFile/B1.svg",
            "walkTime": 600,
            "distance": 1000,
            "altitudes": 5,
            "division": "master",
            "dist": 2956.2863337343624
        }
    ]
 */


data class RecommendPathDTO(
    val name : String,
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
            name,
            description,
            this.toWalkRecord()
        )
    }
}

data class PathPoint(
    val x:Double,
    val y:Double
)