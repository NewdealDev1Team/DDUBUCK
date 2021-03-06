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

import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.mypagechart.MyWalkRecordChartData
import com.mapo.ddubuck.data.mypagechart.RetrofitChart
import com.mapo.ddubuck.databinding.FragmentMypageBinding
import com.mapo.ddubuck.home.FilterDrawer
import com.mapo.ddubuck.home.HomeMapFragment
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
        userRoute: UserRoute,
        userRouteHint: TextView
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
class MyPageFragment : Fragment(), UserRouteCallback {
    private lateinit var myPageEditFragment: MyPageEditFragment
    private lateinit var mypageFragment: MyPageFragment
    private lateinit var profileImageViewModel: ProfileImageViewModel

    //?????????
    private val homemapViewModel: HomeMapViewModel by activityViewModels()
    private val myapgeViewModel: MypageViewModel by activityViewModels()

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
        val galleryHint: TextView = myPageView.findViewById(R.id.user_gallery_hint)
        getAllPhotos(galleryGrid, galleryHint)

        val walkingTimeButton: ConstraintLayout = myPageView.findViewById(R.id.walking_time_button)
        val courseClearButton: ConstraintLayout =
            myPageView.findViewById(R.id.course_complete_button)
        val calorieButton: ConstraintLayout = myPageView.findViewById(R.id.calorie_button)

        val routeInfoButton: ImageView = myPageView.findViewById(R.id.user_route_info_button)

        setUserInfo(userName, profileImage)

        val walkingTimeButtonRecord: TextView =
            myPageView.findViewById(R.id.walking_time_button_record)
        val courseEndButtonRecord: TextView = myPageView.findViewById(R.id.course_end_button_record)
        val calorieButtonRecord: TextView = myPageView.findViewById(R.id.calorie_button_record)

        setRecordInfo(stepCountInMypage,walkingTimeButtonRecord,courseEndButtonRecord,calorieButtonRecord)

        homemapViewModel.recordMywalk.observe(viewLifecycleOwner,{ v ->
            setRecordInfo(stepCountInMypage,walkingTimeButtonRecord,courseEndButtonRecord,calorieButtonRecord)
        })


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

