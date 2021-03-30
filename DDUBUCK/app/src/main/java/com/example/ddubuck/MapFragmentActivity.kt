package com.example.ddubuck

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.hardware.*
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import java.util.*
import java.util.logging.Handler
import kotlin.concurrent.timer


//TODO 코드 최적화
/*
목표
MapFragment 는 본인의 일만 충실히 이행해야함
1. 유저 이동 경로 기록 시작/종료 여부에 따른 기록기능 (자유산책)
2. 전달받은 경로를 지도상에 표시하고, 이 경로를 이용한 유저의 기록을 넘겨주는 기능 (코스산책)
3. 기록한 유저 경로를 넘겨주는 기능 ( 자유산책 -> 경로 기록 )
4. 산책중 쌓인 위치 데이터 및 기타 데이터를 넘겨주는 기능
 */


class MapFragmentActivity : FragmentActivity(), OnMapReadyCallback, SensorEventListener {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var map: NaverMap
    private var isRecordStarted=false
    private var path : PathOverlay = PathOverlay()
    private var walkRecord : WalkRecord = WalkRecord()
    private val customPath = PathOverlay()

    //임의로 만든 첫 루트
    private val firstRoute = mutableListOf(
            LatLng(37.56362279298406, 126.90926225749905),
            LatLng(37.56345663522066, 126.9091328029345),
            LatLng(37.56314632623486, 126.90784351195998),
            LatLng(37.56396493508562, 126.90736905196479),
            LatLng(37.56417998056722, 126.90825278385154),
            LatLng(37.56375202367158, 126.90831947940694),
            LatLng(37.56332059071951, 126.90851459284085),
            LatLng(37.56346358071265, 126.909140550899),
            LatLng(37.5637076839577, 126.9092733697774),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.map_fragment_activity)
        var timer : Timer = timer(period = 1000) {}
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        val startButton : Button = findViewById(R.id.start_button)
        startButton.setOnClickListener {
            isRecordStarted=!isRecordStarted
            if(!isRecordStarted) {
                startButton.text="시작"
                startButton.background = ResourcesCompat.getDrawable(resources, R.drawable.start_button_paused_radius, null)
                startButton.setTextColor(Color.parseColor("#FFFFFF"))
                path.map = null

                val intent = Intent(this, MainActivity::class.java)
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this@MapFragmentActivity,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                dlg.setTitle("운동 완료") //제목
                dlg.setMessage("고도 편차: ${walkRecord.altitudes.maxOrNull()?.minus(walkRecord.altitudes.minOrNull()!!)}\n" +
                        "지점 갯수: ${walkRecord.path.size}\n" +
                        "평균속도: ${walkRecord.speeds.average()}\n" +
                        "발걸음 수: ${walkRecord.stepCount}\n" +
                        "이동거리: ${walkRecord.distance}\n" +
                        "경과시간: ${walkRecord.walkTime}초\n" +
                        "소모 칼로리: ${walkRecord.getCalorie()}") // 메시지
                dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    startActivity(intent)
                    finish()
                })
                dlg.show()
                timer.cancel()
                walkRecord = WalkRecord()
            } else {
                timer = timer(period = 1000) {
                    walkRecord.walkTime++
                }
                startButton.text="중지"
                startButton.background = ResourcesCompat.getDrawable(resources, R.drawable.start_button_started_radius, null)
                startButton.setTextColor(Color.parseColor("#000000"))
            }
        }

        //코스 프리셋 1번을 불러옵니다
        val resetCourseButton : Button = findViewById(R.id.test_button_1)
        resetCourseButton.setOnClickListener{
            customPath.map = this.map
            customPath.coords = firstRoute //TODO 넘겨받은 산책 경로로 변경
            customPath.width = 3
        }

        //현재 기록된 코스를 저장합니다
        val createCourseButton : Button = findViewById(R.id.test_button_2)
        createCourseButton.setOnClickListener{
            val userPath = path.coords
            //앱 제작 시 저장대상을 서버로 변경
            customPath.map = this.map
            customPath.coords = userPath
            //TODO 유저 경로 삭제 코드 강화할 것
            path.map = null
            isRecordStarted = false
        }

        //지정된 코스를 삭제합니다
        val deleteCourseButton : Button = findViewById(R.id.test_button_3)
        deleteCourseButton.setOnClickListener{
            customPath.map = null
        }

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

    }

    private val sensorManager by lazy {           // 지연된 초기화는 딱 한 번 실행됨
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent) {  // 가속도 센서 값이 바뀔때마다 호출됨
        if(isRecordStarted) {
            walkRecord.stepCount++
            val stepTextView:TextView = findViewById(R.id.stepCurrent)
            stepTextView.text = walkRecord.stepCount.toString()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                        grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                map.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap
        map.locationSource = locationSource
        map.locationTrackingMode = LocationTrackingMode.Face
        map.uiSettings.isLocationButtonEnabled = false

        val locationButtonView : LocationButtonView = findViewById(R.id.location)
        locationButtonView.map = this.map

        customPath.coords = firstRoute
        customPath.map = naverMap
        customPath.color = Color.CYAN

        map.addOnLocationChangeListener {
            if(isRecordStarted) {
                val lat = locationSource.lastLocation?.latitude
                val lng = locationSource.lastLocation?.longitude
                val speed = locationSource.lastLocation?.speed
                val alt = locationSource.lastLocation?.accuracy
                if(lat!=null&&lng!=null) {
                    val point = LatLng(lat, lng)
                    if(path.coords.isNotEmpty() && path.map != null) {
                        val lastPoint = path.coords.last()
                        if(!isUserReachedToTarget(point, lastPoint)) {
                            if(speed != null && alt != null) {
                                addUserPath(point, lastPoint,path.coords, speed,alt)
                            }
                        }
                        checkCoursePointArrival(point, customPath.coords.first(), customPath.coords)
                    } else {
                        //초기 점이 비어있을 때
                        initUserPath(LatLng(lat, lng))
                    }

                }
            }
        }

        map.setOnMapClickListener{ p: PointF, l: LatLng ->
            println(l)
        }
    }

    //유저 경로 초기화
    private fun initUserPath(currentPos: LatLng) {
        val initRoute = mutableListOf(currentPos, currentPos)
        path.coords = initRoute
        path.map = this.map
    }

    //목표 점에 도달했는가 (근접해있는가)
    private fun isUserReachedToTarget(currentPos: LatLng, lastPos: LatLng) : Boolean {
        return createBound(lastPos).contains(currentPos)
    }

    //유저 경로 추가하면서 생기는 활동들 총집합
    private fun addUserPath(
            currentPos: LatLng,
            lastPos: LatLng,
            currentPath: MutableList<LatLng>,
            speed:Float,
            alt:Float
    ) {
        //마지막 점과 거리 비교해서 +- 0.00005 으로 지정된 *영역*에
        //현재 점이 포함되어있다면 추가하지 않음
        currentPath.add(currentPos)
        walkRecord.path = currentPath
        walkRecord.speeds.add(speed)
        walkRecord.altitudes.add(alt)
        walkRecord.distance+=lastPos.distanceTo(currentPos)
        path.coords = currentPath
    }

    //산책 경로 도달 시
    private fun checkCoursePointArrival(currentPos: LatLng, lastPos: LatLng, course: MutableList<LatLng>) {
        //현재 점에서 마지막(다가오는) 경로 점의 +- 0.00005 으로 지정된 *영역*에
        //현재 점이 포함되어있다면 마지막 경로 점 삭제 및 완료처리
        if(course.size > 2) {
            if(isUserReachedToTarget(currentPos, lastPos)) {
                course.removeAt(0)
                customPath.coords = course
            }
        } else {
            //코스완료
            customPath.map = null
        }
    }

    //비교용 영역 만들기
    private fun createBound(point: LatLng):LatLngBounds {
        // 주어진 좌표에 좌측 상단 0.00005 +
        // 우측 하단 0.00005 -
        // 두 점으로 사각형 구역을 만들어 반환
        val radius = 0.00005
        return LatLngBounds(
                LatLng(point.latitude - radius, point.longitude - radius),
                LatLng(point.latitude + radius, point.longitude + radius),
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

}