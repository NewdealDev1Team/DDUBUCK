package com.example.ddubuck.home.bottomSheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ddubuck.R
import com.example.ddubuck.home.CourseItem

class BottomSheetFreeDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView  = inflater.inflate(R.layout.bottom_sheet_detail_free,container, false)
        val startButton : Button = rootView.findViewById(R.id.start_button_free)
        startButton.setOnClickListener{
            Toast.makeText(context, "foo", Toast.LENGTH_SHORT).show()
        }
        return rootView
    }

}