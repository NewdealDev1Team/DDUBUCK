package com.mapo.ddubuck.data.home

data class CourseItem(
    val isFreeWalk : Boolean,
    val imgFile:String?,
    val title : String,
    val description : String,
    val walkRecord: WalkRecord) {

    fun compareTo(i : CourseItem) : Boolean {
        return title == i.title && walkRecord.path.size == i.walkRecord.path.size
    }
}