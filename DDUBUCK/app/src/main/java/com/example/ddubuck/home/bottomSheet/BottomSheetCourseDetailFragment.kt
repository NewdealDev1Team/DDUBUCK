package com.example.ddubuck.home.bottomSheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ddubuck.R
import com.example.ddubuck.home.CourseItem

class BottomSheetCourseDetailFragment(private val courseItem: CourseItem) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView  = inflater.inflate(R.layout.bottom_sheet_detail,container, false)
        rootView.findViewById<TextView>(R.id.sheet_detail_title).text = courseItem.title
        rootView.findViewById<TextView>(R.id.estimated_time).text = courseItem.walkRecord.walkTime.toString()
        rootView.findViewById<TextView>(R.id.estimated_distance).text = courseItem.walkRecord.distance.toString()
        rootView.findViewById<TextView>(R.id.elevation_deviation).text = courseItem.walkRecord.altitudes.toString()
        rootView.findViewById<ImageView>(R.id.sheet_detail_picture).setImageResource(R.mipmap.ic_launcher)
        return rootView
    }

}