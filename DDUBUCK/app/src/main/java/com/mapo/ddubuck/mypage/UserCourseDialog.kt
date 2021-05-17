package com.mapo.ddubuck.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isInvisible
import com.bumptech.glide.Glide
import com.mapo.ddubuck.R

class UserCourseDialog(
    private val title: String,
    private val photo: String,
    private val text: String,
    private val time: String,
    private val distance: String,
    private val height: String,
    private val result: String,
    owner: Activity,
) : Dialog(owner) {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_course_view)
        val titleTV = findViewById<TextView>(R.id.dialog_course_view_title)
        titleTV.text = title

        val photoIV: ImageView = findViewById(R.id.dialog_course_view_image_container)
        Glide.with(photoIV.context)
            .load(photo)
            .placeholder(R.color.grey)
            .into(photoIV)

        val mainTV = findViewById<TextView>(R.id.dialog_course_view_text)
        mainTV.text = text

        val timeTV = findViewById<TextView>(R.id.dialog_course_view_timeTv)
        timeTV.text = time

        val distanceTV = findViewById<TextView>(R.id.dialog_course_view_distanceTv)
        distanceTV.text = distance

        val heightTV = findViewById<TextView>(R.id.dialog_course_view_elevationTv)
        heightTV.text = height

        val closeButton = findViewById<ImageView>(R.id.dialog_course_view_closeButton)
        closeButton.setOnClickListener {
            dismiss()
        }

        val buttonArea = findViewById<LinearLayout>(R.id.course_button_area)
        val completeButton = findViewById<Button>(R.id.dialog_course_view_complete_button)
        val auditButton = findViewById<Button>(R.id.dialog_course_view_resultButton)
        val deleteButton = findViewById<Button>(R.id.dialog_course_delete_button)

        if (result == "audit") {
            buttonArea.removeView(completeButton)

            deleteButton.setOnClickListener {
                // 삭제 로직
            }

        } else if (result == "complete") {
            buttonArea.removeView(auditButton)
            buttonArea.removeView(deleteButton)

        }



    }

}