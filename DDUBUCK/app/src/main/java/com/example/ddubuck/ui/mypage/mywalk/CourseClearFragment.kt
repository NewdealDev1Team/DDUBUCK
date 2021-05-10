package com.example.ddubuck.ui.mypage.mywalk

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.example.ddubuck.MainActivityViewModel
import com.example.ddubuck.R
import com.example.ddubuck.data.mypagechart.RetrofitChart
import com.example.ddubuck.data.mypagechart.chartData
import com.example.ddubuck.login.UserService
import com.example.ddubuck.login.UserValidationInfo
import com.example.ddubuck.sharedpref.UserSharedPreferences
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import id.co.barchartresearch.ChartData
import id.co.barchartresearch.CustomBarChartRender
import kotlinx.android.synthetic.main.fragment_course_clear.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.O)
class CourseClearFragment : Fragment() {
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

    val textformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")
    val textformatterString: String = dateNow.format(textformatter)

    val courseformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("a HH:mm")
    val courseformatterString: String = dateNow.format(courseformatter)

    private lateinit var chart: BarChart

    private val mainViewModel: MainActivityViewModel by activityViewModels()
    //개인 UserID
    private var UserId : Int = -1
    override fun onCreateView(
        //프래그먼트가 인터페이스를 처음 그릴때 사용함
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_course_clear, container, false)

