package com.mapo.ddubuck.data.publicdata

import com.naver.maps.geometry.LatLng

data class HiddenChallenge(
    val title : String,
    val x: Double,
    val y: Double,
    val picture : String,
    /**300미터 내 힌트 표시여부**/
    var firstHintShown : Boolean = false,
    /**200미터 내 힌트 표시여부**/
    var secondHintShown : Boolean = false,
    /**100미터 내 힌트 표시여부**/
    var thirdHintShown : Boolean = false,
) {
    fun selector(p:LatLng): Double {
        return p.distanceTo(this.pos())
    }

    fun pos():LatLng{
        return LatLng(x,y)
    }
}