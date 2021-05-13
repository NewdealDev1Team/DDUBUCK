package com.mapo.ddubuck.home.bottomSheet

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import com.mapo.ddubuck.home.HomeMapViewModel

class BottomSheetFreeProgressFragment: Fragment() {
    //뷰모델
    private val homeMapViewModel: HomeMapViewModel by activityViewModels()
    private var isPaused : Boolean = false

    //메모리 누수 경고!
    private lateinit var mContext : Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.bottom_sheet_free_progress, container, false)
        val pauseButton : Button = rootView.findViewById(R.id.sheet_free_progress_pauseButton)
        val endButton : Button = rootView.findViewById(R.id.sheet_free_progress_endButton)
        val formatter = BottomSheetNumberFormat()

        //산책 시작
        homeMapViewModel.courseWalkTrigger(false)
        homeMapViewModel.recorderTrigger(true)

        pauseButton.setOnClickListener{
            isPaused = if(isPaused) {
                //재개
                homeMapViewModel.pauseTrigger(false)
                pauseButton.text="일시정지"
                pauseButton.setTextColor(Color.parseColor("#3DAB5B"))
                pauseButton.setBackgroundResource(R.drawable.sheet_button_pressed)

                //ui변경
                false
            } else {
                //pause
                homeMapViewModel.pauseTrigger(true)
                pauseButton.text="시작하기"
                pauseButton.setTextColor(Color.WHITE)
                pauseButton.setBackgroundResource(R.drawable.sheet_button_unpressed)
                //ui변경
                true
            }
        }

        endButton.setOnClickListener{
            parentFragmentManager.popBackStack(MainActivity.HOME_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }


        val walkTimeTv : TextView = rootView.findViewById(R.id.sheet_free_progress_timeTv)
        homeMapViewModel.walkTime.observe(viewLifecycleOwner,{ v->
            walkTimeTv.text=DateUtils.formatElapsedTime(v)
        })
        val distanceTv : TextView = rootView.findViewById(R.id.sheet_free_progress_distanceTv)
        homeMapViewModel.walkDistance.observe(viewLifecycleOwner,{ v->
            distanceTv.text=formatter.getFormattedDistance(v)
        })
        val calorieTv : TextView = rootView.findViewById(R.id.sheet_free_progress_calorieTv)
        homeMapViewModel.walkCalorie.observe(viewLifecycleOwner,{ v->
            calorieTv.text=formatter.getFormattedCalorie(v)
        })
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeMapViewModel.recorderTrigger(false)
    }

}