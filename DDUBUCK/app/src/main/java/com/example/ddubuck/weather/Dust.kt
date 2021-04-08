package com.example.ddubuck.weather

class Dust {
    var response = DustResponse()
}

class DustResponse {
    var body = Body()
    var header = Header()
}

class Body {
    var toalCount: Int = 0
    var items = ArrayList<Items>()
    var pageNo: Int = 0
    var numOfRows: Int = 0
}

class Items {
    var so2Grade: String? = null
    var coFlag: String? = null
    var khaiValue: String? = null
    var so2Value: String? = null
    var coValue: String? = null
    var pm10Flag: String? = null
    var pm10Value: String? = null
    var khaiGrade: String? = null
    var no2Flag: String? = null
    var no2Grade: String? = null
    var o3Flag: String? = null
    var so2Flag: String? = null

    var dataTime: String? = null
    var coGrade: String? = null
    var no2Value: String? = null
    var pm10Grade: String? = null
    var o3Value: String? = null
}

class Header {
    var resultMsg: String? = null
    var resultCode: String? = null
}