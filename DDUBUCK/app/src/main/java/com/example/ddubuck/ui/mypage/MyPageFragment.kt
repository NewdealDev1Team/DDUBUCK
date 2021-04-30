package com.example.ddubuck.ui.mypage

import android.app.Activity
import android.content.Context
import android.app.ActionBar
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.ui.home.HomeFragment
import com.example.ddubuck.ui.mypage.mywalk.CaloriesFragment
import com.example.ddubuck.ui.mypage.mywalk.CoseClearFragment
import com.example.ddubuck.ui.mypage.mywalk.WalkTimeFragment
import com.example.ddubuck.databinding.FragmentMypageBinding
import com.example.ddubuck.ui.home.HomeMapFragment
import com.example.ddubuck.weather.Main
import kotlinx.android.synthetic.main.fragment_mypage.*


class MyPageFragment : Fragment() {

    private lateinit var myPageViewModel: MyPageViewModel
    private lateinit var myPageBinding: FragmentMypageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val myPageView = inflater.inflate(R.layout.fragment_mypage, container, false)

        val profileImage: ImageView = myPageView.findViewById(R.id.profile_image)
        val walkingTimeButton: ConstraintLayout = myPageView.findViewById(R.id.walking_time_button)
        val courseClearButton: ConstraintLayout = myPageView.findViewById(R.id.course_complete_button)
        val calorieButton: ConstraintLayout = myPageView.findViewById(R.id.calorie_button)

        // 산책 시간 버튼 onClickListener
        walkingTimeButton.setOnClickListener {

        }

        // 코스 완주 버튼 onClickListener
        courseClearButton.setOnClickListener {

        }

        // 칼로리 버튼 onClickListener
        calorieButton.setOnClickListener {

        }


        return myPageView
    }
}

