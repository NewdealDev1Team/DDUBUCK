package com.example.ddubuck.user

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.databinding.ActivityMainBinding
import com.example.ddubuck.databinding.BirthdayInfoLayoutBinding

class UserInfoBirthdayActivity : AppCompatActivity() {
    private lateinit var binding: BirthdayInfoLayoutBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BirthdayInfoLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.birthday_toolbar)
        val datePicker: DatePicker = findViewById(R.id.birthdaySpinner)
        setSupportActionBar(tb)

        val tbm = supportActionBar
        if (tbm != null) {
            tbm.setDisplayShowTitleEnabled(false)
            tbm.show()
        }
        
        datePicker.init(1990, 0, 1, null)

        binding.nextTimeButton.setOnClickListener {
            val dialog = NextTimeDialog("알림", "추가 정보를 입력하지 않으면" + "\n"
                    +"서비스 이용에 제한이 있을 수" + "\n"+ "있습니다.", this)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        binding.toNextPageButton.setOnClickListener {
            toNextPage()
        }
    }
    fun dialogCallback(flag: Boolean){
        if(flag) {
            toHomePage()
        }
    }

    private fun toHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toNextPage() {
        val intent = Intent(this, UserInfoHeightWeight::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveBirthday(year: Int, month: Int, day: Int) {

    }

}