package com.example.ddubuck.ui.mypage.mywalk

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.example.ddubuck.R
import kotlinx.android.synthetic.main.activity_my_walk.*

//import kotlinx.android.synthetic.main.mypage_sheet_button.*

class MyWalkActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_walk)

        button_worktime.setOnClickListener{
            val worktime : WalkTimeFragment = WalkTimeFragment()
            val fmManager : FragmentManager = supportFragmentManager

            val fragmentTransaction = fmManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,worktime).commit()

        }

        button_course_clear.setOnClickListener{
            val course : CourseClearFragment = CourseClearFragment()
            val fmManager : FragmentManager = supportFragmentManager

            val fragmentTransaction = fmManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,course).commit()
        }

        button_calories_walk.setOnClickListener{
            val calories : CaloriesFragment = CaloriesFragment()
            val fmManager : FragmentManager = supportFragmentManager

            val fragmentTransaction = fmManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,calories).commit()
        }

    }
}
