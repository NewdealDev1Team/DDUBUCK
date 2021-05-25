package com.mapo.ddubuck.mypage.mywalk

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.item_marker_view.view.*



@SuppressLint("ViewConstructor")
// marker
class CustomMarketView(context: Context, layoutResources: Int) :
    MarkerView(context, layoutResources) {
    // entry를 content의 텍스트에 지정
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tv_chart_marker.text = e?.y.toString()
        super.refreshContent(e, highlight)
    }
    // draw override를 사용해 marker의 위치 조정 (bar의 상단 중앙)
    override fun getOffset(): MPPointF? {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}
//val num = (e!!.y/60).toInt()
//val miniteName: String = "분"
//val courseName: String = "회"
//val calorieName: String = "kcal"
//tv_chart_marker.text = num.toString() + miniteName
//tv_chart_marke_course.text = e!!.y.toString() + courseName