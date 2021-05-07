package com.example.ddubuck.ui.mypage

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.*
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.data.mypagechart.RetrofitChart
import com.example.ddubuck.data.mypagechart.chartData
import com.example.ddubuck.databinding.FragmentMypageBinding
import com.example.ddubuck.login.UserService
import com.example.ddubuck.login.UserValidationInfo
import com.example.ddubuck.sharedpref.UserSharedPreferences
import com.example.ddubuck.ui.mypage.mywalk.CaloriesFragment
import com.example.ddubuck.ui.mypage.mywalk.CourseClearFragment
import com.example.ddubuck.ui.mypage.mywalk.WalkTimeFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_edit_userinfo.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPageFragment : Fragment() {
    private lateinit var myPageBinding: FragmentMypageBinding
    private lateinit var myPageEditFragment: MyPageEditFragment
    private lateinit var mypageFragment: MyPageFragment

    private lateinit var walkTimeFramgnet: WalkTimeFragment
    private lateinit var courseClearFragment: CourseClearFragment
    private lateinit var caloriesFragment: CaloriesFragment

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val myPageView = inflater.inflate(R.layout.fragment_mypage, container, false)

        val profileImage: CircleImageView = myPageView.findViewById(R.id.profile_image)
        val profileImageEditButton: CircleImageView =
            myPageView.findViewById(R.id.profile_edit_button)
        val userName: TextView = myPageView.findViewById(R.id.user_name)
        val stepCount: TextView = myPageView.findViewById(R.id.step_count)

        val walkingTimeButton: ConstraintLayout = myPageView.findViewById(R.id.walking_time_button)
        val courseClearButton: ConstraintLayout =
            myPageView.findViewById(R.id.course_complete_button)
        val calorieButton: ConstraintLayout = myPageView.findViewById(R.id.calorie_button)

        setUserInfo(userName)

        profileImage.setOnClickListener {
            mypageFragment = MyPageFragment()
            myPageEditFragment = MyPageEditFragment()

            toEditInfoPage()
        }

        profileImageEditButton.setOnClickListener {
            mypageFragment = MyPageFragment()
            myPageEditFragment = MyPageEditFragment()

            toEditInfoPage()

        }
        //나의 산책 기록
        RetrofitChart.instance.getRestsMypage().enqueue(object : Callback<chartData> {
            override fun onResponse(call: Call<chartData>, response: Response<chartData>) {
                if (response.isSuccessful) {
                    Log.d("text", "연결성공")
                    var timeRecordt6 = response.body()?.weekStat?.get(6)?.walkTime?.toInt()
                    val walkingTimeButtonRecordFormat : Int = timeRecordt6!!.toInt()
                    val walkingTimeButtonRecord : TextView = myPageView.findViewById(R.id.walking_time_button_record)
                    walkingTimeButtonRecord.setText(timeRecordt6.toString())

                    var courseRecord6 = response.body()?.weekStat?.get(6)?.completedCount?.toInt()
                    val courseEndButtonRecordFormat : Int = courseRecord6!!.toInt()
                    val courseEndButtonRecord : TextView = myPageView.findViewById(R.id.course_end_button_record)
                    courseEndButtonRecord.setText(courseRecord6.toString())

                    var calorieRecord6 = response.body()?.weekStat?.get(6)?.calorie?.toInt()
                    val walkingtimeButtonRecordFormat : Int = calorieRecord6!!.toInt()
                    val calorieButtonRecord : TextView = myPageView.findViewById(R.id.calorie_button_record)
                    calorieButtonRecord.setText(calorieRecord6.toString())
                }
            }

            override fun onFailure(call: Call<chartData>, t: Throwable) {
                Log.d("error", t.message.toString())
            }
        })

        // 산책 시간 버튼 onClickListener
        walkingTimeButton.setOnClickListener {
            walkTimeFramgnet = WalkTimeFragment()
            toChartWalkTimePage()
        }

        // 코스 완주 버튼 onClickListener
        courseClearButton.setOnClickListener {
            courseClearFragment = CourseClearFragment()
            toChartCourseClearPage()
        }

        // 칼로리 버튼 onClickListener
        calorieButton.setOnClickListener {
            caloriesFragment = CaloriesFragment()
            toChartCaloriePage()
        }
        return myPageView
    }

    private fun toChartWalkTimePage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.mypage, walkTimeFramgnet)
            .addToBackStack(MainActivity.MYPAGE_TAG)
            .commit()
    }
    private fun toChartCourseClearPage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.mypage, courseClearFragment)
            .addToBackStack(MainActivity.MYPAGE_TAG)
            .commit()
    }
    private fun toChartCaloriePage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.mypage, caloriesFragment)
            .addToBackStack(MainActivity.MYPAGE_TAG)
            .commit()
    }

    private fun toEditInfoPage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.mypage, myPageEditFragment)
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

