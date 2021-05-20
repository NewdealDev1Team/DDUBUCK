package com.mapo.ddubuck

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.mapo.ddubuck.login.LoginActivity

class SplashActivity: AppCompatActivity() {
    private val SPLASH_VIEW_TIME: Long = 2000 //2초간 스플래시 화면을 보여줌 (ms)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Handler().postDelayed({ //delay를 위한 handler
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_VIEW_TIME)
    }
}