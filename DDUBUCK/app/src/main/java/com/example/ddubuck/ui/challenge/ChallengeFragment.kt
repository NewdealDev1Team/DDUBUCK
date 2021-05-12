package com.example.ddubuck.ui.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.ui.home.bottomSheet.BottomSheetFreeDetailFragment
import kotlinx.android.synthetic.main.fragment_challenge.*
import kotlinx.android.synthetic.main.fragment_challenge.view.*

class ChallengeFragment : Fragment() {

    private lateinit var challengeViewModel: ChallengeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        challengeViewModel = ViewModelProvider(this).get(ChallengeViewModel::class.java)
        val challengeView = inflater.inflate(R.layout.fragment_challenge, container, false)

        val challengeLayoutManager = GridLayoutManager(challengeView.context, 2)
        val challengeRecyclerView: RecyclerView =
            challengeView.findViewById(R.id.challenge_recyclerView)

        challengeRecyclerView.layoutManager = challengeLayoutManager

        val challengeAdapter = ChallengeAdapter()
        challengeRecyclerView.adapter = challengeAdapter

        return challengeView
    }
}