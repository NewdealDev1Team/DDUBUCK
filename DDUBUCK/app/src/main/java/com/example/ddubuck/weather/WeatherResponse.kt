package com.example.ddubuck.weather

class WeatherResponse() {

    var weather = ArrayList<Weather>()
    var main: Main? = null
    var wind: Wind? = null
    var sys: Sys? = null
}

class Weather {
    var id: Int = 0
    var main: String? = null
    var description: String? = null
    var icon: String? = null
}

class Main {
    var temp: Float = 0.toFloat()
    var humidity: Float = 0.toFloat()
    var pressure: Float = 0.toFloat()
    var temp_min: Float = 0.toFloat()
    var temp_max: Float = 0.toFloat()
}

class Wind {
    var speed: Float = 0.toFloat()
    var deg: Float = 0.toFloat()
}

class Sys {
    var country: String? = null
    var sunrise: Long = 0
    var sunset: Long = 0
}