        mainViewModel.toolbarTitle.value = "코스 완주"
        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            var userKey : Int = it.toInt()
        RetrofitChart.instance.getRestsMypage(userKey).enqueue(object : Callback<chartData> {
            override fun onResponse(call: Call<chartData>, response: Response<chartData>) {
                if (response.isSuccessful) {
                    Log.d("text", "연결성공")
                    var result0 = response.body()?.weekStat?.get(0)?.completedCount?.toFloat()
                    var result1 = response.body()?.weekStat?.get(1)?.completedCount?.toFloat()
                    var result2 = response.body()?.weekStat?.get(2)?.completedCount?.toFloat()
                    var result3 = response.body()?.weekStat?.get(3)?.completedCount?.toFloat()
                    var result4 = response.body()?.weekStat?.get(4)?.completedCount?.toFloat()
                    var result5 = response.body()?.weekStat?.get(5)?.completedCount?.toFloat()
                    var result6 = response.body()?.weekStat?.get(6)?.completedCount?.toFloat()
                    Log.d("~~0번째 time~~~",
                        " $result0 , $result1, $result2, $result3, $result4, $result5, $result6")

                    val sum: Int = (result0!!.toInt() + result1!!.toInt() + result2!!.toInt()
                            + result3!!.toInt() + result4!!.toInt() + result5!!.toInt() + result6!!.toInt())

                    var courseTitleName: String =
                        response.body()?.totalStat?.get(0)?.name.toString()


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

                    chart = rootView.findViewById(R.id.course_bar_chart)
                    //바 차트 커스텀
                    with(chart) {//그래프의 마커를 터치히라 때 해당 데이터를 보여줌
                        description.isEnabled = false
                        legend.isEnabled = false
                        isDoubleTapToZoomEnabled = false

                        setPinchZoom(false)
                        setDrawBarShadow(false)
                        setDrawValueAboveBar(false)
                        //차트 라운들 모양 커스텀
                        val barChartRender =
                            CustomBarChartRender(this, animator, viewPortHandler).apply {
                                setRadius(20)
                            }
                        renderer = barChartRender
                    }

                    fun setData(barData: List<ChartData>) {
                        val values = mutableListOf<BarEntry>()
                        barData.forEachIndexed { index, chartData ->
                            values.add(BarEntry(index.toFloat(), chartData.value))
                        }

                        val barDataSet = BarDataSet(values, "").apply {
                            setDrawValues(false)
                            //차트 색
                            val colors = ArrayList<Int>()
                            colors.add(Color.argb(55, 31, 117, 60));
                            colors.add(Color.argb(55, 31, 117, 60));
                            colors.add(Color.argb(55, 31, 117, 60));
                            colors.add(Color.argb(55, 31, 117, 60));
                            colors.add(Color.argb(55, 31, 117, 60));
                            colors.add(Color.argb(55, 31, 117, 60));
                            colors.add(Color.argb(200, 31, 117, 60));
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

                        //막대 그래프 너비 설정
                        val dataSets = mutableListOf(barDataSet)
                        val data = BarData(dataSets as List<IBarDataSet>?).apply {
//                         setValueTextSize(30F)
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
                                        return barData[value.toInt()].date
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
                                enableGridDashedLine(5f, 5f, 5f)

                                var count = 0
                                barData.forEachIndexed { index, chartData ->
                                    while (chartData.value > axisMaximum) {
                                        count++
                                        if (chartData.value > axisMaximum) {
                                            axisMaximum += 1F
                                        } else {
                                            axisMaximum = 3F
                                        }
                                    }
                                }

                                granularity = 1F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
                                axisMinimum = 0F
//                              axisMaximum = 3F
                                //y축 제목 커스텀
                                valueFormatter = object : ValueFormatter() {
                                    private val mFormat: DecimalFormat = DecimalFormat("###")
                                    override fun getFormattedValue(value: Float): String {
                                        return mFormat.format(value) + "번"
                                    }
                                }
                            }

                            //차트 오른쪽 축, Y방향 false처리, 최소,최대
                            axisLeft.apply {
                                isEnabled = false
                                //그래프 가로 축,선 (점선으로 변경)
                                gridColor = R.color.black

                                var count = 0
                                barData.forEachIndexed { index, chartData ->
                                    while (chartData.value > axisMaximum) {
                                        count++
                                        if (chartData.value > axisMaximum) {
                                            axisMaximum += 1F
                                        } else {
                                            axisMaximum = 3F
                                        }
                                    }
                                }
                                granularity = 1F //30단위마다 선을 그리려고 granularity 설정을 해 주었음
                                axisMinimum = 0F
//                              axisMaximum = 3F
                            }
//                          notifyDataSetChanged()
                            this.data = data
                            invalidate()
                        }
                    }
                    setData(listData)

                    val day: TextView = rootView.findViewById(R.id.course_bottom_title_text_day)
                    day.setText(textformatterString)
                    val time: TextView = rootView.findViewById(R.id.course_bottom_title_text_time)
                    time.setText(courseformatterString)

                    val miniTitleTime: Int = result6!!.toInt()
                    val miniTitle: TextView = rootView.findViewById(R.id.course_mini_title)
                    miniTitle.setText(miniTitleTime.toString())

                    val AllCourseCount: TextView =
                        rootView.findViewById(R.id.bottom_sheet_courseAllCount)
                    AllCourseCount.setText(sum.toString())

                    val courseUserName: TextView = rootView.findViewById(R.id.course_name)
                    setUserInfo(courseUserName)
                }
            }

            override fun onFailure(call: Call<chartData>, t: Throwable) {
                Log.d("error", t.message.toString())
            }
        })
    }

        val button: Button = rootView.findViewById(R.id.course_button_screenshot)

        button.setOnClickListener {
            when (requestPermissions()) {
                true -> Instacapture.capture(this.requireActivity(),
                    object : SimpleScreenCapturingListener() {
                        override fun onCaptureComplete(captureview: Bitmap) {
                            val capture: LinearLayout =
                                requireView().findViewById(R.id.courseclear) as LinearLayout
                            val day = SimpleDateFormat("yyyyMMddHHmmss")
                            val date = Date()
                            //공유 버튼 제거
                            val remove: View =
                                rootView.findViewById(R.id.course_share_button_layout)
                            remove.visibility = View.GONE
                            capture.buildDrawingCache()
                            val captureview: Bitmap = capture.getDrawingCache()
                            val uri = saveImageExternal(captureview)
                            uri?.let {
                                shareImageURI(uri)
                            } ?: showError()
                        }
                    },
                    course_button_screenshot)
                else -> showError()
            }
        }
        return rootView
    }


    private fun showError() {
        Toast.makeText(
            this.activity,
            "승인이 필요합니다.",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun requestPermissions(): Boolean {
        var permissions = false
        Dexter.withActivity(this.activity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    permissions = true
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    permissions = false
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken,
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
        return permissions
    }

    fun saveImageExternal(image: Bitmap): Uri? {
        var uri: Uri? = null
        try {
            //저장할 폴더 setting
            val file = File(activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "Course-Clear-Chart.png")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.close()
            uri = FileProvider.getUriForFile(this.requireContext(),
                this.requireActivity().packageName + ".provider",
                file)
        } catch (e: IOException) {
            Log.d("INFO", "공유를 위해 파일을 쓰는 중 IOException: " + e.message)
        }
        return uri
    }


    fun shareImageURI(uri: Uri) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "message/rfc822"
        }

        startActivity(Intent.createChooser(shareIntent, "Send to"))
    }

    private fun setUserInfo(userName: TextView) {
        val userValidation: Retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.6.181:3000/get/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userValidationServer: UserService = userValidation.create(UserService::class.java)

        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            Log.d("UserKey-------","$it")
            userValidationServer.getUserInfo(it).enqueue(object : Callback<UserValidationInfo> {
                override fun onResponse(
                    call: Call<UserValidationInfo>,
                    response: Response<UserValidationInfo>,
                ) {
                    val name = response.body()?.name
                    userName.text = name.toString()
                }

                override fun onFailure(call: Call<UserValidationInfo>, t: Throwable) {
                    Log.e("Error", "user 정보 가져오기 실패")
                }
            })
        }
    }
}