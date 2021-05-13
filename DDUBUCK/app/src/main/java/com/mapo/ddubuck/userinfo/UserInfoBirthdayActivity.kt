package com.mapo.ddubuck.userinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import com.mapo.ddubuck.databinding.BirthdayInfoLayoutBinding
import com.mapo.ddubuck.login.UserService
import com.mapo.ddubuck.login.UserValidationInfo
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserInfoBirthdayActivity : AppCompatActivity() {
    private lateinit var binding: BirthdayInfoLayoutBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BirthdayInfoLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDefaultBirthday()
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.birthday_toolbar)
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

    private fun getDefaultBirthday() {
        val datePicker: DatePicker = findViewById(R.id.birthdaySpinner)

        val userValidation: Retrofit = Retrofit.Builder()
                .baseUrl("http://3.37.6.181:3000/get/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val userValidationServer: UserService = userValidation.create(UserService::class.java)
        userValidationServer.getUserInfo(UserSharedPreferences.getUserId(this)).enqueue(object : Callback<UserValidationInfo> {
            override fun onResponse(call: Call<UserValidationInfo>, response: Response<UserValidationInfo>) {

                val birth = response.body()?.birth!!.split("-")
                val year = birth[0]
                val month = birth[1]
                val day = birth[2]

                if (year == "" ) {
                    datePicker.init(1990, month.toInt() - 1, day.toInt(), null)
                } else {
                    datePicker.init(year.toInt(), month.toInt() - 1, day.toInt(), null)
                }

            }

            override fun onFailure(call: Call<UserValidationInfo>, t: Throwable) {
                t.message?.let { Log.e("Birthday Read Fail", it) }
            }
        })
    }

    private fun toHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toNextPage() {

        val datePicker: DatePicker = findViewById(R.id.birthdaySpinner)
        var birthday = ""
        if (datePicker.month+1 <= 9) {
            birthday = "${datePicker.year}-0${datePicker.month+1}-"
        } else {
            birthday = "${datePicker.year}-${datePicker.month+1}-"
        }

        if (datePicker.dayOfMonth < 10) {
            birthday += "0${datePicker.dayOfMonth}"
        } else {
            birthday += "${datePicker.dayOfMonth}"
        }

        Log.e("생일",birthday)

        val toBodyActivity = Intent(this, UserInfoHeightWeightActivity::class.java)
        toBodyActivity.putExtra("birthday", birthday)
        toBodyActivity.putExtra("userKey", UserSharedPreferences.getUserId(this))
        startActivity(toBodyActivity)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)

    }

}