package com.example.ddubuck.data.home

import android.net.Uri

data class CourseItem(
        val isFreeWalk : Boolean,
        val imgFile:Uri,
        val title : String,
        val description : String,
        val walkRecord: WalkRecord)