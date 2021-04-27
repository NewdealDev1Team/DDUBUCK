package com.example.ddubuck.ui.share

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ddubuck.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.e
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.SaveSettings

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
        initToolBar()
        initCanvas()
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

    private fun initCanvas() {
        var maxHeight = 0.0F
        var maxWidth=0.0F
        for (i in CustomCanvas.path) {
            if(maxHeight==0.0F||maxWidth==0.0F) {
                maxHeight = i.longitude.toFloat()
                maxWidth = i.latitude.toFloat()
            } else {
                if(maxHeight <= i.longitude.toFloat()) {
                    maxHeight = i.longitude.toFloat()
                }
                if(maxWidth <= i.latitude.toFloat()) {
                    maxWidth = i.latitude.toFloat()
                }
            }
        }

        maxHeight+=10.0F
        maxWidth+=10.0F
        val bitmap =  Bitmap.createBitmap(maxWidth.toInt(), maxHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val canvasView = CustomCanvas(this)
        canvasView.draw(canvas)
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