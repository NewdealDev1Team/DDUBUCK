package com.example.ddubuck.ui.mypage.mywalk

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.ddubuck.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import id.co.barchartresearch.ChartData
import id.co.barchartresearch.CustomBarChartRender
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

//서버에 연결해서 데이터 입력
//마이페이지에서 화면 연결하기 //api 정리하기!!

@RequiresApi(Build.VERSION_CODES.O)
class WalkTimeFragment : Fragment() {

    private lateinit var chart : BarChart

    override fun onCreateView( //프래그먼트가 인터페이스를 처음 그릴때 사용함
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d("onCreateView","~~~ onCreateView ~~~")
        val rootView : View = inflater.inflate(R.layout.fragment_walk_time, container, false)

        chart = rootView.findViewById(R.id.bar_chart)
            //바 차트 커스텀
            with(chart) {//그래프의 마커를 터치히라 때 해당 데이터를 보여줌
                description.isEnabled = false
                legend.isEnabled = false
                isDoubleTapToZoomEnabled = false

                setPinchZoom(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(false)
                //차트 라운들 모양 커스텀
                val barChartRender = CustomBarChartRender(this, animator, viewPortHandler).apply {
                    setRadius(20)
                }
                renderer = barChartRender
            }
            setData(listData)

        return rootView
    }


    //왼쪽 수치 시작 ~ 끝
    companion object {
        private const val START_RANDOM = 0
        private const val END_RANDOM = 90
    }

    ////현재 날짜/시간 가져오기
//@RequiresApi(Build.VERSION_CODES.O)
//val date: LocalDate = LocalDate.now()
//fun main(args: Array<String>) {
//    println("-------- 현재 날짜:-------- $date")
//}
    //현재 날짜/시간 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    val dateNow: LocalDateTime = LocalDateTime.now()
//    LocalDateTime twoDaysAgo = now.minusDays(2); // 2일 전
    val oneDaysAgo : LocalDateTime = dateNow.minusDays(1)
    val twoDaysAgo : LocalDateTime = dateNow.minusDays(2)
    val threeDaysAgo : LocalDateTime = dateNow.minusDays(3)
    val fourDaysAgo : LocalDateTime = dateNow.minusDays(4)
    val fiveDaysAgo : LocalDateTime = dateNow.minusDays(5)
    val sixDaysAgo : LocalDateTime = dateNow.minusDays(5)


    //LocalDate 문자열로 포맷
    val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("M/dd")
    val formatterString: String = dateNow.format(formatter)

    private val listData by lazy {
        mutableListOf(
            ChartData(sixDaysAgo.format(formatter).toString(), Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData(fiveDaysAgo.format(formatter).toString(), Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData(fourDaysAgo.format(formatter).toString(), Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData(threeDaysAgo.format(formatter).toString(), Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData(twoDaysAgo.format(formatter).toString(), Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData(oneDaysAgo.format(formatter).toString(), Random.nextInt(START_RANDOM, END_RANDOM).toFloat()),
            ChartData(dateNow.format(formatter).toString(), Random.nextInt(START_RANDOM, END_RANDOM).toFloat())
        )
    }

    private fun setData(barData: List<ChartData>) {
        val values = mutableListOf<BarEntry>()
        barData.forEachIndexed { index, chartData ->
            values.add(BarEntry(index.toFloat(), chartData.value))
        }

        val barDataSet = BarDataSet(values, "").apply {
            setDrawValues(false)
            //차트 색
            setColor(Color.argb(55,31, 117, 60));
//            gradientColors = mutableListOf(GradientColor(R.color.white, R.color.white))
            //투명,불투명
            highLightAlpha = 0
        }
        //data 클릭 시 분으로 나오는 커스텀
        barDataSet.valueFormatter = object : ValueFormatter(){
            private val mFormat : DecimalFormat = DecimalFormat("###")
            fun getFormattedValue(value:Int, entry: Entry, dataSetIndex : Int, viewPortHandler: ViewPortHandler) : String{
                return mFormat.format(value) + "분"
            }
        }

//막대 그래프 너비 설정
        val dataSets = mutableListOf(barDataSet)
        val data = BarData(dataSets as List<IBarDataSet>?).apply {
//            setValueTextSize(30F)
            barWidth = 0.3F
        }
//애니메이션 효과 0.1초
        with(chart) {
            animateY(1000)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                //그래프
                textColor = R.color.colorBlack
                //월 ~ 일
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return barData[value.toInt()].date.toString()
                    }
                }
            }
//차트 왼쪽 축, Y방향 ( 수치 최소값,최대값 )
            axisRight.apply {
                //아래,왼쪽 제목 색깔
                textColor = R.color.black
                setDrawAxisLine(false) //격자
                //그래프 가로 축,선 (점선으로 변경)
                gridColor = R.color.black
                //cnr wjatjs
                gridLineWidth = 0.5F
                //선 길이, 조각 사이의 공간, 위상
                enableGridDashedLine(5f,5f,5f)
                axisMinimum = 0F
                axisMaximum = 90F
                granularity = 30F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
                //y축 제목 커스
                valueFormatter = object : ValueFormatter(){
                    private val mFormat : DecimalFormat = DecimalFormat("###")
                    override fun getFormattedValue(value: Float): String {
                        return mFormat.format(value) + "분"
                    }
                }
            }

//차트 오른쪽 축, Y방향 false처리
            axisLeft.apply {
                isEnabled = false
                //그래프 가로 축,선 (점선으로 변경)
                gridColor = R.color.black
                axisMinimum = 30F
                axisMaximum = 90F
            }
            notifyDataSetChanged()
            this.data = data
            invalidate()
        }
        //setData
    }
}

