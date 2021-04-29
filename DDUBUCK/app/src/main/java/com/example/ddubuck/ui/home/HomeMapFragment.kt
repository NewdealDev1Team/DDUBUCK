package com.example.ddubuck.ui.home

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.hardware.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.ui.home.bottomSheet.BottomSheetCompleteFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import java.util.*
import kotlin.concurrent.timer



class HomeMapFragment(private val fm: FragmentManager, private val owner: Activity) : Fragment(),
        OnMapReadyCallback, SensorEventListener {


    //산책 시작 여부
    //TODO background operation
    var allowRecording = false
    var isRestarted = false
    var isCourseSelected = false
    var isCourseInitialized = false

    //TODO STATE로 운용할 것
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        const val WALK_WAITING = 200 //산책대기중
        const val WALK_START = 300 //산책시작 : initializing
        const val WALK_PROGRESS = 301 //산책진행
        const val WALK_PAUSE = 302 //산책일시중지
        const val WALK_COURSE = 400 // 코스산책
        const val WALK_FREE = 100 //자유산책
    }

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
    private var course = PolylineOverlay()
    private var courseMarker = Marker()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        model.walkState.value = WALK_START
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
                model.walkState.value = WALK_PROGRESS
            } else {
                //stop
                stopRecording()
                allowRecording = false
                isRestarted = true
                isCourseSelected = false
                isCourseInitialized = false
                model.walkState.value = WALK_WAITING
            }
        })

        model.isRecordPaused.observe(viewLifecycleOwner, { v ->
            allowRecording = if (v) {
                //start
                pauseRecording()
                model.walkState.value = WALK_PAUSE
                false
            } else {
                //stop
                resumeRecording()
                model.walkState.value = WALK_PROGRESS
                true
            }
        })

        model.isCourseWalk.observe(viewLifecycleOwner, { v -> isCourseSelected = v})
        model.courseProgressPath.observe(viewLifecycleOwner, { v -> course.coords = v.toMutableList() })
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
        courseMarker.map = null
        //기록 및 반환 코드
        val walkTag = if(isCourseSelected)
            WALK_COURSE
        else
            WALK_FREE
        parentFragmentManager.beginTransaction()
                .replace(R.id.bottom_sheet_container, BottomSheetCompleteFragment(owner,getWalkResult(), walkTag),
                        HomeFragment.BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(MainActivity.HOME_RESULT_TAG)
                .commit()
        //RetrofitService().createPost(getWalkResult())

        model.walkTime.value = 0
        model.walkCalorie.value = 0.0
        model.walkDistance.value = 0.0

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
                altitudes.average(),
                speeds.average(),
                walkTime,
                stepCount,
                distance,
                Date()
        )
    }


    private fun cameraToCourse(v:List<LatLng>) {
        if(v.size >2){
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(v[0], 16.0).animate(CameraAnimation.Fly)
            map.moveCamera(cameraUpdate)
            course.coords = v
            course.map = map
        } else {
            course.map = null
        }
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

        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_NORMAL
        )

        map.locationSource = locationSource
        map.locationTrackingMode = LocationTrackingMode.Face
        map.uiSettings.isLocationButtonEnabled = false

        locationButtonView.map = this.map

        course.color = Color.parseColor("#2798E7")
        course.width = 15
        course.capType = PolylineOverlay.LineCap.Round
        course.joinType = PolylineOverlay.LineJoin.Round

        //courseMarker.iconTintColor = Color.parseColor("#2798E7")
        courseMarker.icon = MarkerIcons.BLUE

        model.coursePath.observe(viewLifecycleOwner, { v->
            cameraToCourse(v)
        })
        model.walkState.value = WALK_WAITING

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
                //https://developer.android.com/reference/android/location/Location#getSpeed()
                val speed = locationSource.lastLocation?.speed
                //https://developer.android.com/reference/android/location/Location#getAltitude()
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
                            if(isCourseInitialized) {
                                //TODO 다음 경로 위치 알 수 있는 방법 고민 할 것
                                if (course.map != null) {
                                    checkCoursePointArrival(point, course.coords)
                                } else {
                                    course.map = this.map
                                }
                            } else {
                                val v = course.coords.toMutableList()
                                //last 가 맞는지 확인 할 것
                                v.add(v.last())
                                v.add(v.last())
                                course.coords=v
                                courseMarker.position = v.first()
                                courseMarker.map = this.map
                                isCourseInitialized=true
                            }
                        }
                    } else {
                        //초기 점이 비어있을 때
                        initUserPath(LatLng(lat, lng))
                    }

                }
            }
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
            course: MutableList<LatLng>
    ) {
        //현재 점에서 마지막(다가오는) 경로 점의 +- 0.00005 으로 지정된 *영역*에
        //현재 점이 포함되어있다면 마지막 경로 점 삭제 및 완료처리
        if (course.size > 2) {
            if (isUserReachedToTarget(currentPos, course.first())) {
                course.removeAt(0)
                //진동
                model.vibrate(true)
                courseMarker.position = course.first()
                model.passProgressData(course)
                this.course.coords = course
            }
        } else {
            //코스완료
            this.course.map = null
            //bottom sheet pop 해서 코스선택 메뉴로 이동시키기
            parentFragmentManager.popBackStack()
        }
    }

    //비교용 영역 만들기
    private fun createBound(point: LatLng): LatLngBounds {
        // 주어진 좌표에 좌측 상단 0.00007 +
        // 우측 하단 0.00007 -
        // 두 점으로 사각형 구역을 만들어 반환
        val radius = 0.00007
        return LatLngBounds(
                LatLng(point.latitude - radius, point.longitude - radius),
                LatLng(point.latitude + radius, point.longitude + radius),
        )
    }
}