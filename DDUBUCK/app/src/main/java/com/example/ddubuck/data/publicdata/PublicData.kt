package com.example.ddubuck.data.publicdata

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
    val publicRestArea: List<PublicRestArea>
)