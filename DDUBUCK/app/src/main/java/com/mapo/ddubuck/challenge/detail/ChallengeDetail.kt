package com.mapo.ddubuck.challenge.detail

data class ChallengeDetail (

    var title: String,
    var image: Int
//    var distance : ArrayList<ChallengeDistance>,
//    var walkingCount : ArrayList<ChallengeWalkingCount>,
//    var courseComplete : ArrayList<ChallengeCourseComplete>,
//    var place : ArrayList<ChallengePlace>,
//    var weather : ArrayList<ChallengeWeather>,
//    var petDistance : ArrayList<ChallengePetDistance>,
//    var petComplete : ArrayList<ChallengePetComplete>
)

class ChallengeDistance {
    var title: String? = null
    var image: Int? = null
}

class ChallengeWalkingCount {
    var title: String? = null
    var image: Int? = null
}

class ChallengeCourseComplete {
    var title: String? = null
    var image: Int? = null
}

class ChallengePlace {
    var title: String? = null
    var image: Int? = null
}

class ChallengeWeather {
    var title: String? = null
    var image: Int? = null
}

class ChallengePetDistance {
    var title: String? = null
    var image: Int? = null
}

class ChallengePetComplete {
    var title: String? = null
    var image: Int? = null
}