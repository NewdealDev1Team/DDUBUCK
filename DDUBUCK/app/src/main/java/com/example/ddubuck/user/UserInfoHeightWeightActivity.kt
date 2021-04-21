package com.example.ddubuck.user

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.ddubuck.R
import com.example.ddubuck.databinding.BirthdayInfoLayoutBinding
import com.example.ddubuck.databinding.HeightWeightInfoLayoutBinding

class UserInfoHeightWeightActivity: AppCompatActivity() {
    private lateinit var binding: HeightWeightInfoLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HeightWeightInfoLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.birthday_toolbar)

        setSupportActionBar(tb)

        val tbm = supportActionBar
        if (tbm != null) {
            tbm.setDisplayShowTitleEnabled(false)
            tbm.show()
        }

        binding.backToBirthdayButton.setOnClickListener {
            val intent = Intent(this, UserInfoBirthdayActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_slide_enter, R.anim.activity_slide_exit)

        }
    }

}