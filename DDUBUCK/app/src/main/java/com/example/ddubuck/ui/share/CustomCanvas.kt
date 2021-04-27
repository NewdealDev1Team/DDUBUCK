package com.example.ddubuck.ui.share

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.naver.maps.geometry.LatLng

//class android.content.Context, interface android.util.AttributeSet

class CustomCanvas @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val linePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5.0f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.BLACK)
        val fooList = mutableListOf<Float>()
        var maxHeight = 0.0F
        var maxWidth=0.0F
        for (i in path) {
            fooList.add(i.latitude.toFloat())
            fooList.add(i.longitude.toFloat())
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
        //canvas?.setBitmap(Bitmap.createBitmap(maxWidth.toInt(), maxHeight.toInt(), Bitmap.Config.ARGB_8888))
        canvas?.drawLines(fooList.toFloatArray(), linePaint)
        //canvas?.drawLine(36.0F, 126.0F, 33.0F,123.0F, linePaint)
    }

    companion object{
        val path = listOf(
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
        )
    }
}