package com.example.ddubuck.home.bottomSheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.ddubuck.R

class BottomSheetFreeProgressFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.bottom_sheet_free_progress, container, false)
        val pauseButton : Button = rootView.findViewById(R.id.sheet_free_progress_pause)
        val endButton : Button = rootView.findViewById(R.id.sheet_free_progress_end)
        return rootView
    }
}