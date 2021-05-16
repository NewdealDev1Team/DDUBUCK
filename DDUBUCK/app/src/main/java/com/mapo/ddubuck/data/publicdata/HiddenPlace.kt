package com.mapo.ddubuck.data.publicdata

data class HiddenPlace(
    override val name : String,
    override val x: Double,
    override val y: Double,
    val picture : String,
) : PublicDataForm