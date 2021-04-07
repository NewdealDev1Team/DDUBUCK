package com.example.ddubuck.weather

import com.google.gson.annotations.SerializedName

class Dust {
    @SerializedName("response") var response = DustResponse()
}

class DustResponse {
    @SerializedName("body") var body = Body()
    @SerializedName("header") var header = Header()
}

class Body {
    @SerializedName("toalCount") var toalCount: Int = 0
    @SerializedName("items") var items = ArrayList<Items>()
    @SerializedName("pageNo") var pageNo: Int = 0
    @SerializedName("numOfRows") var numOfRows: Int = 0
}

class Items {
    @SerializedName("so2Grade") var so2Grade: String? = null
    @SerializedName("coFlag") var coFlag: String? = null
    @SerializedName("khaiValue") var khaiValue: String? = null
    @SerializedName("so2Value") var so2Value: String? = null
    @SerializedName("coValue") var coValue: String? = null
    @SerializedName("pm10Flag") var pm10Flag: String? = null
    @SerializedName("pm10Value") var pm10Value: String? = null
    @SerializedName("khaiGrade") var khaiGrade: String? = null
    @SerializedName("no2Flag") var no2Flag: String? = null
    @SerializedName("no2Grade") var no2Grade: String? = null

    @SerializedName("o3Flag") var o3Flag: String? = null
    @SerializedName("so2Flag") var so2Flag: String? = null

    @SerializedName("dataTime") var dataTime: String? = null
    @SerializedName("coGrade") var coGrade: String? = null
    @SerializedName("no2Value") var no2Value: String? = null
    @SerializedName("pm10Grade") var pm10Grade: String? = null
    @SerializedName("o3Value") var o3Value: String? = null
}

class Header {
    @SerializedName("resultMsg") var resultMsg: String? = null
    @SerializedName("resultCode") var resultCode: String? = null
}