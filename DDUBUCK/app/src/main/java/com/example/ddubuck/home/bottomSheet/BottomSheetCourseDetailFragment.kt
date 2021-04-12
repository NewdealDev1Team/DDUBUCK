package com.example.ddubuck.home.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ddubuck.R
import com.example.ddubuck.databinding.BottomSheetDetailBinding
import com.example.ddubuck.home.CourseItem

class BottomSheetCourseDetailFragment(private val courseItem: CourseItem) : Fragment() {
    lateinit var bind : BottomSheetDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = BottomSheetDetailBinding.inflate(inflater, container, true)
        return inflater.inflate(R.layout.bottom_sheet_detail,container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.estimatedTime.text = courseItem.walkRecord.walkTime.toString()
        bind.estimatedDistance.text = courseItem.walkRecord.distance.toString()
        bind.elevationDeviation.text = courseItem.walkRecord.altitudes.toString()
        bind.sheetDetailPicture.setImageResource(R.mipmap.ic_launcher)
    }
}