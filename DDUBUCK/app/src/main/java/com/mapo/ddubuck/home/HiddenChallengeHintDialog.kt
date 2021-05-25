package com.mapo.ddubuck.home

import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.mapo.ddubuck.R

class HiddenChallengeHintDialog(
    private val picture: String,
    owner: Activity) : Dialog(owner) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_hidden_hint)
        findViewById<ImageView>(R.id.dialog_hidden_imageView).let {
            Glide.with(it.context)
                .load(picture)
                .placeholder(R.color.grey)
                .into(it)
                .view.let { v->
                    v.setBackgroundResource(R.drawable.dialog_imageview_radius)
                    v.clipToOutline = true
                }
        }
    }
}