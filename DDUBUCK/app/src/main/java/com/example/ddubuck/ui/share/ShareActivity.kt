package com.example.ddubuck.ui.share

import android.Manifest
import android.content.ContentValues
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
import android.os.Environment
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
import com.naver.maps.geometry.LatLng
import java.io.ByteArrayOutputStream
import java.io.OutputStream
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

        val photoSelectButton = findViewById<FrameLayout>(R.id.share_buttons_image)
        photoSelectButton.setOnClickListener{
            dispatchTakePictureIntent()
        }
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageInQ(bitmap)
        } else {
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "DDUBUCK_${System.currentTimeMillis()}", null)
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

        //use application context to get contentResolver
        val contentResolver = application.contentResolver

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null) {
            if (resultCode == RESULT_OK) {
                val imageUri : Uri = when(requestCode) {
                    REQUEST_IMAGE_CAPTURE -> ({
                        data.extras!!.get("data")
                    }) as Uri
                    REQUEST_IMAGE_SELECT -> {
                        data.data!!
                    }
                    else -> Uri.EMPTY
                }

                canvasView.initialize(imageUri,walkRecord)
                isFileLoaded=true
                val photoSelectButton = findViewById<FrameLayout>(R.id.share_buttons_image)
                val frameView = findViewById<FrameLayout>(R.id.share_canvas_container)
                frameView.addView(photoSelectButton)
            }
        }

    }


    private fun dispatchTakePictureIntent() {
    //TODO 이미지/갤러리 다이알로그

//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//        }
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