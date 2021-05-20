package com.mapo.ddubuck.data.mypagechart

data class MyWalkRecordChartData(
    val weekStat: List<WeekStat>,
)

data class WeekStat(
    val name: String,
    val calorie: Double,
    val completedCount: Int,
    val created_at: String,
    val stepCount: Int,
    val walkTime: Int
)
