package com.mapo.ddubuck.mypage

import com.google.gson.annotations.SerializedName

class UserRoute {
    var audit = ArrayList<Audit>()
    var complete = ArrayList<Complete>()
}

class AuditForDelete {
    var audit = ArrayList<Audit>()
}


class Audit {
    var title: String? = null
    var description: String? = null
    var picture: String? = null
    var path = ArrayList<Path>()
    var altitude: Double? = null
    var walkTime: Int? = null
    var distance: Double? = null
    var stepCount: Int? = null
    var calorie: Int? = null
    var created_at: String? = null

}

class Path {
    var x : Double? = null
    var y : Double? = null
}


class Complete {
    var title: String? = null
    var description: String? = null
    var picture: String? = null
    var path = ArrayList<Path>()
    var altitude: Double? = null
    var walkTime: Int? = null
    var distance: Double? = null
    var stepCount: Int? = null
    var calorie: Int? = null
    var created_at: String? = null
}