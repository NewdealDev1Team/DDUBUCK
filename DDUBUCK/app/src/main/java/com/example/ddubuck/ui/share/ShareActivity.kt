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
        //walkRecord = intent.getParcelableExtra("walkRecord")!!
        walkRecord = record
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
        val path = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            saveImageInQ(bitmap)
        } else {
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        }
        return Uri.parse(path.toString())
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveImageInQ(bitmap: Bitmap):Uri {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
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


        val record = WalkRecord(
            listOf(
                LatLng(37.5634487, 126.9095964),
                LatLng(37.5634487, 126.9095964),
                LatLng(37.5633626, 126.909694),
                LatLng(37.5633571, 126.9097694),
                LatLng(37.5633571, 126.90984),
                LatLng(37.5633221, 126.9099255),
                LatLng(37.5633134, 126.9099768),
                LatLng(37.5632954, 126.9100538),
                LatLng(37.5632686, 126.9101073),
                LatLng(37.5632467, 126.9101699),
                LatLng(37.5632112, 126.910241),
                LatLng(37.5631607, 126.9103086),
                LatLng(37.5631374, 126.9103636),
                LatLng(37.5631046, 126.9104265),
                LatLng(37.5630928, 126.9104946),
                LatLng(37.5630647, 126.910558),
                LatLng(37.5630556, 126.9106346),
                LatLng(37.5630485, 126.9107188),
                LatLng(37.5630194, 126.9107888),
                LatLng(37.5629645, 126.9108356),
                LatLng(37.5629391, 126.9108982),
                LatLng(37.5629178, 126.9109625),
                LatLng(37.5628923, 126.911015),
                LatLng(37.5628343, 126.9110621),
                LatLng(37.5627825, 126.9111007),
                LatLng(37.562794, 126.9111654),
                LatLng(37.5628479, 126.9111989),
                LatLng(37.5628893, 126.9112494),
                LatLng(37.5629422, 126.9113111),
                LatLng(37.5630043, 126.9113603),
                LatLng(37.5630721, 126.9114157),
                LatLng(37.5631237, 126.9114621),
                LatLng(37.5631659, 126.9115185),
                LatLng(37.5632176, 126.9115605),
                LatLng(37.5632818, 126.9116114),
                LatLng(37.563334, 126.9116627),
                LatLng(37.5633851, 126.911589),
                LatLng(37.5634156, 126.9115281),
                LatLng(37.5634431, 126.9114677),
                LatLng(37.563473, 126.9113982),
                LatLng(37.5635015, 126.9113472),
                LatLng(37.563515, 126.9112821),
                LatLng(37.5635183, 126.9112276),
                LatLng(37.5635257, 126.9111676),
                LatLng(37.5635646, 126.911103),
                LatLng(37.5636038, 126.9110487),
                LatLng(37.5636418, 126.9109895),
                LatLng(37.5636675, 126.9109325),
                LatLng(37.5636661, 126.9108817),
                LatLng(37.5636367, 126.9108278),
                LatLng(37.5636023, 126.9107581),
                LatLng(37.5635353, 126.9107046),
                LatLng(37.5634556, 126.9106759),
                LatLng(37.5633873, 126.9106403),
                LatLng(37.5633273, 126.9106282),
                LatLng(37.5633331, 126.9105438),
                LatLng(37.5633615, 126.9104891),
                LatLng(37.5633889, 126.9104018),
                LatLng(37.5634081, 126.9103501),
                LatLng(37.5634461, 126.910275),
                LatLng(37.5634975, 126.910197),
                LatLng(37.5635037, 126.9101374),
                LatLng(37.5634898, 126.9100751),
                LatLng(37.5635484, 126.9100265),
                LatLng(37.5635873, 126.9099652),
                LatLng(37.563618, 126.9098981),
                LatLng(37.5636427, 126.9098263),
                LatLng(37.5636503, 126.9097682),
                LatLng(37.563677, 126.9096886),
                LatLng(37.563695, 126.9096351),
                LatLng(37.5637293, 126.9095621),
                LatLng(37.563764, 126.9094942),
                LatLng(37.5637895, 126.9094328),
            ),
            listOf(36.273F,17.716F,8.217F,8.098F,6.959F,6.089F,6.096F,5.968F,4.096F,4.03F,4.027F,4.045F,4.047F,4.154F,5.528F,4.474F,5.382F,3.634F,4.058F,3.378F,3.493F,4.101F,4.0F,4.015F,3.89F,4.855F,5.439F,4.064F,4.042F,5.43F,6.045F,5.582F,4.079F,4.12F,6.463F,7.698F,7.969F,5.968F,4.092F,4.101F,4.905F,6.106F,6.106F,6.056F,5.183F,4.148F,5.804F,4.477F,4.081F,4.076F,4.07F,4.159F,4.158F,4.098F,4.91F,6.114F,6.112F,6.001F,6.015F,6.023F,6.025F,4.748F,5.372F,6.039F,6.057F,6.022F,4.45F,4.096F,4.086F,4.103F,4.087F).average(),
            listOf(2.2850745F,2.231419F,1.8323104F,1.2427931F,1.0168366F,1.4252578F,1.2969129F,0.87330353F,0.95543486F,1.1152202F,1.159526F,1.163301F,0.7323433F,0.97935826F,1.080985F,1.0844249F,0.9402625F,0.57910275F,1.0228742F,1.0462228F,0.82830733F,0.42001262F,0.569097F,0.90020376F,0.97791094F,0.9100287F,0.87062603F,0.95780647F,1.0784539F,1.0578516F,1.0011464F,0.95464677F,1.0094901F,0.36221814F,1.3652068F,1.2659861F,0.9151175F,0.97190046F,0.83896244F,0.846014F,0.86373615F,0.86929697F,0.9738966F,0.9405922F,0.92884946F,0.36063546F,0.3820255F,0.363634F,0.8273605F,1.0257097F,1.0750376F,1.0602535F,1.1451268F,0.90960175F,0.9265632F,1.0658382F,1.0389845F,0.99926394F,1.081557F,1.1682404F,0.65749264F,1.0621904F,1.060041F,1.0258611F,0.9792899F,0.80706793F,1.1155007F,1.1113594F,1.1130321F,1.036313F,0.6483078F).average(),
            360,
            3021,
            487.4403199747573,
        )
    }

}