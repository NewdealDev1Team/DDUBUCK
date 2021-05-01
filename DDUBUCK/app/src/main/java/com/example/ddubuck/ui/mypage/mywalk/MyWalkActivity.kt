package com.example.ddubuck.ui.mypage.mywalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.example.ddubuck.R
import kotlinx.android.synthetic.main.activity_my_walk.*
import kotlinx.android.synthetic.main.fragment_mypage.*
//import kotlinx.android.synthetic.main.mypage_sheet_button.*

class MyWalkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_walk)

        button_worktime.setOnClickListener{
            val worktime : WalkTimeFragment = WalkTimeFragment()
            val fmManager : FragmentManager = supportFragmentManager

            val fragmentTransaction = fmManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,worktime).commit()
        }

        button_cose_clear.setOnClickListener{
            val cose : CoseClearFragment = CoseClearFragment()
            val fmManager : FragmentManager = supportFragmentManager

            val fragmentTransaction = fmManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,cose).commit()
        }

        button_calories_walk.setOnClickListener{
            val calories : CaloriesFragment = CaloriesFragment()
            val fmManager : FragmentManager = supportFragmentManager

            val fragmentTransaction = fmManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container,calories).commit()
        }

    }
}
