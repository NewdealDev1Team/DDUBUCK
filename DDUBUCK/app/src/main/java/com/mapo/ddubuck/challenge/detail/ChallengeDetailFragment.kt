package com.mapo.ddubuck.challenge.detail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.R
import com.mapo.ddubuck.badge.Badge
import com.mapo.ddubuck.challenge.Challenge
import com.mapo.ddubuck.challenge.ChallengeAdapter
import com.mapo.ddubuck.challenge.ChallengeViewModel
import com.mapo.ddubuck.login.UserService
import com.mapo.ddubuck.login.UserValidationInfo
import com.mapo.ddubuck.mypage.MypageViewModel
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChallengeDetailFragment : Fragment() {
    private lateinit var challengeViewModel: ChallengeViewModel

    private val userViewModel: MypageViewModel by activityViewModels()

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
        val challengeDetailRecyclerView: RecyclerView = challengeDetailView.findViewById(R.id.challenge_detail_recyclerView)
        challengeDetailRecyclerView.isNestedScrollingEnabled = false

        val distanceChallengeDetailTitle: TextView = challengeDetailView.findViewById(R.id.distance_challenge_title)
        val disatnceChallengeDetailText: TextView = challengeDetailView.findViewById(R.id.distance_challenge_text)

        challengeDetailRecyclerView.layoutManager = challengeLayoutManager

        disatnceChallengeDetailText.text = detailText
        when (sectionNumber) {
            "1" -> {
                distanceChallengeDetailTitle.text = ddubuckDetailTitle(userViewModel.username.value.toString())[titleIndex.toInt()]

                when (titleIndex.toInt()) {
                    0 -> {
                        val challengeAdapter = ChallengeDetailAdapter(challengeDetail)
                        challengeDetailRecyclerView.adapter = challengeAdapter
                    }
                    1 -> {
                        val challengeAdapter = ChallengeDetailAdapter(challengeDetail2)
                        challengeDetailRecyclerView.adapter = challengeAdapter
                    }
                    2 -> {
                        val challengeAdapter = ChallengeDetailAdapter(challengeDetail3)
                        challengeDetailRecyclerView.adapter = challengeAdapter

                    }
                }

            }
            "2" -> {
                distanceChallengeDetailTitle.text =
                    hiddenDetailTitle(userViewModel.username.value.toString())[titleIndex.toInt()]
                val challengeAdapter = ChallengeDetailAdapter(challengeDetail)
                challengeDetailRecyclerView.adapter = challengeAdapter

            }
            "3" -> {
                distanceChallengeDetailTitle.text =
                    petDetailTitle((userViewModel.username.value.toString()))[titleIndex.toInt()]
                val challengeAdapter = ChallengeDetailAdapter(challengeDetail)
                challengeDetailRecyclerView.adapter = challengeAdapter

            }
        }


        return challengeDetailView
    }


    private val challengeDetail: MutableList<ChallengeDetail> = mutableListOf(
        ChallengeDetail("1km", R.drawable.ic_a1),
        ChallengeDetail("5km", R.drawable.ic_a1),
        ChallengeDetail("10km", R.drawable.ic_a1)
    )

    private val challengeDetail2: MutableList<ChallengeDetail> = mutableListOf(
        ChallengeDetail("1000걸음", R.drawable.ic_a1),
        ChallengeDetail("3000걸음", R.drawable.ic_a1),
        ChallengeDetail("5000걸음", R.drawable.ic_a1)
    )

    private val challengeDetail3: MutableList<ChallengeDetail> = mutableListOf(
        ChallengeDetail("성산동 코스\n" + "완주!", R.drawable.ic_a1),
        ChallengeDetail("코스\n" + "\n" + "다음은\n" + "어떤 코스일까?", R.drawable.ic_a1),
        ChallengeDetail("코스\n" + "\n" + "다음은\n" + "어떤 코스일까?", R.drawable.ic_a1)
    )


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