package com.example.ddubuck.ui.mypage.mpandroidchart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.ddubuck.R
import com.example.ddubuck.data.home.WalkRecord
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.model.GradientColor
import id.co.barchartresearch.ChartData
import id.co.barchartresearch.CustomBarChartRender
import id.co.barchartresearch.CustomMarketView
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlin.random.Random

class BarChartActivity : AppCompatActivity() {

    //왼쪽 수치 시작 ~ 끝
    companion object {
        private const val START_RANDOM = 0
        private const val END_RANDOM = 90
    }

    private val listData by lazy {
        mutableListOf(
//                WalkRecord("월",)
            ChartData("월", Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData("화", Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData("수", Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData("목", Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData("금", Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData("토", Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData("일", Random.nextInt(START_RANDOM, END_RANDOM).toFloat())
        )
    }

    //차트 색상(초록)
    private val startColor by lazy {
        ContextCompat.getColor(this, android.R.color.holo_green_dark)
    }
    private val endColor by lazy {
        ContextCompat.getColor(this, android.R.color.holo_green_dark)
    }
    //아래,왼쪽 제목 이름
    private val whiteColor by lazy {
        ContextCompat.getColor(this, R.color.colorBlack)
    }

    //그래프 가로 축,선 (점선으로 변경)
    private val transparentBlackColor by lazy {
        ContextCompat.getColor(this, R.color.colorBlack)
    }

    private val barGradientColor by lazy {
        mutableListOf(GradientColor(startColor, endColor))
    }

    private val customMarkerView by lazy {
        CustomMarketView(this, R.layout.item_marker_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)
        initChart()
    }

    private fun initChart() {
        customMarkerView.chartView = bar_chart
        with(bar_chart) {
            marker = customMarkerView
            description.isEnabled = false
            legend.isEnabled = false
            isDoubleTapToZoomEnabled = false

            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(false)
            //둥근 모서리 색상
            val barChartRender = CustomBarChartRender(this, animator, viewPortHandler).apply {
                setRadius(20)
            }
            renderer = barChartRender
        }
        setData(listData)
    }

    private fun setData(barData: List<ChartData>) {
        val values = mutableListOf<BarEntry>()
        barData.forEachIndexed { index, chartData ->
            values.add(BarEntry(index.toFloat(), chartData.value))
        }

        val barDataSet = BarDataSet(values, "").apply {
            setDrawValues(false)
            gradientColors = barGradientColor
            highLightAlpha = 0
        }

        //막대 그래프 너비 설정
        val dataSets = mutableListOf(barDataSet)
        val data = BarData(dataSets as List<IBarDataSet>?).apply {
//            setValueTextSize(30F)
            barWidth = 0.3F
        }
        //애니메이션 효과 0.1초
        with(bar_chart) {
            animateY(100)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = whiteColor
                //월 ~ 일
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return barData[value.toInt()].date
                    }
                }
            }
            //차트 왼쪽 축, Y방향 ( 수치 최소값,최대값 )
            axisRight.apply {
                textColor = whiteColor
                setDrawAxisLine(false) //격자
                gridColor = transparentBlackColor
                axisMinimum = 0F
                axisMaximum = 90F
                granularity  = 30F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
            }

            //차트 오른쪽 축, Y방향 false처리
            axisLeft.apply {
                isEnabled = false
                gridColor = transparentBlackColor
                axisMinimum = 30F
                axisMaximum = 90F
            }

            notifyDataSetChanged()
            this.data = data
            invalidate()
        }
    }
}
