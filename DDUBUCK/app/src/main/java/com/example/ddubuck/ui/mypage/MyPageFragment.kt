package com.example.ddubuck.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.ddubuck.MainActivity
import com.example.ddubuck.MainActivityViewModel
import com.example.ddubuck.R
import com.example.ddubuck.databinding.FragmentMypageBinding
import com.example.ddubuck.login.UserService
import com.example.ddubuck.login.UserValidationInfo
import com.example.ddubuck.sharedpref.UserSharedPreferences
import com.example.ddubuck.ui.home.HomeMapFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_edit_userinfo.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPageFragment : Fragment() {
    private lateinit var myPageViewModel: MyPageViewModel
    private lateinit var myPageBinding: FragmentMypageBinding
    private lateinit var myPageEditFragment: MyPageEditFragment
    private lateinit var mypageFragment: MyPageFragment

    private val activityModel: MainActivityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val myPageView = inflater.inflate(R.layout.fragment_mypage, container, false)

        val profileImage: CircleImageView = myPageView.findViewById(R.id.profile_image)
        val profileImageEditButton: CircleImageView = myPageView.findViewById(R.id.profile_edit_button)
        val userName: TextView = myPageView.findViewById(R.id.user_name)
        val stepCount: TextView = myPageView.findViewById(R.id.step_count)

        val walkingTimeButton: ConstraintLayout = myPageView.findViewById(R.id.walking_time_button)
        val courseClearButton: ConstraintLayout =
            myPageView.findViewById(R.id.course_complete_button)
        val calorieButton: ConstraintLayout = myPageView.findViewById(R.id.calorie_button)

        setUserInfo(userName)

        profileImageEditButton.setOnClickListener {

            mypageFragment = MyPageFragment()
            myPageEditFragment = MyPageEditFragment()

            toEditInfoPage()

        }

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

    private fun toEditInfoPage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_main_container, myPageEditFragment)
            .addToBackStack(MainActivity.MYPAGE_TAG)
            .commit()

    }

    private fun setUserInfo(userName: TextView) {
        val userValidation: Retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.6.181:3000/get/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userValidationServer: UserService = userValidation.create(UserService::class.java)

        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            userValidationServer.getUserInfo(it).enqueue(object : Callback<UserValidationInfo> {
                override fun onResponse(
                    call: Call<UserValidationInfo>,
                    response: Response<UserValidationInfo>,
                ) {
                    val name = response.body()?.name
                    userName.text = name.toString()
                }

                override fun onFailure(call: Call<UserValidationInfo>, t: Throwable) {
                    Log.e("Error", "user 정보 가져오기 실패")
                }
            })
        }
    }


}

