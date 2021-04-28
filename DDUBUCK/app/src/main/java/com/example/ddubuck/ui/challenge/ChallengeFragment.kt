package com.example.ddubuck.ui.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.ui.home.bottomSheet.BottomSheetCourseProgressFragment
import com.example.ddubuck.ui.home.bottomSheet.BottomSheetFreeDetailFragment

class ChallengeFragment : Fragment() {

    private lateinit var challengeViewModel: ChallengeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        challengeViewModel =
                ViewModelProvider(this).get(ChallengeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_challenge, container, false)
        val textView: TextView = root.findViewById(R.id.text_challenge)
        challengeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        val frag = BottomSheetFreeDetailFragment()
        textView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                    .replace(R.id.challenge_container, frag, null)
                    .addToBackStack(MainActivity.CHALLENGE_BACK_STACK_TAG)
                    .commit()
        }
        return root
    }
}