package com.mapo.ddubuck.userinfo

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import com.mapo.ddubuck.databinding.HeightWeightInfoLayoutBinding
import com.google.firebase.database.DatabaseReference
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserInfoHeightWeightActivity : AppCompatActivity() {
    private lateinit var binding: HeightWeightInfoLayoutBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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

        binding.endMoreInfoButton.setOnClickListener {

            if (binding.weightTextFieldInput.text.toString().trim().isEmpty() && binding.heightTextFieldInput.text.toString().trim().isEmpty()) {
                binding.weightTextField.error = "몸무게를 입력하세요."
                binding.heightTextField.error = "키를 입력하세요."
            } else if (binding.heightTextFieldInput.text.toString().trim().isEmpty()) {
                binding.weightTextField.error = null
                binding.heightTextField.error = "키를 입력하세요."

            } else if (binding.weightTextFieldInput.text.toString().trim().isEmpty()) {
                binding.weightTextField.error = "몸무게를 입력하세요."
                binding.heightTextField.error = null
            } else {
                saveHeightWeight(binding.heightTextFieldInput.text.toString().toDouble() , binding.weightTextFieldInput.text.toString().toDouble())
                toHomePage()
            }
        }
    }

    private fun toHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // 생일과 키와 몸무게 정보 저장
    private fun saveHeightWeight(height: Double, weight: Double) {
        if (intent.hasExtra("birthday")) {
            val birthday = intent.getStringExtra("birthday")!!
            val userKey = intent.getStringExtra("userKey")!!
            val userInfo: Retrofit = Retrofit.Builder()
                    .baseUrl("http://3.37.6.181:3000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            val userMoreServer: UserInfoService = userInfo.create(UserInfoService::class.java)

            if (weight == 0.0) {
                UserSharedPreferences.setUserWeight(this, 0.0)
            } else {
                UserSharedPreferences.setUserWeight(this, weight)
            }

            userMoreServer.saveUserBodyInfo(userKey, birthday, height, weight).enqueue(object : Callback<UserBody> {
                override fun onResponse(call: Call<UserBody>, response: Response<UserBody>) {

                    Log.e("Success", response.message())
                }

                override fun onFailure(call: Call<UserBody>, t: Throwable) {
                    t.message?.let { Log.e("Fail", it) }
                }
            })

        } else {
            Log.e("Error : ", "생일 저장 데이터 없음")
        }


    }




}