package com.mapo.ddubuck.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.mapo.ddubuck.R

class CommonDialog(private val title: String,
                   private val message: String,
                   owner: Activity) : Dialog(owner) {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_layout)
        val titleTV = findViewById<TextView>(R.id.dialog_title)
        titleTV.text = title

        val mainTV = findViewById<TextView>(R.id.dialog_text)
        mainTV.text = message

        val cancelButton: TextView = findViewById(R.id.dialog_cancel_button)
        cancelButton.visibility = View.GONE

        val okButton: TextView = findViewById(R.id.dialog_ok_button)
        okButton.setOnClickListener{
            dismiss()
        }
    }


}