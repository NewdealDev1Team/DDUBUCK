package com.mapo.ddubuck.challenge

data class Challenge(
    var image: Int,
    var title: String,
    var infoText: String,
    var section: Int,
    var position: Int

) {
    fun compareTo(challenge: Challenge): Boolean {
        return infoText == challenge.infoText
    }
}

