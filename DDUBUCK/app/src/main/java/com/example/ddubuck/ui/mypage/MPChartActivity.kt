package com.example.ddubuck.ui.mypage

import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.example.ddubuck.R
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.databinding.ActivityMainBinding


abstract class MPChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var rvGraph: RecyclerView = findViewById(R.id.rvGraph)
    private lateinit var graphAdapter:GraphAdapter

    private val WalkRecord : ArrayList<WalkRecord> = arrayListOf()
    private val week = 7    //가로 : 7일
    private val hour = 24   //세로 : 24시간
    private val lineWidth = 30      //line크기 30px


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_m_p_chart)


        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        linearLayoutManager.stackFromEnd = true
        graphAdapter.setWidthCount(week)
        graphAdapter.setHeightCount(hour)
        rvGraph.layoutManager = linearLayoutManager
        rvGraph.adapter = graphAdapter

        rvGraph.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
                super.onScrollStateChanged(recyclerView, scrollState)
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    rvGraph.post { autoScroll() }
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }


    private fun autoScroll() {
        var graph: FrameLayout?
        if (WalkRecord.size > 0)
        {
            val xy = IntArray(2)
            var gap = 0
            var position = 0
            var minimumGap = -1
            for (i in 0 until rvGraph.childCount)
            {
                graph = rvGraph.getChildAt(i) as FrameLayout
                if (graph != null)
                {
                    graph.getLocationInWindow(xy)
                    position = xy[0] + (graph.getWidth() + lineWidth) / 2
                    gap = position - rvGraph.getWidth()
                    if (minimumGap == -1 || Math.abs(gap) < Math.abs(minimumGap))
                    {
                        minimumGap = gap
                    }
                }
            }
            rvGraph.smoothScrollBy(minimumGap, 0)
        }
    }
}