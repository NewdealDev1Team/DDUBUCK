package com.example.ddubuck.data.publicdata

data class CarFreeRoad(
    val name : String,
    val path : List<Path>,
    val time : String,
)

data class Path(
    val x: Double,
    val y: Double
)