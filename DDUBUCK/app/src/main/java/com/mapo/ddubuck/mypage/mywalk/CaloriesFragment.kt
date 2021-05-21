package com.mapo.ddubuck.mypage.mywalk

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.mapo.ddubuck.MainActivityViewModel
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.mypagechart.RetrofitChart
import com.mapo.ddubuck.login.UserService
import com.mapo.ddubuck.login.UserValidationInfo
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
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
import com.mapo.ddubuck.data.mypagechart.MyWalkRecordChartData
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import id.co.barchartresearch.ChartData
import id.co.barchartresearch.CustomBarChartRender
import kotlinx.android.synthetic.main.fragment_calories.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


@RequiresApi(Build.VERSION_CODES.O)
class CaloriesFragment : Fragment() {  //현재 날짜/시간 가져오기
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

    val calorieformatter: DateTimeFormatter = DateTimeFormatter.ofPattern("a HH:mm")
    val calorieformatterString: String = dateNow.format(calorieformatter)

    private lateinit var chart: BarChart

    //일주일 산책 기록 데이터
    var oneWeekRecord = mutableListOf<Float>()

    val listData by lazy {
        mutableListOf(
            ChartData(sixDaysAgo.format(formatter).toString(), oneWeekRecord[0]),
            ChartData(fiveDaysAgo.format(formatter).toString(), oneWeekRecord[1]),
            ChartData(fourDaysAgo.format(formatter).toString(), oneWeekRecord[2]),
            ChartData(threeDaysAgo.format(formatter).toString(), oneWeekRecord[3]),
            ChartData(twoDaysAgo.format(formatter).toString(), oneWeekRecord[4]),
            ChartData(oneDaysAgo.format(formatter).toString(), oneWeekRecord[5]),
            ChartData(dateNow.format(formatter).toString(), oneWeekRecord[6])
        )
    }

    private val mainViewModel: MainActivityViewModel by activityViewModels()

    private val shareButtonViewImage : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainViewModel.toolbarTitle.value = "칼로리"
        val rootView: View = inflater.inflate(R.layout.fragment_calories, container, false)

        val day: TextView = rootView.findViewById(R.id.calorie_bottom_title_text_day)
        day.setText(textformatterString)
        val time: TextView = rootView.findViewById(R.id.calorie_bottom_title_text_time)
        time.setText(calorieformatterString)
        val miniTitle: TextView = rootView.findViewById(R.id.calories_mini_title)
        val calorieUserName: TextView = rootView.findViewById(R.id.calorie_name)
        setOneWeekRecordInfo(miniTitle,calorieUserName)

        chart = rootView.findViewById(R.id.calories_bar_chart)
        chart.setNoDataText("Loading")
        initChart(chart)

        val button: Button = rootView.findViewById(R.id.calorie_share_button)
        val shareButtonView: View = rootView.findViewById(R.id.calorie_share_button)
        button.setOnClickListener { takeAndShareScreenShot(shareButtonView)}

