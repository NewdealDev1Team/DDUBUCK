package com.mapo.ddubuck.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.mapo.ddubuck.R
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
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
    private val userRouteAdapter: UserRouteAdapter,
    private val myapgeViewModel: MypageViewModel,
    owner: Activity,
) : Dialog(owner) {

    @RequiresApi(Build.VERSION_CODES.Q)
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
            .view.let { v->
                v.setBackgroundResource(R.drawable.dialog_imageview_radius)
                v.clipToOutline = true
            }

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

                val userCourseServer: UserRouteAPI = userValidation.create(UserRouteAPI::class.java)
                context.let { UserSharedPreferences.getUserId(it) }.let {
                    userCourseServer.deleteUserRoute(it, created_at)
                        .enqueue(object : Callback<AuditForDelete> {
                            override fun onResponse(
                                call: Call<AuditForDelete>,
                                response: Response<AuditForDelete>,
                            ) {
                                val audit = response.body()?.audit
                                userRouteAdapter.updateRecyclerView(audit!!)
                                myapgeViewModel.isRouteChanged.value = true
                                Toast.makeText(context, "$title 경로가 삭제되었습니다. ", Toast.LENGTH_SHORT).show()
                            }

                            override fun onFailure(call: Call<AuditForDelete>, t: Throwable) {
                                Toast.makeText(context, "$title 경로가 삭제에 실패하였습니다. ", Toast.LENGTH_SHORT).show()
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