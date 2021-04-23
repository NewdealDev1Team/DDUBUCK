package com.example.ddubuck.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.example.ddubuck.R
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.SaveSettings
import java.io.ByteArrayOutputStream
import java.io.File


class ShareActivity : AppCompatActivity() {
    private lateinit var mPhotoEditor : PhotoEditor
    private var isFileLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        initToolBar()
        initPhotoEditor()
        initButtons()
    }

    private fun initToolBar() {
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.share_toolbar)
        setSupportActionBar(tb)
        val tbm = supportActionBar
        if(tbm != null) {
            tbm.setDisplayHomeAsUpEnabled(true)
            tbm.show()
        }

    }

    private fun initPhotoEditor() {
        val mPhotoEditorView = findViewById<PhotoEditorView>(R.id.photoEditorView)

        mPhotoEditorView.source.setImageResource(R.drawable.weather_high)
        dispatchTakePictureIntent()
        val mTextRobotoTf = ResourcesCompat.getFont(this, R.font.mapohongdaefreedom)

        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .build()



        //TODO bundle 받기

        isFileLoaded = true

    }

    private fun initButtons() {
        val cancelButton : Button = findViewById(R.id.share_buttons_cancel)

        cancelButton.setOnClickListener{
            finish()
        }

        val confirmButton : Button = findViewById(R.id.share_buttons_confirm)


        confirmButton.setOnClickListener{
            if(isFileLoaded) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    val saveSettings = SaveSettings.Builder()
                            .setClearViewsEnabled(true)
                            .setTransparencyEnabled(true)
                            .build()
                    mPhotoEditor.saveAsBitmap(saveSettings, object : OnSaveBitmap {
                        override fun onBitmapReady(saveBitmap: Bitmap) {
                            val imageUris: ArrayList<Uri> = arrayListOf(
                                    getImageUriFromBitmap(this@ShareActivity, saveBitmap)
                            )

                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND_MULTIPLE
                                putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
                                type = "image/*"
                            }
                            startActivity(Intent.createChooser(shareIntent, "Share images to.."))
                        }

                        override fun onFailure(exception: Exception) {
                            Log.e("PhotoEditor", "Failed to save Image")
                        }
                    })
                } else {
                    //TODO 권한 비허가 시
                    Log.e("권한", "쓰기 권한을 허용해주세요")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                val imageBitmap = data.extras!!.get("data") as Bitmap
                val mPhotoEditorView = findViewById<PhotoEditorView>(R.id.photoEditorView)
                mPhotoEditorView.source.setImageBitmap(imageBitmap)
            }
        }

    }


    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    //뒤로가기 버튼 눌렀을 때 작동
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    companion object{
        const val REQUEST_IMAGE_CAPTURE = 1
    }

}