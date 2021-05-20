package com.mapo.ddubuck.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.mypagechart.RetrofitChart
import com.mapo.ddubuck.data.mypagechart.chartData
import com.mapo.ddubuck.home.HomeMapViewModel

import com.mapo.ddubuck.login.UserService
import com.mapo.ddubuck.login.UserValidationInfo
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.mapo.ddubuck.userinfo.NextTimeDialog
import com.mapo.ddubuck.mypage.mywalk.CaloriesFragment
import com.mapo.ddubuck.mypage.mywalk.CourseClearFragment
import com.mapo.ddubuck.mypage.mywalk.WalkTimeFragment
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface UserRouteCallback {
    fun onSuccessRoute(
        userRouteRecyclerView: RecyclerView,
        userRoute: UserRoute
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
class MyPageFragment : Fragment(), UserRouteCallback {
    private lateinit var myPageEditFragment: MyPageEditFragment
    private lateinit var mypageFragment: MyPageFragment
    private lateinit var profileImageViewModel: ProfileImageViewModel

    //뷰모델
    private val model: HomeMapViewModel by activityViewModels()

    var userRouteAdapter: UserRouteAdapter? = null

    private lateinit var walkTimeFramgnet: WalkTimeFragment
    private lateinit var courseClearFragment: CourseClearFragment
    private lateinit var caloriesFragment: CaloriesFragment

    private val userViewModel: MypageViewModel by activityViewModels()

    //    private lateinit var v : View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        myPageEditFragment = MyPageEditFragment()

        profileImageViewModel = ProfileImageViewModel()
        val myPageView = inflater.inflate(R.layout.fragment_mypage, container, false)
//        v = myPageView
        val profileImage: CircleImageView = myPageView.findViewById(R.id.profile_image)
        val profileImageEditButton: CircleImageView =
            myPageView.findViewById(R.id.profile_edit_button)
        val userName: TextView = myPageView.findViewById(R.id.user_name)
        val stepCountInMypage: TextView = myPageView.findViewById(R.id.step_count)

        val galleryGrid: GridView = myPageView.findViewById(R.id.gallery_grid)
        getAllPhotos(galleryGrid)

        val walkingTimeButton: ConstraintLayout = myPageView.findViewById(R.id.walking_time_button)
        val courseClearButton: ConstraintLayout =
            myPageView.findViewById(R.id.course_complete_button)
        val calorieButton: ConstraintLayout = myPageView.findViewById(R.id.calorie_button)

        val routeInfoButton: ImageView = myPageView.findViewById(R.id.user_route_info_button)

        setUserInfo(userName, profileImage)


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
        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            val userKey: Int = it.toInt()
            RetrofitChart.instance.getRestsMypage(userKey).enqueue(object : Callback<chartData> {
                override fun onResponse(call: Call<chartData>, response: Response<chartData>) {
                    if (response.isSuccessful) {
                        Log.d("text", "연결성공")
                        var stepCount = response.body()?.weekStat?.get(6)?.stepCount?.toInt()
                        val setCountRecordText: TextView = stepCountInMypage //TextView
                        setCountRecordText.setText(stepCount.toString())


                        var timeRecordt6 = response.body()?.weekStat?.get(6)?.walkTime?.toInt()
                        val walkingTimeButtonRecordFormat: Int = timeRecordt6!!.toInt()
                        val walkingTimeButtonRecord: TextView =
                            myPageView.findViewById(R.id.walking_time_button_record)
                        if (60 <= timeRecordt6.toInt()) {
                            val hour: Int = timeRecordt6 / 60
                            val hourName: String = "시"
                            walkingTimeButtonRecord.setText(hour.toString() + hourName)
                        } else {
                            val miniteName: String = "분"
                            walkingTimeButtonRecord.setText(timeRecordt6.toString() + miniteName)
                        }


                        var courseRecord6 =
                            response.body()?.weekStat?.get(6)?.completedCount?.toInt()
                        val courseEndButtonRecordFormat: Int = courseRecord6!!.toInt()
                        val courseEndButtonRecord: TextView =
                            myPageView.findViewById(R.id.course_end_button_record)
                        val countName: String = "번"
                        courseEndButtonRecord.setText(courseRecord6.toString() + countName)

                        var calorieRecord6 = response.body()?.weekStat?.get(6)?.calorie?.toInt()
                        val walkingtimeButtonRecordFormat: Int = calorieRecord6!!.toInt()
                        val calorieButtonRecord: TextView =
                            myPageView.findViewById(R.id.calorie_button_record)
                        val calorieName: String = "kcal"
                        calorieButtonRecord.setText(calorieRecord6.toString() + calorieName)
                    }
                }

                override fun onFailure(call: Call<chartData>, t: Throwable) {
                    Log.d("error", t.message.toString())
                }
            })
        }

        // 산책 시간 버튼 onClickListener
        walkingTimeButton.setOnClickListener {
            mypageFragment = MyPageFragment()
            walkTimeFramgnet = WalkTimeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.scrollview_mypage, walkTimeFramgnet)
                .addToBackStack(MainActivity.MYPAGE_TAG)
                .commit()
        }

        // 코스 완주 버튼 onClickListener
        courseClearButton.setOnClickListener {
            mypageFragment = MyPageFragment()
            courseClearFragment = CourseClearFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.scrollview_mypage, courseClearFragment)
                .addToBackStack(MainActivity.MYPAGE_TAG)
                .commit()
        }

        // 칼로리 버튼 onClickListener
        calorieButton.setOnClickListener {
            mypageFragment = MyPageFragment()
            caloriesFragment = CaloriesFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.scrollview_mypage, caloriesFragment)
                .addToBackStack(MainActivity.MYPAGE_TAG)
                .commit()
        }


        routeInfoButton.setOnClickListener {
            openUserRouteInfoDialog()
        }


        val userRouteRecyclerView: RecyclerView =
            myPageView.findViewById(R.id.user_route_recyclerview)
        userRouteRecyclerView.isNestedScrollingEnabled = false

        setUserRoute(userRouteRecyclerView)
        return myPageView
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllPhotos(gridView: GridView) {
        val cursor = activity?.contentResolver?.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")
        val uriArr = ArrayList<String>()
        var count = 0
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 사진 경로 Uri 가져오기
                if (count == 4) break
                val uri =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                if (uri.contains(getString(R.string.app_content_path))) {
                    uriArr.add(uri)
                    count += 1
                }
            }
            Log.e("이미지 경로", uriArr.toString())

            cursor.close()
        }

        val adapter = context?.let { GalleryAdapter(it, uriArr) }
        gridView.numColumns = 4 // 한 줄에 4개
        gridView.adapter = adapter
    }

    private fun toEditInfoPage() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction
            .replace(R.id.scrollview_mypage, myPageEditFragment)
            .addToBackStack(MainActivity.MYPAGE_TAG)
            .commit()


    }


    private fun setUserInfo(userName: TextView, profileImage: CircleImageView) {
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
                    val profileImageURL = response.body()?.picture

                    userViewModel.setUserValue(name.toString())

                    userName.text = name.toString()
                    activity?.let { it1 ->
                        Glide.with(it1).load(profileImageURL).into(profileImage)
                    }
                }

                override fun onFailure(call: Call<UserValidationInfo>, t: Throwable) {
                    Log.e("Error", t.message.toString())
                }
            })
        }
    }

    private fun setUserRoute(userRouteRecyclerView: RecyclerView) {
        val userValidation: Retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.6.181:3000/get/User/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userValidationServer: UserRouteAPI = userValidation.create(UserRouteAPI::class.java)

        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            userValidationServer.getUserRoute(it).enqueue(object : Callback<UserRoute> {
                override fun onResponse(call: Call<UserRoute>, response: Response<UserRoute>) {
                    val userRouteResponse = response.body()
                    if (userRouteResponse != null) {
                        Log.e("성공", "사용자 지정 경로 가져오기 성공")
                        onSuccessRoute(userRouteRecyclerView, userRouteResponse)
                    }
                }

                override fun onFailure(call: Call<UserRoute>, t: Throwable) {
                    Log.e("Fail", "사용자 지정 경로 가져오기 실패")
                }

            })
        }
    }

    private fun openUserRouteInfoDialog() {
        val dialog = NextTimeDialog("사용자 지정 경로란?",
            "내가 사용하는 경로를 다른 사용자에게 추천하고 싶을때 사용자 지정 경로를 등록해주시면 심사 후 사용자들이 사용할 수 있는 서비스로 등록해드립니다.",
            context as Activity)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val okButton: TextView = dialog.findViewById(R.id.dialog_ok_button)
        okButton.setOnClickListener {
            dialog.dismiss()
        }

        val cancelButton: TextView = dialog.findViewById(R.id.dialog_cancel_button)
        cancelButton.visibility = View.INVISIBLE
    }


    override fun onSuccessRoute(userRouteRecyclerView: RecyclerView, userRoute: UserRoute) {
        userRouteAdapter = context?.let { UserRouteAdapter(userRoute.audit, userRoute.complete, it) }
        userRouteRecyclerView.apply {
            this.adapter = userRouteAdapter
            this.layoutManager = GridLayoutManager(userRouteRecyclerView.context, 1)
        }

    }

}

