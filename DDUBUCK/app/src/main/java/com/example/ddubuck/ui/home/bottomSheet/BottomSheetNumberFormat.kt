package com.example.ddubuck.ui.home.bottomSheet

import java.text.DecimalFormat

class BottomSheetNumberFormat {
    fun getFormattedDistance(v : Double) : String {
        return DecimalFormat("#.##km").format(v/1000)
    }

    fun getFormattedSpeed(v : Double) : String {
        return DecimalFormat("#.##km/h").format(v)
    }

    fun getFormattedAltitude(v : Double) : String {
        return DecimalFormat("#.##m").format(v)
    }

    fun getFormattedCalorie(v : Double) : String {
        return DecimalFormat("#.##kcal").format(v)
    }

}