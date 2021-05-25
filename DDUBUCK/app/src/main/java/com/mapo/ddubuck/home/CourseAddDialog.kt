package com.mapo.ddubuck.home

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.RetrofitService
import com.mapo.ddubuck.data.home.WalkRecord
import com.mapo.ddubuck.databinding.DialogCourseAddBinding
import com.mapo.ddubuck.home.bottomSheet.BottomSheetNumberFormat
import com.mapo.ddubuck.mypage.MypageViewModel
import com.mapo.ddubuck.share.ImageProviderSelectDialog
import com.mapo.ddubuck.share.ImageProviderSheetViewModel
import java.lang.IllegalArgumentException

class CourseAddDialog(private val walkRecord: WalkRecord,
                      private val userKey : String,
                      private val owner: Activity) : DialogFragment() {
    private lateinit var binding : DialogCourseAddBinding
    private val imageProviderSheetViewModel : ImageProviderSheetViewModel by activityViewModels()
    private val mypageViewModel : MypageViewModel by activityViewModels()
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCourseAddBinding.inflate(inflater)
        val numberFormatter = BottomSheetNumberFormat()
        var bitmapUri : Uri? = null
        binding.dialogCourseAddTimeTv.text = numberFormatter.getFormattedTime(walkRecord.walkTime)
        binding.dialogCourseAddDistanceTv.text = numberFormatter.getFormattedDistance(walkRecord.distance)
        binding.dialogCourseAddElevationTv.text = numberFormatter.getFormattedAltitude(walkRecord.altitude)

        binding.dialogCourseAddConfirmButton.setOnClickListener {
            var isValid = true
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
                val title = binding.dialogCourseAddTitle.text.toString()
                val description = binding.dialogCourseAddEditText.text.toString()
                val imageUri = getRealPathFromURI(bitmapUri!!)!!

                RetrofitService().addAdditionalInfo(userKey,title,description,imageUri)
                mypageViewModel.isRouteChanged.value = true
                dismiss()
            }

        }

        binding.dialogCourseAddCloseButton.setOnClickListener {
            dismiss()
        }

        binding.dialogCourseAddImageButton.setOnClickListener {
            ImageProviderSelectDialog(owner).show(parentFragmentManager, null)
        }

        imageProviderSheetViewModel.imageUri.observe(viewLifecycleOwner, {v ->
            bitmapUri = v
            val srcBmp : Bitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(owner.contentResolver, v)
            ) { decoder: ImageDecoder, _: ImageDecoder.ImageInfo?, _: ImageDecoder.Source? ->
                decoder.isMutableRequired = true
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
            binding.dialogCourseAddImageContainer.let { v->
                v.setImageBitmap(srcBmp)
                v.setBackgroundResource(R.drawable.dialog_imageview_radius)
                v.clipToOutline = true
            }
        })
        return binding.root
    }

    @SuppressLint("Recycle")
    private fun getRealPathFromURI(uri: Uri): String? {
        Log.e("uri", "${uri.path}")
        val buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            return uri.path
        }
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity?.contentResolver?.query(uri, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                try {
                    columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                } catch (e:IllegalArgumentException) {

                    return uri.path
                }
            }
        }
        return cursor?.getString(columnIndex)
    }
}