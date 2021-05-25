package com.mapo.ddubuck.data.home

data class CourseItem(
    val isFreeWalk : Boolean,
    val imgFile:String?,
    val title : String,
    val description : String,
    val walkRecord: WalkRecord) {

    fun compareTo(i : CourseItem) : Boolean {
        if(isFreeWalk) {
            return isFreeWalk == i.isFreeWalk
        }
        return title == i.title && walkRecord.walkTime == i.walkRecord.walkTime
    }
}