package com.example.ddubuck.user

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import com.example.ddubuck.R

class NextTimeDialog(private val title: String , private val message: String, private val owner: UserInfoBirthdayActivity) : Dialog(owner) {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_layout)
        val titleTV = findViewById<TextView>(R.id.dialog_title)
        titleTV.text = title

        val mainTV = findViewById<TextView>(R.id.dialog_text)
        mainTV.text = message

        val cancelButton: TextView = findViewById(R.id.dialog_cancel_button)
        cancelButton.setOnClickListener{
            owner.dialogCallback(false)
            dismiss()
        }

    }


}