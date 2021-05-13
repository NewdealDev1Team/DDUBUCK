package com.example.ddubuck.ui.challenge

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R

class ChallengeFragment : Fragment() {

    private lateinit var challengeViewModel: ChallengeViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        challengeViewModel = ViewModelProvider(this).get(ChallengeViewModel::class.java)
        val challengeView = inflater.inflate(R.layout.fragment_challenge, container, false)

        val challengeLayoutManager = GridLayoutManager(challengeView.context, 2)
        val challengeRecyclerView: RecyclerView = challengeView.findViewById(R.id.challenge_recyclerView)
        challengeRecyclerView.isNestedScrollingEnabled = false

        val ddubuckChallengeTitle: TextView = challengeView.findViewById(R.id.ddubuck_challenge_title)
        val ddubuckChallengeText: TextView = challengeView.findViewById(R.id.ddubuck_challenge_text)

        val hiddenChallengeLayoutManager = GridLayoutManager(challengeView.context, 2)
        val hiddenChallengeRecyclerView: RecyclerView = challengeView.findViewById(R.id.hidden_challenge_recyclerView)
        hiddenChallengeRecyclerView.isNestedScrollingEnabled = false

        val hiddenChallengeTitle: TextView = challengeView.findViewById(R.id.hidden_challenge_title)
        val hiddenChallengeText: TextView = challengeView.findViewById(R.id.hidden_challenge_text)

        val petChallengeLayoutManager = GridLayoutManager(challengeView.context, 2)
        val petChallengeRecyclerView: RecyclerView = challengeView.findViewById(R.id.pet_challenge_recyclerView)

        val petChallengeTitle: TextView = challengeView.findViewById(R.id.pet_challenge_title)
        val petChallengeText: TextView = challengeView.findViewById(R.id.pet_challenge_text)
        petChallengeRecyclerView.isNestedScrollingEnabled = false


        ddubuckChallengeTitle.text = "뚜벅뚜벅 챌린지"
        ddubuckChallengeText.text = "우리 함께 기분 좋은 산책,\n" + "시작해볼까요?"

        hiddenChallengeTitle.text = "히든 챌린지"
        hiddenChallengeText.text = "자유산책을 하면서\n" + "숨겨진 챌린지를 찾아보세요"

        petChallengeTitle.text = "반려동물과 함께"
        petChallengeText.text = "나의 소중한 반려동물이 있나요?\n" + "우리 함께 산책해 볼까요?"

        challengeRecyclerView.layoutManager = challengeLayoutManager
        hiddenChallengeRecyclerView.layoutManager = hiddenChallengeLayoutManager
        petChallengeRecyclerView.layoutManager = petChallengeLayoutManager

        val challengeAdapter = DDUBUCKChallengeAdapter()
        challengeRecyclerView.adapter = challengeAdapter

        val hiddenChallengeAdapter = HiddenChallengeAdapter()
        hiddenChallengeRecyclerView.adapter = hiddenChallengeAdapter

        val petChallengeAdapter = PetChallengeAdapter()
        petChallengeRecyclerView.adapter = petChallengeAdapter


        return challengeView
    }
}
