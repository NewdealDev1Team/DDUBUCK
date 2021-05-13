package com.mapo.ddubuck.weather

class UVRays {
    var response = UVRaysResponse()
}

class UVRaysResponse {

    var header = UVRaysHeader()
    var body = UVRaysBody()
}

class UVRaysHeader {
    var resultCode: String? = null
    var resultMsg: String? = null
}

class UVRaysBody {
    var dataType: String? = null
    var items = Item()
    var pageNo: Int = 0
    var numOfRows: Int = 0
    var totalCount: Int = 0
}

class Item {
    var item = ArrayList<UVRaysItems>()
}

class UVRaysItems {
    var code: String? = null
    var areaNo: String? = null
    var date: String? = null
    var today: String? = null
    var tomorrow: String? = null
    var theDayAfterTomorrow: String? = null
}