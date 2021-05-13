package com.mapo.ddubuck.share

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.mapo.ddubuck.R
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
                startActivityForResult(getPictureIntent, REQUEST_IMAGE_SELECT)
            }
        }
        return root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val imageUri : Uri = when(requestCode) {
                    REQUEST_IMAGE_CAPTURE -> {
                        cameraPhotoUri = galleryAddPic()
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
        return File.createTempFile(
            "DDUBUCK_${System.currentTimeMillis()}", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun galleryAddPic():Uri{
        val bytes = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageInQ(bitmap)
        } else {
            MediaStore.Images.Media.insertImage(owner.contentResolver, bitmap, "DDUBUCK_${System.currentTimeMillis()}", null)
        }
        return Uri.parse(path.toString())
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveImageInQ(bitmap: Bitmap):Uri {
        val filename = "DDUBUCK_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream?
        var imageUri: Uri?
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val contentResolver = owner.application.contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(imageUri!!, contentValues, null, null)

        return imageUri!!

    }



    companion object{
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_SELECT = 2
    }

}