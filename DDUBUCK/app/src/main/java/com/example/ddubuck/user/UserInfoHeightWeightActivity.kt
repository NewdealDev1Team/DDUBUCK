package com.example.ddubuck.user

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.databinding.HeightWeightInfoLayoutBinding


interface MoreUserInfo {

}

class UserInfoHeightWeightActivity : AppCompatActivity() {
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
                toHomePage()
            }
        }
    }

    private fun toHomePage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}