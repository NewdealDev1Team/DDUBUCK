package com.example.ddubuck.ui.share

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.ui.share.canvas.CanvasActivity
import com.example.ddubuck.ui.share.canvas.CustomCanvas
import com.naver.maps.geometry.LatLng
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.SaveSettings
import java.io.ByteArrayOutputStream
import java.util.*


class ShareActivity : AppCompatActivity() {
    lateinit var canvasView : CustomCanvas
    private var isFileLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        dispatchTakePictureIntent()
        initToolBar()
        initButtons()
        initRecyclerView()
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

    private fun initCanvas(srcBmp: Bitmap) {
        val walkRecord = intent.extras?.get("walkRecord")
        if(walkRecord!=null) {
            canvasView = CustomCanvas(this, null,0,srcBmp, walkRecord as WalkRecord)
            val frameView = findViewById<FrameLayout>(R.id.canvas_container)
            frameView.addView(canvasView)
        } else {
            Log.e("ERROR", "RECORD is Null!")
        }
    }

    private fun initRecyclerView() {
        val recordedValue : Array<String> = intent.getStringArrayExtra("recordedValue") as Array<String>
        val shareSelectRv : RecyclerView = findViewById(R.id.share_selectRV)
        val mAdapter = ShareSelectRvAdapter(recordedValue) { v ->
            Log.e("FFF","[$v]")
        }
        shareSelectRv.adapter = mAdapter
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
                    val imageUris: ArrayList<Uri> = arrayListOf(
                        getImageUriFromBitmap(this@ShareActivity, canvasView.saveCanvas())
                    )
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND_MULTIPLE
                        putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
                        type = "image/*"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share images to.."))
                } else {
                    Log.e("권한", "쓰기 권한을 허용해주세요")
                }
            }
        }
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                val imageBitmap = data.extras!!.get("data") as Bitmap
                initCanvas(imageBitmap)
            }
        }

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