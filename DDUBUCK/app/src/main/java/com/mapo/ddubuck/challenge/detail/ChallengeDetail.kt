package com.mapo.ddubuck.challenge.detail

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

class ChallengeDetail {
    var distance = ArrayList<ChallengeDistance>()
    var stepCount = ArrayList<ChallengeWalkingCount>()
    var course = ArrayList<ChallengeCourseComplete>()
    var place = ArrayList<ChallengePlace>()
    var weather = ArrayList<ChallengeWeather>()
    @SerializedName("pet_distance")
    var petDistance = ArrayList<ChallengePetDistance>()
    @SerializedName("pet_course")
    var petCourse = ArrayList<ChallengePetComplete>()

}

class ChallengeDistance {
    var title: List<String>? = null
    var image: List<String>? = null
}

class ChallengeWalkingCount {
    var title: List<String>? = null
    var image: List<String>? = null
}

class ChallengeCourseComplete {
    var title: List<String>? = null
    var image: List<String>? = null
}

class ChallengePlace {
    var title: List<String>? = null
    var image: List<String>? = null
}

class ChallengeWeather {
    var title: List<String>? = null
    var image: List<String>? = null
}

class ChallengePetDistance {
    var title: List<String>? = null
    var image: List<String>? = null
}

class ChallengePetComplete {
    var title: List<String>? = null
    var image: List<String>? = null
}