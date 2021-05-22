package com.mapo.ddubuck.data.publicdata

import com.naver.maps.geometry.LatLng

data class HiddenChallenge(
    val title : String,
    val x: Double,
    val y: Double,
    val picture : String,
) {
    fun selector(p:LatLng): Double {
        return p.distanceTo(this.pos())
    }

    fun pos():LatLng{
        return LatLng(x,y)
    }
}