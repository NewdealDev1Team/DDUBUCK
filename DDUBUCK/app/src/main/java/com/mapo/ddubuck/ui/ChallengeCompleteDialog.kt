package com.mapo.ddubuck.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.mapo.ddubuck.R

class ChallengeCompleteDialog(private val title: String, private val image: String, private val date: String, owner: Activity) : Dialog(owner) {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_mission_complete)

        val closeButton: ImageView = findViewById(R.id.challenge_badge_dialog_close_button)
        closeButton.setOnClickListener {
            dismiss()
        }

        val titleTV: TextView = findViewById(R.id.challenge_badge_dialog_title)
        titleTV.text = title

        val iconIV: ImageView = findViewById(R.id.challenge_badge_dialog_image)
        // 이 라인이 안먹으면 context 문제일것!
        GlideToVectorYou.justLoadImage(context as Activity?, Uri.parse(image), iconIV)

        val dateTV: TextView = findViewById(R.id.challenge_badge_dialog_date)
        dateTV.text = date

    }


}