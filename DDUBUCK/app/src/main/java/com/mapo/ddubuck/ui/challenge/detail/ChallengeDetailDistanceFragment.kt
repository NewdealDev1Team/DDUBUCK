package com.example.ddubuck.ui.challenge.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import com.example.ddubuck.ui.challenge.ChallengeViewModel
import com.example.ddubuck.ui.challenge.DDUBUCKChallengeAdapter
import com.example.ddubuck.ui.challenge.HiddenChallengeAdapter
import com.example.ddubuck.ui.challenge.PetChallengeAdapter

class ChallengeDetailDistanceFragment: Fragment() {
    private lateinit var challengeViewModel: ChallengeViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        challengeViewModel = ViewModelProvider(this).get(ChallengeViewModel::class.java)
        val challengeDetailDistanceView = inflater.inflate(R.layout.fragment_challenge_distance, container, false)

        val challengeLayoutManager = GridLayoutManager(challengeDetailDistanceView.context, 3)
        val challengeDetailRecyclerView: RecyclerView = challengeDetailDistanceView.findViewById(R.id.challenge_detail_recyclerView)
        challengeDetailRecyclerView.isNestedScrollingEnabled = false

        challengeDetailRecyclerView.setOnClickListener {
            Log.e("터치터치베이비","ㅓ치")
        }

        val distanceChallengeDetailTitle: TextView = challengeDetailDistanceView.findViewById(R.id.distance_challenge_title)
        val disatnceChallengeDetailText: TextView = challengeDetailDistanceView.findViewById(R.id.distance_challenge_text)

        val username = ""

        distanceChallengeDetailTitle.text = "도전하는 ${username}님, 정말 멋져요!\n" +
                "완료한 나의 챌린지를 확인하세요!"
        disatnceChallengeDetailText.text = "시작이 반!! 1개 달성 ⏰\n" +
                "이 미션은 한 달마다 갱신 됩니다."

        challengeDetailRecyclerView.layoutManager = challengeLayoutManager

        val challengeAdapter = ChallengeDetailAdapter()
        challengeDetailRecyclerView.adapter = challengeAdapter

        return challengeDetailDistanceView
    }
}