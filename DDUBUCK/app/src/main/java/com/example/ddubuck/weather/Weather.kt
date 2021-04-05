package com.example.ddubuck.weather

import com.example.ddubuck.login.Response

data class Weather(
        var resultCode: String,
        var message: String,
        var response: Response
)
