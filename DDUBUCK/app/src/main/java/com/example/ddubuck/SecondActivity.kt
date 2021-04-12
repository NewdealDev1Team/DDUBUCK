package com.example.ddubuck

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.ddubuck.login.LoginActivity
import com.example.ddubuck.login.NaverAPI
import com.example.ddubuck.login.UserInfo
import com.example.ddubuck.weather.Weather
import com.example.ddubuck.weather.WeatherActivity
import com.example.ddubuck.weather.WeatherViewModel
import com.kakao.sdk.user.UserApiClient

class SecondActivity : AppCompatActivity() {

    lateinit var weatherFragment: WeatherActivity

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        weatherFragment = WeatherActivity()

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container , weatherFragment)
        ft.commit()
        ////

        val logout : Button = findViewById(R.id.kakao_logout_button)
        val unlink : Button = findViewById(R.id.kakao_unlink_button)
        val id : TextView = findViewById(R.id.id)
        val nickname: TextView = findViewById(R.id.nickname)
        var intent = getIntent()

        logout.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        unlink.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }

        UserApiClient.instance.me { user, error ->
            id.text = "회원번호: ${user?.id}"
            nickname.text = "닉네임: ${user?.kakaoAccount?.profile?.nickname}"

        }


    }
}