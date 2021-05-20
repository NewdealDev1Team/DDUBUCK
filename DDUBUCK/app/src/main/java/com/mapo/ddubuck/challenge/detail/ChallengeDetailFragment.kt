package com.mapo.ddubuck.challenge.detail

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.R
import com.mapo.ddubuck.challenge.ChallengeViewModel
import com.mapo.ddubuck.mypage.MypageViewModel
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.mapo.ddubuck.weather.Dust
import com.mapo.ddubuck.weather.UVRays
import com.mapo.ddubuck.weather.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.OutputStream


interface ChallengeAPICallback {
    fun onSuccess(
        challengeDetailRecyclerView: RecyclerView,
        challengeDetail: ChallengeDetail,
        sectionNumber: String,
        titleIndex: String,
        challengeDetailTitle: TextView,
        disatnceChallengeDetailText: TextView
    )
}

class ChallengeDetailFragment : Fragment(), ChallengeAPICallback {
    private lateinit var challengeViewModel: ChallengeViewModel

    private val userViewModel: MypageViewModel by activityViewModels()
//    private val challengeDetail: MutableList<ChallengeDetail> = mutableListOf()
private val shareButtonViewImage : Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        challengeViewModel = ViewModelProvider(this).get(ChallengeViewModel::class.java)
        val challengeDetailView =
            inflater.inflate(R.layout.fragment_challenge_detail, container, false)
        val titleIndex = arguments?.get("index").toString()
        val sectionNumber = arguments?.get("section").toString()

        val challengeLayoutManager = GridLayoutManager(challengeDetailView.context, 3)
        val challengeDetailRecyclerView: RecyclerView =
            challengeDetailView.findViewById(R.id.challenge_detail_recyclerView)
        challengeDetailRecyclerView.layoutManager = challengeLayoutManager
        challengeDetailRecyclerView.isNestedScrollingEnabled = false

        val challengeDetailTitle: TextView =
            challengeDetailView.findViewById(R.id.distance_challenge_title)
        val disatnceChallengeDetailText: TextView =
            challengeDetailView.findViewById(R.id.distance_challenge_text)

        disatnceChallengeDetailText.text = detailText

        setChallengeDetail(challengeDetailRecyclerView, sectionNumber, titleIndex, challengeDetailTitle, disatnceChallengeDetailText)