        // ?????? ?????? ?????? onClickListener
        walkingTimeButton.setOnClickListener {
            mypageFragment = MyPageFragment()
            walkTimeFramgnet = WalkTimeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.scrollview_mypage, walkTimeFramgnet)
                .addToBackStack(MainActivity.MYPAGE_TAG)
                .commit()
        }

        // ?????? ?????? ?????? onClickListener
        courseClearButton.setOnClickListener {
            mypageFragment = MyPageFragment()
            courseClearFragment = CourseClearFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.scrollview_mypage, courseClearFragment)
                .addToBackStack(MainActivity.MYPAGE_TAG)
                .commit()
        }

        // ????????? ?????? onClickListener
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
        val userRouteHint: TextView = myPageView.findViewById(R.id.user_route_hint)
        setUserRoute(userRouteRecyclerView, userRouteHint)

        myapgeViewModel.isRouteChanged.observe(viewLifecycleOwner, {
            setUserRoute(userRouteRecyclerView, userRouteHint)
        })

        myapgeViewModel.isImageUpdate.observe(viewLifecycleOwner, {
            getAllPhotos(galleryGrid, galleryHint)
        })

        return myPageView

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllPhotos(gridView: GridView, hint: TextView) {
        val cursor = activity?.contentResolver?.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_ADDED + " DESC")
        val uriArr = ArrayList<String>()
        val sortedUriArr = ArrayList<Long>()
        val resultArr = ArrayList<String>()
        var route = ""
        var count = 0
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // ?????? ?????? Uri ????????????
                if (count == 4) break
                val uri =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                if (uri.contains(getString(R.string.app_content_path))) {
                    uriArr.add(uri)
                    count += 1
                }
            }

            for (value in uriArr) {
                val split = value.split("_")
                route = split[0]
                sortedUriArr.add(split[1].split(".")[0].toLong())
            }

            for (sorted in sortedUriArr.sortedDescending()) {
                resultArr.add("${route}_$sorted.jpg")
            }


            cursor.close()
        }

        if (uriArr.isEmpty()) {
            gridView.visibility = View.INVISIBLE
            hint.visibility = View.VISIBLE
        } else {
            gridView.visibility = View.VISIBLE
            hint.visibility = View.INVISIBLE
        }

        val adapter = context?.let { GalleryAdapter(it, resultArr) }
        gridView.numColumns = 4 // ??? ?????? 4???
        gridView.adapter = adapter
    }

    private fun toEditInfoPage() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction
            .replace(R.id.scrollview_mypage, myPageEditFragment)
            .addToBackStack(MainActivity.MYPAGE_TAG)
            .commit()


    }

    private fun setRecordInfo(setCountRecordText: TextView,walkingTimeButtonRecord: TextView,courseEndButtonRecord: TextView, calorieButtonRecord : TextView){
        //?????? ?????? ??????
        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            val userKey: Int = it.toInt()
            RetrofitChart.instance.getRestsMypage(userKey).enqueue(object : Callback<MyWalkRecordChartData> {
                override fun onResponse(call: Call<MyWalkRecordChartData>, response: Response<MyWalkRecordChartData>) {
                    if (response.isSuccessful) {
                        Log.d("text", "????????????")
                        var stepCount = response.body()?.weekStat?.get(6)?.stepCount?.toInt()
                        setCountRecordText.setText(stepCount.toString())

                        ///?????? ??????
                        var timeRecordt6 = response.body()?.weekStat?.get(6)?.walkTime?.toInt()
                        val walkingTimeButtonRecordFormat: Int = timeRecordt6!!.toInt()
                        //????????? ?????? ????????? ????????? ???????????? ??????
                        if (3600 <= timeRecordt6.toInt()) { //1??????
                            val hour: Int = ( timeRecordt6 / 60 ) / 60
                            val minute : Int = (timeRecordt6 / 60) % 60
                            val hourName: String = "??????"
                            val miniteName: String = "???"
                            walkingTimeButtonRecord.setText((hour.toString() + hourName) + (minute.toString() + miniteName))
                        } else{
                            if(60 <= timeRecordt6.toInt()) {
                                val miniteName: String = "???"
                                val mininteName: Int = timeRecordt6 / 60
                                walkingTimeButtonRecord.setText(mininteName.toString() + miniteName)
                            }else{
                                val secondName: String = "???"
                                val second: Int = timeRecordt6
                                walkingTimeButtonRecord.setText(second.toString() + secondName)
                            }
                        }

                        //?????? ??????
                        var courseRecord6 =
                            response.body()?.weekStat?.get(6)?.completedCount?.toInt()

                        val courseEndButtonRecordFormat: Int = courseRecord6!!.toInt()
                        val countName: String = "???"
                        courseEndButtonRecord.setText(courseRecord6.toString() + countName)

                        //?????? ??????
                        var calorieRecord6 = response.body()?.weekStat?.get(6)?.calorie?.toInt()
                        val walkingtimeButtonRecordFormat: Int = calorieRecord6!!.toInt()
                        val calorieName: String = "kcal"
                        calorieButtonRecord.setText(calorieRecord6.toString() + calorieName)
                    }
                }

                override fun onFailure(call: Call<MyWalkRecordChartData>, t: Throwable) {
                    Log.d("error", t.message.toString())
                }
            })


        }
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
                    val weight = response.body()?.weight
                    UserSharedPreferences.setUserWeight(context!!, weight!!)

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

    private fun setUserRoute(userRouteRecyclerView: RecyclerView, userRouteHint: TextView) {
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
                        Log.e("??????", "????????? ?????? ?????? ???????????? ??????")
                        onSuccessRoute(userRouteRecyclerView, userRouteResponse, userRouteHint)
                    }
                }

                override fun onFailure(call: Call<UserRoute>, t: Throwable) {
                    Log.e("Fail", "????????? ?????? ?????? ???????????? ?????? ${t.message}")
                }

            })
        }
    }

    private fun openUserRouteInfoDialog() {
        val dialog = NextTimeDialog("????????? ?????? ??????????",
            "?????? ???????????? ????????? ?????? ??????????????? ???????????? ????????? ????????? ?????? ????????? ?????????????????? ?????? ??? ??????????????? ????????? ??? ?????? ???????????? ?????????????????????.",
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


    override fun onSuccessRoute(userRouteRecyclerView: RecyclerView, userRoute: UserRoute, userRouteHint: TextView) {
        userRouteAdapter = context?.let { UserRouteAdapter(userRoute.audit, userRoute.complete,userViewModel, it) }
        userRouteRecyclerView.apply {
            this.adapter = userRouteAdapter
            this.layoutManager = GridLayoutManager(userRouteRecyclerView.context, 1)
        }

        if (userRoute.audit.isEmpty() && userRoute.complete.isEmpty()) {
            userRouteRecyclerView.visibility = View.INVISIBLE
            userRouteHint.visibility = View.VISIBLE
        } else {
            userRouteRecyclerView.visibility = View.VISIBLE
            userRouteHint.visibility = View.INVISIBLE
        }
    }

}

