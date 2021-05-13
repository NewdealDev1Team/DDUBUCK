package com.mapo.ddubuck.data.publicdata

data class PetCafe(
    override val name : String,
    override val x: Double,
    override val y: Double,
    val address : String,
) : PublicDataForm