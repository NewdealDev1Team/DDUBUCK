package com.example.ddubuck.ui.mypage.mywalk

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.ddubuck.R
import com.example.ddubuck.data.mypagechart.RetrofitChart
import com.example.ddubuck.data.mypagechart.chartData
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
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

//서버에 연결해서 데이터 입력
//마이페이지에서 화면 연결하기 //api 정리하기!!
@RequiresApi(Build.VERSION_CODES.O)
class WalkTimeFragment : Fragment() {
    //현재 날짜/시간 가져오기
    val dateNow: LocalDateTime = LocalDateTime.now()

    //1 ~ 5일
    val oneDaysAgo: LocalDateTime = dateNow.minusDays(1)
    val twoDaysAgo: LocalDateTime = dateNow.minusDays(2)
    val threeDaysAgo: LocalDateTime = dateNow.minusDays(3)
    val fourDaysAgo: LocalDateTime = dateNow.minusDays(4)
    val fiveDaysAgo: LocalDateTime = dateNow.minusDays(5)
    val sixDaysAgo: LocalDateTime = dateNow.minusDays(6)


    //LocalDate 문자열로 포맷
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E")
    val formatterString: String = dateNow.format(formatter)

    val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")
    val textformatterString: String = dateNow.format(textformatter)

    val timeformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("a HH:mm")
    val timeformatterString: String = dateNow.format(timeformatter)

