package com.example.ddubuck.ui.home.bottomSheet

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.ddubuck.R
import com.example.ddubuck.data.home.CourseItem
import com.example.ddubuck.ui.home.HomeFragment
import java.text.DecimalFormat

class BottomSheetCourseDetailFragment(private val courseItem: CourseItem) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView  = inflater.inflate(R.layout.bottom_sheet_course_detail,container, false)
        val titleTv : TextView = rootView.findViewById(R.id.sheet_course_detail_titleTv)
        titleTv.text = courseItem.title
        val timeTv : TextView = rootView.findViewById(R.id.sheet_course_detail_timeTv)
        timeTv.text = DateUtils.formatElapsedTime(courseItem.walkRecord.walkTime)
        val distanceTv : TextView = rootView.findViewById(R.id.sheet_course_detail_distanceTv)
        distanceTv.text = DecimalFormat("#.##m").format(courseItem.walkRecord.distance)
        val elevationTv : TextView = rootView.findViewById(R.id.sheet_course_detail_elevationTv)
        elevationTv.text = DecimalFormat("#.##m").format(courseItem.walkRecord.altitude)
        val pictureIv : ImageView = rootView.findViewById(R.id.sheet_course_detail_pictureIv)
        pictureIv.setImageResource(R.mipmap.ic_launcher)
        val startButton : Button = rootView.findViewById(R.id.sheet_course_detail_startButton)
        startButton.setOnClickListener{
            val fm = parentFragmentManager
            fm.popBackStack(HomeFragment.DETAIL_PAGE_FRAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val frag = BottomSheetCourseProgressFragment(courseItem)
            val fmTransaction = fm.beginTransaction()
            fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            fmTransaction.replace(R.id.bottom_sheet_container, frag, HomeFragment.BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(null).commit()
        }
        return rootView
    }


}