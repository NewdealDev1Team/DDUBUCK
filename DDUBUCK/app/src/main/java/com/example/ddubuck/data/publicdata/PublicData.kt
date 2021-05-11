package com.example.ddubuck.data.publicdata

import com.example.ddubuck.data.home.CourseItem
import com.example.ddubuck.data.home.WalkRecord

interface PublicDataForm {
    val name : String
    val x : Double
    val y : Double
}

data class PublicData(
    val cafe: List<Cafe>,
    val carFreeRoad: List<CarFreeRoad>,
    val petCafe: List<PetCafe>,
    val petRestaurant: List<PetRestaurant>,
    val publicToilet: List<PublicToilet>,
    val publicRestArea: List<PublicRestArea>,
    val recommendPath : List<CourseItem>,
)