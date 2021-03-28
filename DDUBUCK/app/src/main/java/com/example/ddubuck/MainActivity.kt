package com.example.ddubuck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val changeScreen : Button = findViewById(R.id.changeScreen)

        changeScreen.setOnClickListener {
            val intent = Intent(this, MapFragmentActivity::class.java)

            startActivity(intent)
        }
    }
}