package com.mapo.ddubuck.data.home

import android.net.Uri
import com.google.gson.Gson
import org.json.JSONObject

data class CourseItem(
    val isFreeWalk : Boolean,
    val imgFile:String?,
    val title : String,
    val description : String,
    val walkRecord: WalkRecord) {

    fun compareTo(i : CourseItem) : Boolean {
        return title == i.title
    }

    fun toJsonString() : String {
        return Gson().toJson(this@CourseItem)
    }
}