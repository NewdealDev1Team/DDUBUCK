package com.example.ddubuck.ui.home.bottomSheet

import android.os.Bundle
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

class BottomSheetCourseDetailFragment(private val courseItem: CourseItem) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView  = inflater.inflate(R.layout.bottom_sheet_course_detail,container, false)
        rootView.findViewById<TextView>(R.id.sheet_course_detail_titleTv).text = courseItem.title
        rootView.findViewById<TextView>(R.id.sheet_course_detail_timeTv).text = courseItem.walkRecord.walkTime.toString()
        rootView.findViewById<TextView>(R.id.sheet_course_detail_distanceTv).text = courseItem.walkRecord.distance.toString()
        rootView.findViewById<TextView>(R.id.sheet_course_detail_elevationTv).text = courseItem.walkRecord.altitudes.toString()
        rootView.findViewById<ImageView>(R.id.sheet_course_detail_pictureIv).setImageResource(R.mipmap.ic_launcher)
        rootView.findViewById<Button>(R.id.sheet_course_detail_startButton).setOnClickListener{
            val fm = parentFragmentManager
            fm.popBackStack(HomeFragment.DETAIL_PAGE_FRAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val frag = BottomSheetCourseProgressFragment()
            val fmTransaction = fm.beginTransaction()
            fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            fmTransaction.replace(R.id.bottom_sheet_container, frag, HomeFragment.BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(null).commit()
        }
        return rootView
    }


}