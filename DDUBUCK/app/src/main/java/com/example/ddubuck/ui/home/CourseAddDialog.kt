package com.example.ddubuck.ui.home

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.databinding.DialogCourseAddBinding
import com.example.ddubuck.ui.home.bottomSheet.BottomSheetNumberFormat
import com.example.ddubuck.ui.share.ImageProviderSelectDialog
import com.example.ddubuck.ui.share.ImageProviderSheetViewModel

class CourseAddDialog(private val walkRecord: WalkRecord,
                      private val userKey : String,
                      private val createdDate : String,
                      private val owner: Activity) : DialogFragment() {
    private lateinit var binding : DialogCourseAddBinding
    private val imageProviderSheetViewModel : ImageProviderSheetViewModel by activityViewModels()
    @RequiresApi(Build.VERSION_CODES.P)
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
            var isValid : Boolean = true
            if(binding.dialogCourseAddTitle.text.isEmpty()) {
                Toast.makeText(owner, "제목을 입력해주세요!", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            if (binding.dialogCourseAddEditText.text.isEmpty()) {
                Toast.makeText(owner, "설명을 입력해주세요!", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if (binding.dialogCourseAddImageContainer.drawable == null) {
                Toast.makeText(owner, "사진을 등록해주세요!", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if(isValid) {
                val bitmap = binding.dialogCourseAddImageContainer.drawable
                val title = binding.dialogCourseAddTitle
                val description = binding.dialogCourseAddEditText.text
            }

        }

        binding.dialogCourseAddCloseButton.setOnClickListener {
            dismiss()
        }

        binding.dialogCourseAddImageButton.setOnClickListener {
            ImageProviderSelectDialog(owner).show(parentFragmentManager, null)
        }

        imageProviderSheetViewModel.imageUri.observe(viewLifecycleOwner, {v ->
            val srcBmp : Bitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(owner.contentResolver, v)
            ) { decoder: ImageDecoder, _: ImageDecoder.ImageInfo?, _: ImageDecoder.Source? ->
                decoder.isMutableRequired = true
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
            binding.dialogCourseAddImageContainer.setImageBitmap(srcBmp)
        })
        return binding.root
    }
}