        return rootView
    }
    // --- 캡처 후 공유 --
    private fun takeAndShareScreenShot(shareButtonView: View) {
        Instacapture.capture(this.requireActivity(),
            object : SimpleScreenCapturingListener() {
                override fun onCaptureComplete(captureview: Bitmap) {
                    val capture: FrameLayout =
                        requireView().findViewById(R.id.calorie) as FrameLayout
                    shareButtonView.visibility = View.GONE
                    capture.buildDrawingCache()
                    val captureview: Bitmap = capture.getDrawingCache()
                    val uri = saveImageExternal(captureview)
                    uri?.let {
                        if (!shareImageURI(uri)) {
                            shareButtonView.visibility = View.VISIBLE
                        } else {
                            shareImageURI(uri)
                        }
                    }
                }
            }, calorie_share_button)
    }
    fun saveImageExternal(image: Bitmap): Uri? {
        val filename = "DDUBUCK_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var uri: Uri? = null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        //use application context to get contentResolver
        val contentResolver = this.requireActivity().contentResolver

        contentResolver.also { resolver ->
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = uri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { image.compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(uri!!, contentValues, null, null)

        return uri!!
    }

    fun shareImageURI(uri: Uri) :Boolean {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "message/rfc822"
            type = "image/*"
        }

        startActivity(Intent.createChooser(shareIntent, "Send to"))
        return shareButtonViewImage
    }
    // -- 바 차트 커스텀 --
    fun initChart(chart: BarChart){
        with(chart) {
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
    }

    fun setData(barData: List<ChartData>) {
        val values = mutableListOf<BarEntry>()
        barData.forEachIndexed { index, chartData ->
            values.add(BarEntry(index.toFloat(), chartData.value))
        }

        val barDataSet = BarDataSet(values, "").apply {
            setDrawValues(false)

            val colors = ArrayList<Int>()
            colors.add(Color.argb(55, 250, 168, 46));
            colors.add(Color.argb(55, 250, 168, 46));
            colors.add(Color.argb(55, 250, 168, 46));
            colors.add(Color.argb(55, 250, 168, 46));
            colors.add(Color.argb(55, 250, 168, 46));
            colors.add(Color.argb(55, 250, 168, 46));
            colors.add(Color.argb(200, 250, 168, 46));
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
                            axisMaximum += 300F
                        } else {
                            axisMaximum = 600F
                        }
                    }
                }
                granularity = 200F //200단위마다 선을 그리려고 granularity 설정을 해 주었음
                axisMinimum = 0F
            }

            //차트 오른쪽 축, Y방향 false처리
            axisLeft.apply {
                isEnabled = false
                //그래프 가로 축,선 (점선으로 변경)
                gridColor = R.color.black

                var count = 0
                barData.forEachIndexed { index, chartData ->
                    while (chartData.value > axisMaximum) {
                        count++
                        if (chartData.value > axisMaximum) {
                            axisMaximum += 300F
                        } else {
                            axisMaximum = 600F
                        }
                    }
                }
                granularity = 200F
                //30단위마다 선을 그리려고 granularity 설정을 해 주었음
                axisMinimum = 0F
            }
            notifyDataSetChanged()
            this.data = data
            invalidate()
        }
    }

    //  -- 산책 기록 API Call --
    fun setOneWeekRecordInfo(miniTitle: TextView,calorieUserName: TextView){
        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            var userKey : Int = it.toInt()
            Log.d("userKe","$userKey")
            RetrofitChart.instance.getRestsMypage(userKey).enqueue(object : Callback<MyWalkRecordChartData> {
                override fun onResponse(call: Call<MyWalkRecordChartData>, response: Response<MyWalkRecordChartData>) {
                    if (response.isSuccessful) {
                        Log.d("text", "연결성공")
                        var result0 = response.body()?.weekStat?.get(0)?.calorie?.toFloat()
                        var result1 = response.body()?.weekStat?.get(1)?.calorie?.toFloat()
                        var result2 = response.body()?.weekStat?.get(2)?.calorie?.toFloat()
                        var result3 = response.body()?.weekStat?.get(3)?.calorie?.toFloat()
                        var result4 = response.body()?.weekStat?.get(4)?.calorie?.toFloat()
                        var result5 = response.body()?.weekStat?.get(5)?.calorie?.toFloat()
                        var result6 = response.body()?.weekStat?.get(6)?.calorie?.toFloat()
                        Log.d("~~0번째~~~",
                            " $result0 , $result1, $result2, $result3, $result4, $result5, $result6")

                        val sum: Int = (result0!!.toInt() + result1!!.toInt() + result2!!.toInt()
                                + result3!!.toInt() + result4!!.toInt() + result5!!.toInt())

                        oneWeekRecord.add(result0!!)
                        oneWeekRecord.add(result1!!)
                        oneWeekRecord.add(result2!!)
                        oneWeekRecord.add(result3!!)
                        oneWeekRecord.add(result4!!)
                        oneWeekRecord.add(result5!!)
                        oneWeekRecord.add(result6!!)
                        setData(listData)

                        val miniTitleTime: Int = result6!!.toInt()
                        miniTitle.setText(miniTitleTime.toString())
                        setUserInfo(calorieUserName)
                    }
                }

                override fun onFailure(call: Call<MyWalkRecordChartData>, t: Throwable) {
                    Log.d("error", t.message.toString())
                }
            })
        }

    }

    private fun setUserInfo(userName: TextView) {
        val userValidation: Retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.6.181:3000/get/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userValidationServer: UserService = userValidation.create(UserService::class.java)

        context?.let { UserSharedPreferences.getUserId(it) }?.let {
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