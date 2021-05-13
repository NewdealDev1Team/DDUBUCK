package com.mapo.ddubuck.share.canvas

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.View
import androidx.core.view.drawToBitmap
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.home.WalkRecord
import com.naver.maps.geometry.LatLng
import java.text.DecimalFormat


class CustomCanvas(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(
    context,
    attrs,
    defStyleAttr), View.OnClickListener {

    private val whitePathPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 8.0f
    }

    private val whiteTextPaint = Paint().apply {
        textSize = 70.0f
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 3.0f
    }

    private val blackPathPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 8.0f
    }

    private val blackTextPaint = Paint().apply {
        textSize = 70.0f
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 3.0f
    }

    private lateinit var canvas : Canvas
    private lateinit var path : Path
    private val translateMatrix = Matrix()
    private val pathBoundRect = RectF()
    private var srcBmp : Bitmap? = null
    private var walkRecord : WalkRecord? = null
    private var isBlack = true
    private val numberFormatter = DecimalFormat("#.##km")
    private val logoIcon = BitmapFactory.decodeResource(this.resources, R.drawable.icon_logo_white)
    private val logoMatrix = Matrix()

    fun initialize(uri: Uri, record: WalkRecord) {
        walkRecord = record
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            srcBmp = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(context.contentResolver, uri)
            ) { decoder: ImageDecoder, _: ImageDecoder.ImageInfo?, _: ImageDecoder.Source? ->
                decoder.isMutableRequired = true
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            srcBmp = BitmapDrawable(
                context.resources,
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            ).bitmap
        }
        invalidate()
    }

    private fun cropBitmap() {
        val srcBmp = srcBmp!!
        val croppedBmp : Bitmap
        if(srcBmp.width >= srcBmp.height) {
            croppedBmp = Bitmap.createBitmap(
                srcBmp,
                srcBmp.width / 2 - srcBmp.height / 2,
                0,
                srcBmp.height,
                srcBmp.height
            )
        } else {
            croppedBmp = Bitmap.createBitmap(
                srcBmp,
                0,
                srcBmp.height / 2 - srcBmp.width / 2,
                srcBmp.width,
                srcBmp.width
            )
        }
        this.srcBmp = Bitmap.createScaledBitmap(croppedBmp, width, height, false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizeMax = if(widthMeasureSpec>heightMeasureSpec) {
            heightMeasureSpec
        } else {
            widthMeasureSpec
        }
        layoutParams.height=sizeMax
        layoutParams.width=sizeMax
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            this.canvas = canvas
            if(srcBmp!=null && walkRecord!=null) {
                cropBitmap()
                setOnClickListener(this)
                initCanvas(isBlack)
            }

        }
    }

    private fun initCanvas(isBlack: Boolean) {
        if(::canvas.isInitialized) {
            if(srcBmp!=null) {
                val croppedBmp = srcBmp!!
                canvas.drawBitmap(croppedBmp,
                    0f,
                    0f,
                    null)
            }
            val record = walkRecord!!
            //사용자 이동경로 그리기
            path = routeToPath(record.path, width)
            initPath()
            //로고 그리기
            logoMatrix.setScale(0.15f,
                0.15f,
                (width - (logoIcon.width * 0.15f) / 2),
                height * 0.05f)
            canvas.drawBitmap(logoIcon, logoMatrix, null)
            if(isBlack) {
                canvas.drawPath(path, whitePathPaint)
                canvas.drawText(
                    numberFormatter.format(walkRecord!!.distance / 1000),
                    width * 0.05f,
                    (height - pathBoundRect.height() - 100f), whiteTextPaint)
            } else {
                canvas.drawPath(path, blackPathPaint)
                canvas.drawText(
                    numberFormatter.format(walkRecord!!.distance / 1000),
                    width * 0.05f,
                    (height - pathBoundRect.height() - 100f), blackTextPaint)

            }
        }
    }

    override fun onClick(v: View?) {
        invalidate()
        isBlack=!isBlack
    }

    private fun initPath(){
        path.computeBounds(pathBoundRect, true)
        val initX = if(pathBoundRect.left<0) {
            -pathBoundRect.left
        } else {
            -pathBoundRect.left
        }
        val initY = if(pathBoundRect.top<=0) {
            -pathBoundRect.top
        } else {
            -pathBoundRect.top
        }
        val marginValue = 0.05f
        translateMatrix.setTranslate(initX,initY)
        translateMatrix.postTranslate(width*marginValue,height-pathBoundRect.height()-(height*marginValue))
        path.transform(translateMatrix)
    }

    private fun routeToPath(
        route: List<LatLng>,
        viewWidth: Int,
    ) : Path {
        val scaleMatrix = Matrix()
        val bound = RectF()
        val path = Path()
        path.reset()
        path.moveTo(route[0].latitude.toFloat(), route[0].longitude.toFloat())
        for (i in route) {
            path.lineTo(i.latitude.toFloat(), i.longitude.toFloat())
        }
        path.computeBounds(bound, true)
        val boundMax:Float = if(bound.height() > bound.width()) {
            bound.height()
        } else {
            bound.width()
        }

        val scaleValue = (viewWidth*0.3F) / boundMax
        scaleMatrix.setScale(
            scaleValue,
            scaleValue,
            bound.centerX(),
            bound.centerY())
        path.transform(scaleMatrix)

        return path
    }

    fun saveCanvas() : Bitmap {
        return this.drawToBitmap(Bitmap.Config.ARGB_8888)
    }

}