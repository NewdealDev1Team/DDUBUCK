package com.example.ddubuck.ui.home.bottomSheet

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.ddubuck.R
import com.example.ddubuck.ui.home.HomeMapViewModel
import org.w3c.dom.Text

class BottomSheetFreeProgressFragment : Fragment() {
    //뷰모델
    private val model: HomeMapViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.bottom_sheet_free_progress, container, false)
        val pauseButton : Button = rootView.findViewById(R.id.sheet_free_progress_pauseButton)
        val endButton : Button = rootView.findViewById(R.id.sheet_free_progress_endButton)

        pauseButton.setOnClickListener{
            model.recorderTrigger(true)
        }

        endButton.setOnClickListener{
            model.recorderTrigger(false)
        }

        val walkTimeTv : TextView = rootView.findViewById(R.id.sheet_free_progress_timeTv)
        model.walkTime.observe(viewLifecycleOwner,{v->
            walkTimeTv.text=v.toString()
        })
        val distanceTv : TextView = rootView.findViewById(R.id.sheet_free_progress_distanceTv)
        model.walkDistance.observe(viewLifecycleOwner,{v->
            distanceTv.text=v.toString()
        })
        val calorieTv : TextView = rootView.findViewById(R.id.sheet_free_progress_calorieTv)
        model.walkCalorie.observe(viewLifecycleOwner,{v->
            calorieTv.text=v.toString()
        })
        return rootView
    }
}