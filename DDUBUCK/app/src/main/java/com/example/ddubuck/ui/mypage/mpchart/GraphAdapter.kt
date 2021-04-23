package com.example.ddubuck.ui.mypage.mpchart

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.ui.mypage.mpchart.GraphAdapter.*


class GraphAdapter(data: List<WalkRecord>?) : RecyclerView.Adapter<ViewHolder?>() {
    private var data: List<WalkRecord>?
    private var width = 0
    private var height = 0
    private var widthCount = 7
    private var heightCount = 24
    private var graphLineWidth = 15

    fun setData(data: List<WalkRecord>?) {
        this.data = data
    }

    fun setWidthCount(widthCount: Int) { //가로 길이 항목
        this.widthCount = widthCount
    }

    fun setHeightCount(heightCount: Int) { //세로 길이 항목
        this.heightCount = heightCount
    }

    fun setGraphLineWidth(graphLineWidth: Int) {
        this.graphLineWidth = graphLineWidth
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        Log.d("tag1","onCreateView")
        val v: View = inflater.inflate(R.layout.mpchart_line, viewGroup, false)
        width = viewGroup.width
        height = viewGroup.height
        //안드로이드 스마트폰 크기 호환성
        if (width <= 0) {
            val dm = viewGroup.context.resources.displayMetrics
            width = dm.widthPixels //가로 화소수
        }
       //레이아웃 파라미터(layoutParameter)는 뷰가 배치되는 부모, 즉 레아아웃에 소속되는 속성
        val params = v.layoutParams
        params.width = width / widthCount
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        v.layoutParams = params
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        Log.d("tag1","onBind")
        val walkTime: Long = data!![i].walkTime
        val currentHeight: Int
        (if (walkTime > 0) {
            (((height * walkTime) / heightCount).toInt())
        } else 0).also { currentHeight = it }

        val line1_param = holder.graphLine.layoutParams
        line1_param.width = graphLineWidth
        line1_param.height = currentHeight
        holder.graphLine.layoutParams = line1_param
    }

    override fun getItemCount(): Int {
        return if (data != null) data!!.size else 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val graphLine: View

        init {
            graphLine = itemView.findViewById(R.id.graphLine)
        }
    }

    init {
//        Trace.d("GraphAdapter")
        this.data = data
    }
}