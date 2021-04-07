package com.example.ddubuck.home

data class CourseItem(
        val isFreeWalk : Boolean,
        val title : String,
        val description : String,
        val walkRecord: WalkRecord)