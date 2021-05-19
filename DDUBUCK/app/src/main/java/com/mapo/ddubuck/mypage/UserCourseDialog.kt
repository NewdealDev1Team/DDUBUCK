package com.mapo.ddubuck.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.internal.ContextUtils.getActivity
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserCourseDialog(
    private val title: String,
    private val photo: String,
    private val text: String,
    private val time: String,
    private val distance: String,
    private val height: String,
    private val result: String,
    private val created_at: String,
    inflater: LayoutInflater,
    owner: Activity,
) : Dialog(owner) {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_course_view)
        val titleTV = findViewById<TextView>(R.id.dialog_course_view_title)
        titleTV.text = title

        val photoIV: ImageView = findViewById(R.id.dialog_course_view_image_container)
        Glide.with(photoIV.context)
            .load(photo)
            .placeholder(R.color.grey)
            .into(photoIV)

        val mainTV = findViewById<TextView>(R.id.dialog_course_view_text)
        mainTV.text = text

        val timeTV = findViewById<TextView>(R.id.dialog_course_view_timeTv)
        timeTV.text = time

        val distanceTV = findViewById<TextView>(R.id.dialog_course_view_distanceTv)
        distanceTV.text = distance

        val heightTV = findViewById<TextView>(R.id.dialog_course_view_elevationTv)
        heightTV.text = height

        val closeButton = findViewById<ImageView>(R.id.dialog_course_view_closeButton)
        closeButton.setOnClickListener {
            dismiss()
        }

        val buttonArea = findViewById<LinearLayout>(R.id.course_button_area)
        val completeButton = findViewById<Button>(R.id.dialog_course_view_complete_button)
        val auditButton = findViewById<Button>(R.id.dialog_course_view_resultButton)
        val deleteButton = findViewById<Button>(R.id.dialog_course_delete_button)

        if (result == "audit") {
            buttonArea.removeView(completeButton)

            deleteButton.setOnClickListener {
                // 삭제 로직
                val userValidation: Retrofit = Retrofit.Builder()
                    .baseUrl("http://3.37.6.181:3000/set/User/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                // 테스트 ID 수정 필요
                val userCourseServer: UserRouteAPI = userValidation.create(UserRouteAPI::class.java)
                context.let { "1682936995" }.let {
                    userCourseServer.deleteUserRoute(it, created_at)
                        .enqueue(object : Callback<UserCourseDelete> {
                            override fun onResponse(
                                call: Call<UserCourseDelete>,
                                response: Response<UserCourseDelete>,
                            ) {
                                Log.e("Success", "유저 경로 삭제 성공")

                            }

                            override fun onFailure(call: Call<UserCourseDelete>, t: Throwable) {
                                Log.e("Error", t.message.toString())
                            }
                        })
                }

                dismiss()

            }

        } else if (result == "complete") {
            buttonArea.removeView(auditButton)
            buttonArea.removeView(deleteButton)

        }


    }

}