package com.example.ddubuck

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.hardware.*
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.UiThread
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
import kotlin.concurrent.timer


//TODO 코드 최적화
/*
목표
1. CRUD 구현
Create
 - startRecording - stopRecording
Read
 - getWalkResult
Update
 - X
Delete
 - X
2. 백그라운드 작동

 */


class MapFragmentActivity : FragmentActivity(), OnMapReadyCallback, SensorEventListener {
    //환경설정 변수
    private lateinit var locationSource: FusedLocationSource
    private lateinit var map: NaverMap
    private lateinit var timer : Timer
    private val sensorManager by lazy { // 지연된 초기화는 딱 한 번 실행됨
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    //산책 시작 여부
    private var isRecordStarted=false
    private var isCourseSelected=false
    //측정 관련 변수
    private var userPath  = PathOverlay()
    private var altitudes : MutableList<Float> = mutableListOf()
    private var speeds : MutableList<Float> = mutableListOf()
    private var walkTime : Long = 0
    private var stepCount : Int = 0
    private var distance : Double = 0.0

    //코스
    private var course = PathOverlay()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var walkRecord : WalkRecord
        setContentView(R.layout.map_fragment_activity)
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        //시작버튼
        val startButton : Button = findViewById(R.id.start_button)
        startButton.setOnClickListener {
            isRecordStarted=!isRecordStarted
            if(!isRecordStarted) {
                stopRecording(startButton)
                walkRecord = getWalkResult()
                showResultDialog(walkRecord)
            } else {
                startRecording(startButton)
            }
        }

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

    }

    //버튼 텍스트 바꾸고 산책시작
    private fun startRecording(startButton:Button) {
        startButton.text="중지"
        startButton.background = ResourcesCompat.getDrawable(resources, R.drawable.start_button_started_radius, null)
        startButton.setTextColor(Color.parseColor("#000000"))
        timer = timer(period = 1000) {
            walkTime++
        }
    }

    //산책을 종료하고 기록을 반환합니다
    private fun stopRecording(startButton: Button) {
        startButton.text="시작"
        startButton.background = ResourcesCompat.getDrawable(resources, R.drawable.start_button_paused_radius, null)
        startButton.setTextColor(Color.parseColor("#FFFFFF"))
        userPath.map = null
        timer.cancel()
    }

    //산책기록을 반환합니다
    private fun getWalkResult():WalkRecord {
        return WalkRecord(
                userPath.coords,
                altitudes,
                speeds,
                walkTime,
                stepCount,
                distance,
        )
    }

    //사용자가 이동한 경로를 저장합니다
    //TODO 유저 경로 저장
    private fun saveUserRoute(p:List<LatLng>) {
        //임시코드 : 코스 경로로 저장합니다
        //앱 제작 시 저장대상을 서버로 변경
        addCoursePath(p)
        deleteUserRoute()
    }

    //유저가 이동하며 기록한 경로를 삭제합니다
    //TODO 유저 경로 삭제
    private fun deleteUserRoute() {
        userPath.map = null
    }


    //언젠가 사라질 다이알로그 띄우기
    private fun showResultDialog(walkRecord:WalkRecord) {
        val intent = Intent(this, MainActivity::class.java)
        val dlg: AlertDialog.Builder = AlertDialog.Builder(this@MapFragmentActivity,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        dlg.setTitle("운동 완료") //제목
        dlg.setMessage("고도 편차: ${altitudes.maxOrNull()?.minus(walkRecord.altitudes.minOrNull()!!)}\n" +
                "지점 갯수: ${userPath.coords.size}\n" +
                "평균속도: ${speeds.average()}\n" +
                "발걸음 수: ${stepCount}\n" +
                "이동거리: ${distance}\n" +
                "경과시간: ${walkTime}초\n" +
                "소모 칼로리: ${walkRecord.getCalorie()}") // 메시지
        dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            startActivity(intent)
            finish()
        })
        dlg.show()
    }

    //코스 경로 추가하기
    private fun addCoursePath(p:List<LatLng>) {
        course.coords = p
        course.map = this.map
        isCourseSelected = true
    }

    //코스 경로 삭제하기
    private fun clearCoursePath() {
        course.map = null
        isCourseSelected = false
    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent) {  // 가속도 센서 값이 바뀔때마다 호출됨
        if(isRecordStarted) {
            stepCount++
            val stepTextView:TextView = findViewById(R.id.stepCurrent)
            stepTextView.text = stepCount.toString()
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

        course.color = Color.CYAN

        userPath = PathOverlay()

        map.addOnLocationChangeListener {
            if(isRecordStarted) {
                val lat = locationSource.lastLocation?.latitude
                val lng = locationSource.lastLocation?.longitude
                val speed = locationSource.lastLocation?.speed
                val alt = locationSource.lastLocation?.accuracy
                if(lat!=null&&lng!=null) {
                    val point = LatLng(lat, lng)
                    if(userPath.coords.isNotEmpty() && userPath.map != null) {
                        val lastPoint = userPath.coords.last()
                        if(!isUserReachedToTarget(point, lastPoint)) {
                            if(speed != null && alt != null) {
                                addUserPath(point, lastPoint,userPath.coords, speed,alt)
                            }
                        }
                        if(isCourseSelected) {
                            if(course.coords.isNotEmpty()) {
                                checkCoursePointArrival(point, course.coords.first(), course.coords)
                            }
                        }
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
        userPath.coords = initRoute
        userPath.map = this.map
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
        speeds.add(speed)
        altitudes.add(alt)
        distance+=lastPos.distanceTo(currentPos)
        userPath.coords = currentPath
    }

    //산책 경로 도달 시
    private fun checkCoursePointArrival(currentPos: LatLng, lastPos: LatLng, course: MutableList<LatLng>) {
        //현재 점에서 마지막(다가오는) 경로 점의 +- 0.00005 으로 지정된 *영역*에
        //현재 점이 포함되어있다면 마지막 경로 점 삭제 및 완료처리
        if(course.size > 2) {
            if(isUserReachedToTarget(currentPos, lastPos)) {
                course.removeAt(0)
                this.course.coords = course
            }
        } else {
            //코스완료
            this.course.map = null
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