package com.example.ddubuck.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.PointF
import android.hardware.*
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.example.ddubuck.R
import com.example.ddubuck.data.home.WalkRecord
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import java.text.DecimalFormat
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


class HomeMapFragment(private val fm: FragmentManager, owner: Activity) : Fragment(),
        OnMapReadyCallback, SensorEventListener {
    //뷰모델
    private val model: HomeMapViewModel by activityViewModels()

    //환경설정 변수
    private lateinit var map: NaverMap
    private lateinit var timer: Timer
    private lateinit var locationButtonView: LocationButtonView
    private lateinit var locationSource: FusedLocationSource
    private val sensorManager by lazy { // 지연된 초기화는 딱 한 번 실행됨
        owner.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private var isLocationFirstChanged = false


    //산책 시작 여부
    var allowRecording = false
    var isRestarted = false
    var isCourseSelected = false

    //측정 관련 변수
    private var userPath = PathOverlay()
    private var altitudes: MutableList<Float> = mutableListOf()
    private var speeds: MutableList<Float> = mutableListOf()
    private var walkTime: Long = 0
    private var timePoint: Long = 0
    private var stepCount: Int = 0
    private var distance: Double = 0.0
    private var burnedCalorie: Double = 0.0

    //코스
    private var course = PathOverlay()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        val nMapFragment = fm.findFragmentById(R.id.map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.map, it).commit()
                }
        nMapFragment.getMapAsync(this)
        locationButtonView = rootView.findViewById(R.id.location)

        model.isRecordStarted.observe(viewLifecycleOwner, { v ->
            if (v) {
                //start
                startRecording()
                allowRecording = true
            } else {
                //stop
                stopRecording()
                allowRecording = false
                isRestarted = true
                isCourseSelected = false
            }
        })



        model.isRecordPaused.observe(viewLifecycleOwner, { v ->
            allowRecording = if (v) {
                //start
                pauseRecording()
                false
            } else {
                //stop
                resumeRecording()
                true
            }
        })

        model.isCourseWalk.observe(viewLifecycleOwner, { v -> isCourseSelected = v })
        model.coursePath.observe(viewLifecycleOwner, { v -> course.coords = v.toMutableList() })
        return rootView
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //버튼 텍스트 바꾸고 산책시작
    private fun startRecording() {
        timer = timer(period = 1000) {
            model.recordTime(walkTime)
            walkTime++
        }
    }

    //산책을 일시정지 합니다
    private fun pauseRecording() {
        timer.cancel()
    }

    private fun resumeRecording() {
        timer = timer(period = 1000) {
            model.recordTime(walkTime)
            walkTime++
        }
    }

    //산책을 종료하고 기록을 반환합니다
    private fun stopRecording() {
        userPath.map = null
        timer.cancel()
        course.map = null
        //기록 및 반환 코드
        showResultDialog(getWalkResult())
        //
        //------ 수 정 하 라 !!!!!!!!
        model.walkTime.value = 0
        model.walkCalorie.value = 0.0
        model.walkDistance.value = 0.0
        //-------
        altitudes.clear()
        speeds.clear()
        stepCount = 0
        distance = 0.0
        timePoint = 0
        walkTime = 0
        burnedCalorie = 0.0
    }

    //산책기록을 반환합니다
    private fun getWalkResult(): WalkRecord {
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
    private fun saveUserRoute(p: List<LatLng>) {
        //임시코드 : 코스 경로로 저장합니다
        //앱 제작 시 저장대상을 서버로 변경
        addCoursePath(p)
    }


    //언젠가 사라질 다이알로그 띄우기
    fun showResultDialog(walkRecord: WalkRecord) {
        val dlg: AlertDialog.Builder = AlertDialog.Builder(
                context,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
        )
        dlg.setTitle("운동 완료") //제목
        dlg.setMessage(
                "고도 편차: ${altitudes.maxOrNull()?.minus(walkRecord.altitudes.minOrNull()!!)}\n" +
                        "지점 갯수: ${userPath.coords.size}\n" +
                        "평균속도: ${speeds.average()}\n" +
                        "발걸음 수: ${stepCount}\n" +
                        "이동거리: ${DecimalFormat("#.## m").format(distance)}\n" +
                        "경과시간: ${DateUtils.formatElapsedTime(walkTime)}\n" +
                        "소모 칼로리: ${DecimalFormat("#.## kcal").format(walkRecord.getCalorie(65.0))}"
        ) // 메시지
        dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which -> })
        dlg.show()
    }

    //코스 경로 추가하기
    private fun addCoursePath(p: List<LatLng>) {
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
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onSensorChanged(event: SensorEvent) {  // 가속도 센서 값이 바뀔때마다 호출됨
        if (allowRecording) {
            stepCount++
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap
        if (!locationSource.isActivated) { // 권한 거부됨
            map.locationTrackingMode = LocationTrackingMode.None
        }
        map.locationSource = locationSource
        map.locationTrackingMode = LocationTrackingMode.Face
        map.uiSettings.isLocationButtonEnabled = false

        locationButtonView.map = this.map

        course.color = Color.CYAN

        userPath = PathOverlay()

        map.addOnLocationChangeListener {
            if(!isLocationFirstChanged) {
                model.recordPosition(
                        LatLng(
                                locationSource.lastLocation?.latitude!!,
                                locationSource.lastLocation?.longitude!!
                        )
                )
                isLocationFirstChanged=true
            }


            if (allowRecording) {
                val lat = locationSource.lastLocation?.latitude
                val lng = locationSource.lastLocation?.longitude
                val speed = locationSource.lastLocation?.speed
                val alt = locationSource.lastLocation?.accuracy
                if (lat != null && lng != null) {
                    if (isRestarted) {
                        //초기 점이 비어있을 때
                        initUserPath(LatLng(lat, lng))
                        isRestarted = false
                    }
                    val point = LatLng(lat, lng)
                    if (userPath.coords.isNotEmpty() && userPath.map != null) {
                        val lastPoint = userPath.coords.last()
                        if (!isUserReachedToTarget(point, lastPoint)) {
                            if (speed != null && alt != null) {
                                addUserPath(point, lastPoint, userPath.coords, speed, alt)
                            }
                        }
                        if (isCourseSelected) {
                            if (course.map != null) {
                                checkCoursePointArrival(point, course.coords.first(), course.coords)
                            } else {
                                course.map = this.map
                            }
                        }
                    } else {
                        //초기 점이 비어있을 때
                        initUserPath(LatLng(lat, lng))
                    }

                }
            }
        }

        map.setOnMapClickListener { p: PointF, l: LatLng ->
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
    private fun isUserReachedToTarget(currentPos: LatLng, lastPos: LatLng): Boolean {
        return createBound(lastPos).contains(currentPos)
    }

    //유저 경로 추가하면서 생기는 활동들 총집합
    private fun addUserPath(
            currentPos: LatLng,
            lastPos: LatLng,
            currentPath: MutableList<LatLng>,
            speed: Float,
            alt: Float
    ) {
        if (timePoint != 0.toLong()) {
            burnedCalorie += calculateMomentCalorie(speed, walkTime - timePoint)
            timePoint = walkTime
        } else {
            burnedCalorie += calculateMomentCalorie(speed, walkTime)
            timePoint = walkTime
        }
        //마지막 점과 거리 비교해서 +- 0.00005 으로 지정된 *영역*에
        //현재 점이 포함되어있다면 추가하지 않음
        currentPath.add(currentPos)
        speeds.add(speed)
        altitudes.add(alt)
        distance += lastPos.distanceTo(currentPos)
        model.recordDistance(distance)
        model.recordCalorie(burnedCalorie)
        userPath.coords = currentPath
    }

    //순간 칼로리 연산
    private fun calculateMomentCalorie(speed: Float, passedTime: Long): Double {
        val weight = 65
        val met = when (speed) {
            in 0.0..0.09 -> 0.0
            in 0.1..4.0 -> 2.0 // 느리게 걷기
            in 4.0..8.0 -> 3.8 // 보통 걷기
            in 8.0..12.0 -> 4.0 // 빠르게 걷기
            else -> 5.0 // 전력질주
        }
        val time = passedTime / 60.0
        return (met * (3.5 * weight * time)) * 0.001 * 5
    }

    //산책 경로 도달 시
    private fun checkCoursePointArrival(
            currentPos: LatLng,
            lastPos: LatLng,
            course: MutableList<LatLng>
    ) {
        //현재 점에서 마지막(다가오는) 경로 점의 +- 0.00005 으로 지정된 *영역*에
        //현재 점이 포함되어있다면 마지막 경로 점 삭제 및 완료처리
        if (course.size > 2) {
            if (isUserReachedToTarget(currentPos, lastPos)) {
                course.removeAt(0)
                model.passPathData(course)
                this.course.coords = course
            }
        } else {
            //코스완료
            this.course.map = null
            //bottom sheet pop 해서 코스선택 메뉴로 이동시키기
            stopRecording()
            parentFragmentManager.popBackStack()
        }
    }

    //비교용 영역 만들기
    private fun createBound(point: LatLng): LatLngBounds {
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
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}