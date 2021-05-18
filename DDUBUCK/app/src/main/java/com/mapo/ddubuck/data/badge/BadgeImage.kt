package com.mapo.ddubuck.data.badge

data class BadgeImage(
    val anniversary: List<Anniversary>,
    val challenge: List<Challenge>,
    val special: List<Special>
)

data class Anniversary(
    val image: List<String>,
    val title: List<String>
)

data class Challenge(
    val image: List<String>,
    val title: List<String>
)

data class Special(
    val image: List<String>,
    val title: List<String>
)