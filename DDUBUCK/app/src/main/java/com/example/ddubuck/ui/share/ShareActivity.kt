package com.example.ddubuck.ui.share

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.ui.share.canvas.CustomCanvas
import java.io.ByteArrayOutputStream
import java.util.*


class ShareActivity : AppCompatActivity() {
    lateinit var canvasView : CustomCanvas
    lateinit var walkRecord: WalkRecord
    private var isFileLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        initData()
        dispatchTakePictureIntent()
        initToolBar()
        initButtons()
        initCanvas()
    }

    private fun initData(){
        walkRecord = intent.getParcelableExtra("walkRecord")!!
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

    private fun initCanvas() {
        canvasView = CustomCanvas(this, null,0)
        val frameView = findViewById<FrameLayout>(R.id.share_canvas_container)
        frameView.addView(canvasView)
    }

/*
    private fun initRecyclerView() {
        val recordedValue : Array<String> = intent.getStringArrayExtra("recordedValue") as Array<String>
        val shareSelectRv : RecyclerView = findViewById(R.id.share_selectRV)
        val mAdapter = ShareSelectRvAdapter(recordedValue) { v ->
            Log.e("FFF","[$v]")
        }
        shareSelectRv.adapter = mAdapter
    }
 */

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
        //TODO 얘 바꾸기
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null) {
            if (resultCode == RESULT_OK) {
                val imageBitmap = when(requestCode) {
                    REQUEST_IMAGE_CAPTURE -> {
                        data.extras!!.get("data") as Bitmap
                    }
                    REQUEST_IMAGE_SELECT -> {
                        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.P) {
                            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, data.data!!))
                        } else {
                            //TODO Change to PlaceHolder
                            BitmapFactory.decodeResource(resources, R.drawable.wide_aspect_ratio)
                        }
                    }
                    else -> {
                        //TODO Change to PlaceHolder
                        BitmapFactory.decodeResource(resources, R.drawable.wide_aspect_ratio)
                    }
                }

                canvasView.initialize(imageBitmap,walkRecord)
                isFileLoaded=true
            }
        }

    }


    private fun dispatchTakePictureIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { getPictureIntent ->
            startActivityForResult(getPictureIntent, REQUEST_IMAGE_SELECT)
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
        const val REQUEST_IMAGE_SELECT = 2
    }

}