        return challengeDetailView
    }

    private fun setChallengeDetail(challengeDetailRecyclerView: RecyclerView, sectionNumber: String, titleIndex: String, challengeDetailTitle: TextView, disatnceChallengeDetailText: TextView) {

        val userValidation: Retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.6.181:3000/get/User/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val challengeServer: ChallengeAPI = userValidation.create(ChallengeAPI::class.java)
        context?.let {UserSharedPreferences.getUserId(it) }?.let {
            challengeServer.getChallengeImage(it).enqueue(object : Callback<ChallengeDetail> {
                override fun onResponse(
                    call: Call<ChallengeDetail>,
                    response: Response<ChallengeDetail>,
                ) {

                    val challengeDetailElements = response.body()!!
                    onSuccess(challengeDetailRecyclerView, challengeDetailElements, sectionNumber, titleIndex, challengeDetailTitle, disatnceChallengeDetailText)
                }

                override fun onFailure(call: Call<ChallengeDetail>, t: Throwable) {
                    Log.e("Error", t.message.toString())
                }
            })
        }

    }


    override fun onSuccess(challengeDetailRecyclerView: RecyclerView, challengeDetail: ChallengeDetail, sectionNumber: String, titleIndex: String, challengeDetailTitle: TextView, disatnceChallengeDetailText: TextView) {

        when (sectionNumber) {
            "1" -> {
                challengeDetailTitle.text = ddubuckDetailTitle(userViewModel.username.value.toString())[titleIndex.toInt()]
                disatnceChallengeDetailText.text = detailText
                when (titleIndex.toInt()) {
                    0 -> {
                        val distance = challengeDetail.distance

                        val distanceTitle = mutableListOf<String>()
                        val distanceImage = mutableListOf<String>()

                        for (i in 0 until distance[0].title?.size!!) {
                            distanceTitle.add(distance[0].title!![i])
                            distanceImage.add(distance[1].image!![i])
                        }

                        val challengeAdapter = ChallengeDetailAdapter(distanceTitle, distanceImage, context as Activity)
                        challengeDetailRecyclerView.adapter = challengeAdapter

                    }
                    1 -> {
                        val stepCount = challengeDetail.stepCount

                        val stepCountTitle = mutableListOf<String>()
                        val stepCountImage = mutableListOf<String>()

                        for (i in 0 until stepCount[0].title?.size!!) {
                            stepCountTitle.add(stepCount[0].title!![i])
                            stepCountImage.add(stepCount[1].image!![i])
                        }

                        val challengeAdapter = ChallengeDetailAdapter(stepCountTitle, stepCountImage, context as Activity)
                        challengeDetailRecyclerView.adapter = challengeAdapter
                    }
                    2 -> {
                        val course = challengeDetail.course

                        val courseTitle = mutableListOf<String>()
                        val courseImage = mutableListOf<String>()

                        for (i in 0 until course[0].title?.size!!) {
                            courseTitle.add(course[0].title!![i])
                            courseImage.add(course[1].image!![i])
                        }

                        val challengeAdapter = ChallengeDetailAdapter(courseTitle, courseImage, context as Activity)
                        challengeDetailRecyclerView.adapter = challengeAdapter
                    }
                }

            }
            "2" -> {
                disatnceChallengeDetailText.text = ""
                challengeDetailTitle.text =
                    hiddenDetailTitle(userViewModel.username.value.toString())[titleIndex.toInt()]

                when (titleIndex.toInt()) {
                    0 -> {
                        val place = challengeDetail.place

                        val placeTitle = mutableListOf<String>()
                        val placeImage = mutableListOf<String>()

                        for (i in 0 until place[0].title?.size!!) {
                            placeTitle.add(place[0].title!![i])
                            placeImage.add(place[1].image!![i])
                        }

                        val challengeAdapter = ChallengeDetailAdapter(placeTitle, placeImage, context as Activity)
                        challengeDetailRecyclerView.adapter = challengeAdapter

                    }
                    1 -> {
                        val weather = challengeDetail.weather

                        val weatherTitle = mutableListOf<String>()
                        val weatherImage = mutableListOf<String>()

                        for (i in 0 until weather[0].title?.size!!) {
                            weatherTitle.add(weather[0].title!![i])
                            weatherImage.add(weather[1].image!![i])
                        }

                        val challengeAdapter = ChallengeDetailAdapter(weatherTitle, weatherImage, context as Activity)
                        challengeDetailRecyclerView.adapter = challengeAdapter
                    }
                }

            }
            "3" -> {
                challengeDetailTitle.text =
                    petDetailTitle((userViewModel.username.value.toString()))[titleIndex.toInt()]
                disatnceChallengeDetailText.text = detailText

                when (titleIndex.toInt()) {
                    0 -> {
                        val petDistance = challengeDetail.petDistance

        val challengeShareButton: Button = challengeDetailView.findViewById(R.id.challenge_screenshot)
        challengeShareButton.setOnClickListener {
            Instacapture.capture(
                this.requireActivity(),
                object : SimpleScreenCapturingListener() {
                    @RequiresApi(Build.VERSION_CODES.Q)
                    override fun onCaptureComplete(captureview: Bitmap) {
                        val capture: RecyclerView = requireView().findViewById(R.id.challenge_detail_recyclerView) as RecyclerView
                        val shareButtonView: View = challengeDetailView.findViewById(R.id.challenge_detail_recyclerView)
                        shareButtonView.visibility = View.GONE
                        capture.buildDrawingCache()
                        val captureview: Bitmap = capture.getDrawingCache()
                        val uri = saveImageExternal(captureview)
                        uri?.let {
                            if (!shareImageURI(uri)) {
                                shareButtonView.visibility = View.VISIBLE
                            } else {
                                shareImageURI(uri)
                            }
                        }
                    }
                }
            )
        }
        return challengeDetailView
    }
    fun saveImageExternal(image: Bitmap): Uri? {
        val filename = "DDUBUCK_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var uri: Uri? = null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        //use application context to get contentResolver
        val contentResolver = this.requireActivity().contentResolver

        contentResolver.also { resolver ->
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = uri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { image.compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(uri!!, contentValues, null, null)

        return uri!!
    }


    fun shareImageURI(uri: Uri) : Boolean {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "message/rfc822"
            type = "image/*"
        }

        startActivity(Intent.createChooser(shareIntent, "Send to"))
        return shareButtonViewImage
    }
                        val petDistanceTitle = mutableListOf<String>()
                        val petDistanceImage = mutableListOf<String>()

                        for (i in 0 until petDistance[0].title?.size!!) {
                            petDistanceTitle.add(petDistance[0].title!![i])
                            petDistanceImage.add(petDistance[1].image!![i])
                        }

                        val challengeAdapter = ChallengeDetailAdapter(petDistanceTitle, petDistanceImage, context as Activity)
                        challengeDetailRecyclerView.adapter = challengeAdapter

                    }
                    1 -> {
                        val petCourse = challengeDetail.petCourse

                        val petCourseTitle = mutableListOf<String>()
                        val petCourseImage = mutableListOf<String>()

                        for (i in 0 until petCourse[0].title?.size!!) {
                            petCourseTitle.add(petCourse[0].title!![i])
                            petCourseImage.add(petCourse[1].image!![i])
                        }

                        val challengeAdapter = ChallengeDetailAdapter(petCourseTitle, petCourseImage, context as Activity)
                        challengeDetailRecyclerView.adapter = challengeAdapter
                    }
                }

            }
        }
    }


    private fun ddubuckDetailTitle(username: String): MutableList<String> {
        return mutableListOf(
            "도전하는 ${username}님, 정말 멋져요!\n" + "완료한 나의 챌린지를 확인하세요!",
            "오늘도 꾸준히! ${username}님, \n" + "오늘의 걸음 수를 확인하세요!",
            "${username}님, 힘내요!\n" + "내가 완료한 코스를 확인하세요!",
        )
    }

    private fun hiddenDetailTitle(username: String): MutableList<String> {
        return mutableListOf(
            "${username}님, 좋아요!\n" + "히든 플레이스를 찾으셨나요?!",
            "${username}님,\n" + "오늘 날씨는 어땠나요?"
        )
    }

    private fun petDetailTitle(username: String): MutableList<String> {
        return mutableListOf(
            "${username}님, 대단해요!\n" + "내 반려동물과 함께해왔어요!",
            "자연과 함께하는 ${username}님, \n" + "오늘도 수고하셨어요!"
        )
    }

    private val detailText = "이 미션은 한 달마다 갱신 됩니다."


}