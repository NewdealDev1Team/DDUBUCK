package com.example.ddubuck.userinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.databinding.BirthdayInfoLayoutBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserInfoBirthdayActivity : AppCompatActivity() {
    private lateinit var binding: BirthdayInfoLayoutBinding
    private lateinit var database: DatabaseReference

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BirthdayInfoLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDefaultBirthday()
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.birthday_toolbar)
        val datePicker: DatePicker = findViewById(R.id.birthdaySpinner)
        setSupportActionBar(tb)

        val tbm = supportActionBar
        if (tbm != null) {
            tbm.setDisplayShowTitleEnabled(false)
            tbm.show()
        }


        binding.nextTimeButton.setOnClickListener {
            val dialog = NextTimeDialog("알림", "추가 정보를 입력하지 않으면 서비스 이용에 제한이 있을 수 있습니다.", this)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val cancelButton: TextView = dialog.findViewById(R.id.dialog_cancel_button)
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }


            val okButton: TextView = dialog.findViewById(R.id.dialog_ok_button)
            okButton.setOnClickListener {
                dialog.dismiss()
                toHomePage()
            }
        }

        binding.toNextPageButton.setOnClickListener {
            toNextPage()
        }
    }

    fun getDefaultBirthday() {
        val datePicker: DatePicker = findViewById(R.id.birthdaySpinner)

        database = Firebase.database.reference
        database.child("users").child("Naver").child("14116137").child("birthday").get().addOnSuccessListener {
            var birthday = (it.value as String).split("-")
            if (birthday[0] != "null") {
                datePicker.init(birthday[0].toInt(), birthday[1].toInt()-1, birthday[2].toInt(), null)
            } else {
                datePicker.init(1990, birthday[1].toInt()-1, birthday[2].toInt(), null)

            }

        }.addOnFailureListener{
            datePicker.init(1990, 0, 1, null)
        }
    }

    private fun toHomePage() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toNextPage() {
        val intent = Intent(this, UserInfoHeightWeightActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)

    }

    private fun saveBirthday(year: Int, month: Int, day: Int) {

    }

}