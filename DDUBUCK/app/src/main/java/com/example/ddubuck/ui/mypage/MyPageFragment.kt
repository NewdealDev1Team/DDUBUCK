package com.example.ddubuck.ui.mypage

import android.app.Activity
import android.content.Context
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
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ddubuck.MainActivity
import com.example.ddubuck.MainActivityViewModel
import com.example.ddubuck.R
import com.example.ddubuck.databinding.FragmentMypageBinding
import com.example.ddubuck.login.UserService
import com.example.ddubuck.login.UserValidationInfo
import com.example.ddubuck.sharedpref.UserSharedPreferences
import com.example.ddubuck.ui.home.HomeMapFragment
import com.example.ddubuck.userinfo.NextTimeDialog
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
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
    private lateinit var profileImageViewModel: ProfileImageViewModel

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        myPageEditFragment = MyPageEditFragment()

        profileImageViewModel = ProfileImageViewModel()
        val myPageView = inflater.inflate(R.layout.fragment_mypage, container, false)

        val profileImage: CircleImageView = myPageView.findViewById(R.id.profile_image)
        val profileImageEditButton: CircleImageView =
            myPageView.findViewById(R.id.profile_edit_button)
        val userName: TextView = myPageView.findViewById(R.id.user_name)
        val stepCount: TextView = myPageView.findViewById(R.id.step_count)

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

        // 산책 시간 버튼 onClickListener
        walkingTimeButton.setOnClickListener {

        }

        // 코스 완주 버튼 onClickListener
        courseClearButton.setOnClickListener {

        }

        // 칼로리 버튼 onClickListener
        calorieButton.setOnClickListener {

        }

        routeInfoButton.setOnClickListener {
            val dialog = NextTimeDialog("사용자 지정 경로란?", "내가 사용하는 경로를 다른 사용자에게 추천하고 싶을때 사용자 지정 경로를 등록해주시면 심사 후 사용자들이 사용할 수 있는 서비스로 등록해드립니다.",
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


        return myPageView
    }

    private fun toEditInfoPage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.scrollview_mypage, myPageEditFragment)
            .addToBackStack(MainActivity.MYPAGE_TAG)
            .commit()
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
                uriArr.add(uri)
                count += 1

            }
            Log.e("이미지 경로", uriArr.toString())

            cursor.close()
        }

        val adapter = context?.let { GalleryAdapter(it, uriArr) }
        gridView.numColumns = 4 // 한 줄에 4개씩
        gridView.adapter = adapter


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


}

