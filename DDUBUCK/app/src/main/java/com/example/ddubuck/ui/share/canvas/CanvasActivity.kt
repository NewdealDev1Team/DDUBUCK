package com.example.ddubuck.ui.share.canvas

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ddubuck.R
import com.example.ddubuck.ui.share.ShareActivity
import ja.burhanrashid52.photoeditor.PhotoEditorView
import java.io.ByteArrayOutputStream

///캔버스로 사진 편집기 구현
///--------------------------------------
///목표
///1.사진 불러오기 (갤러리/카메라/코드상의 Bitmap)
///2.산책기록 Bitmap 형식으로 불러오기 (재사용을 위해 기록이 없는상황도 고려할것)
///2-1.Text -> Bitmap 변환
///2-2.UserPath -> PolyLine 변환
///3.사진 저장하기

class CanvasActivity : AppCompatActivity() {

    private var isFileLoaded : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)
        //dispatchTakePictureIntent()
        val tempBitmap = BitmapFactory.decodeResource(this.resources, R.drawable.wide_aspect_ratio)
        initCanvas(tempBitmap)
        initToolBar()
        initButtons()
    }

    private fun initToolBar() {
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.canvas_toolbar)
        setSupportActionBar(tb)
        val tbm = supportActionBar
        if(tbm != null) {
            tbm.setDisplayHomeAsUpEnabled(true)
            tbm.show()
        }
    }

    private fun initCanvas(srcBmp: Bitmap) {
        val canvasView = CustomCanvas(this, null,0,srcBmp)
        val frameView = findViewById<FrameLayout>(R.id.canvas_container)
        frameView.addView(canvasView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null) {
            if (requestCode == ShareActivity.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                val imageBitmap = data.extras!!.get("data") as Bitmap
                initCanvas(imageBitmap)
            }
        }
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, ShareActivity.REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    private fun initButtons() {
        val cancelButton : Button = findViewById(R.id.canvas_buttons_cancel)

        cancelButton.setOnClickListener{
            finish()
        }

        val confirmButton : Button = findViewById(R.id.canvas_buttons_confirm)

        confirmButton.setOnClickListener{
            if(isFileLoaded) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.e("저장", "저장성공!")
                } else {
                    //TODO 권한 비허가 시
                    Log.e("권한", "쓰기 권한을 허용해주세요")
                }
            }
        }
    }

}