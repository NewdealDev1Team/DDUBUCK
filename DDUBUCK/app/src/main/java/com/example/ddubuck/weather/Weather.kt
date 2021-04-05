package com.example.ddubuck.weather

import com.example.ddubuck.login.Response

data class Weather(
        var resultCode: String,
        var message: String,
        var response: Response
)

//466c10bee7b689ae03ea8c7729ba0b1f