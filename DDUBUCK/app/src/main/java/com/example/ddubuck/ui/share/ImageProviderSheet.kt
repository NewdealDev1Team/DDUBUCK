package com.example.ddubuck.ui.share

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.example.ddubuck.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.*

class ImageProviderSelectDialog(private val owner:Activity) : BottomSheetDialogFragment() {
    private val imageProviderSheetViewModel : ImageProviderSheetViewModel by activityViewModels()
    private lateinit var currentPhotoPath : String
    private lateinit var cameraPhotoUri : Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.bottom_sheet_image_provider, container, false)
        val cameraButton = root.findViewById<LinearLayout>(R.id.share_sheet_cameraButton)
        cameraButton.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(owner.packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {

                        null
                    }
                    photoFile?.also {
                        cameraPhotoUri = FileProvider.getUriForFile(
                            owner,
                            "com.example.ddubuck.provider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }
        val galleryButton = root.findViewById<LinearLayout>(R.id.share_sheet_galleryButton)
        galleryButton.setOnClickListener {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { getPictureIntent ->
                //TODO 실패시 처리
                startActivityForResult(getPictureIntent, REQUEST_IMAGE_SELECT)
            }
        }
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val imageUri : Uri = when(requestCode) {
                    REQUEST_IMAGE_CAPTURE -> {
                        galleryAddPic()
                        cameraPhotoUri
                    }
                    REQUEST_IMAGE_SELECT -> {
                        data.data!!
                    }
                    else -> Uri.EMPTY
                }
                imageProviderSheetViewModel.imageUri.value = imageUri
                dismiss()
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir: File = owner.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        storageDir.mkdirs()
        return File.createTempFile(
            "DDUBUCK_${System.currentTimeMillis()}", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath

        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            owner.sendBroadcast(mediaScanIntent)
        }
    }


    companion object{
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_SELECT = 2
    }

}