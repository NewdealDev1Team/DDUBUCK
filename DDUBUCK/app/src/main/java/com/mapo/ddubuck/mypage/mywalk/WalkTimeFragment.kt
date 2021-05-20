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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mapo.ddubuck.MainActivityViewModel
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.mypagechart.RetrofitChart
import com.mapo.ddubuck.data.mypagechart.chartData
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
import com.mapo.ddubuck.home.HomeMapViewModel
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import id.co.barchartresearch.ChartData
import id.co.barchartresearch.CustomBarChartRender
import kotlinx.android.synthetic.main.fragment_walk_time.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


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

    private val mainViewModel: MainActivityViewModel by activityViewModels()

    private val shareButtonViewImage : Boolean = false

    //뷰 모델
    private val model: HomeMapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainViewModel.toolbarTitle.value = "산책 시간"
        val rootView: View = inflater.inflate(R.layout.fragment_walk_time, container, false)

        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            val userKey : Int = it.toInt()
            Log.d("userKey----","$userKey")
            RetrofitChart.instance.getRestsMypage(userKey).enqueue(object : Callback<chartData> {
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

                        var timeTitleName: String =
                            response.body()?.weekStat?.get(0)?.name.toString()

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
                            val barChartRender =
                                CustomBarChartRender(this, animator, viewPortHandler).apply {
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

                        val titleUserName: TextView = rootView.findViewById(R.id.time_name)

                        setUserInfo(titleUserName)
                    }
                }

                override fun onFailure(call: Call<chartData>, t: Throwable) {
                    Log.d("error", t.message.toString())
                }
            })
        }

        val button: Button = rootView.findViewById(R.id.time_share_button)
        button.setOnClickListener {
            when (requestPermissions()) {
                true -> Instacapture.capture(this.requireActivity(),
                    object : SimpleScreenCapturingListener() {
                        @RequiresApi(Build.VERSION_CODES.Q)
                        override fun onCaptureComplete(captureview: Bitmap) {
                            val capture: FrameLayout =
                                requireView().findViewById(R.id.walktime) as FrameLayout
                            val shareButtonView: View = rootView.findViewById(R.id.time_share_button)
                            shareButtonView.visibility = View.GONE
                            capture.buildDrawingCache()
                            val captureview: Bitmap = capture.getDrawingCache()
                            val uri = saveImageExternal(captureview)
                            uri?.let {
                                if(!shareImageURI(uri)){
                                    shareButtonView.visibility = View.VISIBLE
                                }else {
                                    shareImageURI(uri)
                                }
                            } ?: showError()
                        }
                    },
                    time_share_button)
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
//    fun saveImageExternal(image: Bitmap): Uri? {
//        val v: FrameLayout = requireView().findViewById(R.id.walktime) as FrameLayout
//        var uri: Uri? = null
//        try {
//            //저장할 폴더 setting
//            val file = File(activity?.getExternalFilesDir(Environment.DIRECTORY_SCREENSHOTS),
//                "Walking-Time-Chart.png")
//            val stream = FileOutputStream(file)
//            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
//            stream.close()
//            uri = FileProvider.getUriForFile(this.requireContext(),
//                this.requireActivity().packageName + ".provider",
//                file)
//        } catch (e: IOException) {
//            Log.d("INFO", "공유를 위해 파일을 쓰는 중 IOException: " + e.message)
//        }
//        return uri
//    }
//    <!--    manifast : ScreenShot    -->
//        <provider
//            android:name="androidx.core.content.FileProvider"
//            android:authorities="${applicationId}.provider"
//            android:exported="false"
//            android:grantUriPermissions="true">
//            <meta-data
//                android:name="android.support.FILE_PROVIDER_PATHS"
//                android:resource="@xml/provider_paths" />
//        </provider>
//    provier_paths/xml
//    <?xml version="1.0" encoding="utf-8"?>
//    <paths xmlns:android="http://schemas.android.com/apk/res/android">
//    <root-path name="external_files" path="." />
//    </paths>

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


    fun shareImageURI(uri: Uri) : Boolean {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "message/rfc822"
            type = "image/*"
        }

        startActivity(Intent.createChooser(shareIntent, "Send to"))
        return shareButtonViewImage
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


