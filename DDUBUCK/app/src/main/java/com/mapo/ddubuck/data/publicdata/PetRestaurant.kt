package com.example.ddubuck.data.publicdata

data class PetRestaurant(
    override val name : String,
    override val x: Double,
    override val y: Double,
    val address : String,
) : PublicDataForm