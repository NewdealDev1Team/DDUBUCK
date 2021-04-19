package com.example.ddubuck.user

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import android.widget.DatePicker
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.ddubuck.R

class AdditionalUserInfo: AppCompatActivity() {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.birthday_info_layout)

        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.birthday_toolbar)
        val datePicker: DatePicker = findViewById(R.id.birthdaySpinner)
        setSupportActionBar(tb)

        val tbm = supportActionBar
        if(tbm != null) {
            tbm.setDisplayShowTitleEnabled(false)
            tbm.show()
        }


    }

}