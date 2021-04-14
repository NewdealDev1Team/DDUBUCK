package com.example.ddubuck.ui.home.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.ddubuck.R
import com.example.ddubuck.ui.home.HomeFragment

class BottomSheetFreeDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView  = inflater.inflate(R.layout.bottom_sheet_free_detail,container, false)
        val startButton : Button = rootView.findViewById(R.id.sheet_free_detail_startButton)
        startButton.setOnClickListener{
            val fm = parentFragmentManager
            fm.popBackStack(HomeFragment.DETAIL_PAGE_FRAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val frag = BottomSheetFreeProgressFragment()
            val fmTransaction = fm.beginTransaction()
            fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            fmTransaction.replace(R.id.bottom_sheet_container, frag, HomeFragment.BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(null).commit()
        }
        return rootView
    }

}