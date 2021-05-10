package com.example.ddubuck.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.databinding.DialogCourseAddBinding
import com.example.ddubuck.ui.home.bottomSheet.BottomSheetNumberFormat

class CourseAddDialog(private val walkRecord: WalkRecord) : DialogFragment() {
    private lateinit var binding : DialogCourseAddBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCourseAddBinding.inflate(inflater)
        val numberFormatter = BottomSheetNumberFormat()
        binding.dialogCourseAddTimeTv.text = numberFormatter.getFormattedTime(walkRecord.walkTime)
        binding.dialogCourseAddDistanceTv.text = numberFormatter.getFormattedDistance(walkRecord.distance)
        binding.dialogCourseAddElevationTv.text = numberFormatter.getFormattedAltitude(walkRecord.altitude)
        binding.dialogCourseAddConfirmButton.setOnClickListener {
            Log.e("내경로추가버튼","날누르지마!!!!!!!!")
        }
        return binding.root
    }
}