    private lateinit var chart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_walk_time, container, false)

        RetrofitChart.instance.getRestsMypage().enqueue(object : Callback<chartData> {
            override fun onResponse(call: Call<chartData>, response: Response<chartData>) {
                if (response.isSuccessful) {
                    Log.d("text", "연결성공")
                    var result0 = response.body()?.weekStat?.get(0)?.walkTime?.toFloat()
                    var result1 = response.body()?.weekStat?.get(1)?.walkTime?.toFloat()
                    var result2 = response.body()?.weekStat?.get(2)?.walkTime?.toFloat()
                    var result3 = response.body()?.weekStat?.get(3)?.walkTime?.toFloat()
                    var result4 = response.body()?.weekStat?.get(4)?.walkTime?.toFloat()
                    var result5 = response.body()?.weekStat?.get(5)?.walkTime?.toFloat()
                    var result6 = response.body()?.weekStat?.get(6)?.walkTime?.toFloat()
                    Log.d("~~0번째 time~~~",
                        " $result0 , $result1, $result2, $result3, $result4, $result5, $result6")

                    var timeTitleName : String = response.body()?.totalStep?.get(0)?.name.toString()

                    val listData by lazy {
                        mutableListOf(
                            ChartData(sixDaysAgo.format(formatter).toString(), result0!!),
                            ChartData(fiveDaysAgo.format(formatter).toString(), result1!!),
                            ChartData(fourDaysAgo.format(formatter).toString(), result2!!),
                            ChartData(threeDaysAgo.format(formatter).toString(), result3!!),
                            ChartData(twoDaysAgo.format(formatter).toString(), result4!!),
                            ChartData(oneDaysAgo.format(formatter).toString(), result5!!),
                            ChartData(dateNow.format(formatter).toString(), result6!!)
                        )
                    }

                    chart = rootView.findViewById(R.id.time_bar_chart)
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

                    fun setData(barData: List<ChartData>) {
                        val values = mutableListOf<BarEntry>()
                        //Entry에값을추가
                        barData.forEachIndexed { index, chartData ->
                            values.add(BarEntry(index.toFloat(), chartData.value))
                        }

                        //BarDataentries.add(BarEntry(iasFloat,sumOfDay))
                        val barDataSet = BarDataSet(values, "").apply {
                            setDrawValues(false)
                            //차트색
                            val colors = ArrayList<Int>()
                            colors.add(Color.argb(55, 61, 171, 91))
                            colors.add(Color.argb(55, 61, 171, 91))
                            colors.add(Color.argb(55, 61, 171, 91))
                            colors.add(Color.argb(55, 61, 171, 91))
                            colors.add(Color.argb(55, 61, 171, 91))
                            colors.add(Color.argb(55, 61, 171, 91))
                            colors.add(Color.argb(200, 61, 171, 91))
                            setColors(colors)

                            //투명,불투명
                            highLightAlpha = 0
                        }

                        //data 클릭 시 분으로 나오는 커스텀
                        barDataSet.valueFormatter = object : ValueFormatter() {
                            private val mFormat: DecimalFormat = DecimalFormat("###")
                            fun getFormattedValue(
                                value: Int,
                                entry: Entry,
                                dataSetIndex: Int,
                                viewPortHandler: ViewPortHandler,
                            ): String {
                                return mFormat.format(value) + "분"
                            }
                        }

                        //막대 그래프 너비설정
                        val dataSets = mutableListOf(barDataSet)
                        val data = BarData(dataSets as List<IBarDataSet>?).apply {
                            barWidth = 0.3F
                        }

                        //애니메이션효과 0.1초
                        with(chart) {
                            animateY(1000)
                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                setDrawGridLines(false)
                                //그래프
                                textColor = R.color.colorBlack
                                //월~일
                                valueFormatter = object : ValueFormatter() {
                                    override fun getFormattedValue(value: Float): String {
                                        return barData[value.toInt()].date.toString()
                                    }
                                }
                            }
                            //차트 왼쪽 축,Y방향 ( 수치 최소값, 최대값 )
                            axisRight.apply {
                                //아래,왼쪽제목색깔
                                textColor = R.color.black
                                setDrawAxisLine(false)//격자
                                //그래프 가로축,선(점선으로변경)
                                gridColor = R.color.black
                                //점선 크기 조정
                                gridLineWidth = 0.5F
                                //선 길이,조각 사이의 공간,위상(점선)
                                enableGridDashedLine(5f, 5f, 5f)

                                var count = 0
                                barData.forEachIndexed { index, chartData ->
                                    while (chartData.value > axisMaximum) {
                                        count++
                                        if (chartData.value > axisMaximum) {
                                            axisMaximum += 30F
                                        } else {
                                            axisMaximum = 90F
                                        }
                                    }
                                }
                                granularity = 30F//30단위마다선을그리려고granularity설정을해주었음
                                axisMinimum = 0F

                                //y축 제목 커스텀
                                valueFormatter = object : ValueFormatter() {
                                    private val mFormat: DecimalFormat = DecimalFormat("###")
                                    override fun getFormattedValue(value: Float): String {
                                        return mFormat.format(value) + "분"
                                    }
                                }
                            }
                            //차트 오른쪽 축,Y방향 false 처리
                            axisLeft.apply {
                                isEnabled = false
                                //그래프가로축,선(점선으로변경)
                                gridColor = R.color.black

                                var count = 0
                                barData.forEachIndexed { index, chartData ->
                                    while (chartData.value > axisMaximum) {
                                        count++
                                        if (chartData.value > axisMaximum) {
                                            axisMaximum += 30F
                                        } else {
                                            axisMaximum = 90F
                                        }
                                    }
                                }
                                axisMaximum + 30F
                                granularity = 30F
                                axisMinimum = 0F
                                //axisMaximum=90F
                            }
                            //notifyDataSetChanged()
                            this.data = data
                            invalidate()
                        }
                    }
                    setData(listData)

                    //마지막 업데이트 5/4 오전 09:34
                    val day: TextView = rootView.findViewById(R.id.time_bottom_title_text_day)
                    day.setText(textformatterString)
                    val time: TextView = rootView.findViewById(R.id.time_bottom_title_text_time)
                    time.setText(timeformatterString)

                    val miniTitleTime: Int = result6!!.toInt()
                    val miniTitle: TextView = rootView.findViewById(R.id.time_mini_title)
                    miniTitle.setText(miniTitleTime.toString())

                    val titleName: TextView = rootView.findViewById(R.id.time_name)
                    titleName.setText(timeTitleName.toString())
                }
            }
            override fun onFailure(call: Call<chartData>, t: Throwable) {
                Log.d("error", t.message.toString())
            }
        })

        return rootView
    }
}



