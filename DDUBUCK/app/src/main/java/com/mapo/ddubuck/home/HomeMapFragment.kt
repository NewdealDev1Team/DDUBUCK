package com.mapo.ddubuck.home

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.*
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.RetrofitClient
import com.mapo.ddubuck.data.RetrofitService
import com.mapo.ddubuck.data.home.CourseItem
import com.mapo.ddubuck.data.home.WalkRecord
import com.mapo.ddubuck.data.publicdata.PublicData
import com.mapo.ddubuck.data.publicdata.RecommendPathDTO
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.mapo.ddubuck.ui.CommonDialog
import com.mapo.ddubuck.home.bottomSheet.BottomSheetCompleteFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.timer



class HomeMapFragment(private val fm: FragmentManager, private val owner: Activity) : Fragment(),
        OnMapReadyCallback, SensorEventListener {

    //산책 시작 여부
    private var allowRecording = false
    private var isRestarted = false
    private var isCourseSelected = false
    private var isCourseInitialized = false

    private var walkTag = WALK_FREE

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        const val WALK_WAITING = 200 //산책대기중
        const val WALK_START = 300 //산책시작 : initializing
        const val WALK_PROGRESS = 301 //산책진행
        const val WALK_PAUSE = 302 //산책일시중지
        const val WALK_COURSE = 400 // 코스산책
        const val WALK_FREE = 100 //자유산책
        const val WALK_COURSE_COMPLETE = 401
    }

    //뷰모델
    private val model: HomeMapViewModel by activityViewModels()

    //환경설정 변수
    private val userKey : String = UserSharedPreferences.getUserId(owner)
    private lateinit var map: NaverMap
    private lateinit var timer: Timer
    private lateinit var locationSource: FusedLocationSource
    private val locationThread = BackgroundLocationThread("Background_Location","1")
    private val sensorManager by lazy {
        owner.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private var markers:HashMap<String, MutableList<Marker>> = HashMap()
    private val sharedPref = UserSharedPreferences
    private var isLocationDataInitialized = false

    //측정 관련 변수
    private var userPath = PathOverlay()
    private var altitudes: MutableList<Float> = mutableListOf()
    private var speeds: MutableList<Float> = mutableListOf()
    private var walkTime: Long = 0
    private var timePoint: Long = 0
    private var stepCount: Int = 0
    private var distance: Double = 0.0
    private var burnedCalorie: Double = 0.0
    private var initialPosition : LatLng = LatLng(0.0,0.0)

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

        model.isRecordStarted.observe(viewLifecycleOwner, { v ->
            if (v) {
                //start
                startRecording()
                allowRecording = true
                isRestarted = true
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


    private fun initPublicData(x:Double, y:Double) {

        markers[FilterDrawer.PUBLIC_TOILET] = mutableListOf()
        markers[FilterDrawer.PUBLIC_REST_AREA] = mutableListOf()
        markers[FilterDrawer.PET_RESTAURANT] = mutableListOf()
        markers[FilterDrawer.PET_CAFE] = mutableListOf()
        markers[FilterDrawer.CAR_FREE_ROAD]= mutableListOf()
        markers[FilterDrawer.CAFE]= mutableListOf()

        RetrofitClient.publicDataInstance.getResult(x,y)
            .enqueue(object : Callback<PublicData> {
                override fun onResponse(call: Call<PublicData>, response: Response<PublicData>) {
                    if(response.body() != null) {
                        val publicData = response.body()!!
                        val markerHeightSize = 80
                        val markerWidthSize = 60
                        for (i in publicData.petCafe) {
                            val marker = Marker()
                            marker.position = LatLng(i.x, i.y)
                            if(sharedPref.getFilterVisible(owner, FilterDrawer.PET_CAFE)) {
                                marker.map = map
                            }
                            marker.icon = MarkerIcons.BLUE
                            marker.height = markerHeightSize
                            marker.width = markerWidthSize
                            marker.isHideCollidedMarkers = true
                            marker.setOnClickListener {
                                //dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                CommonDialog("반려견 출입가능 카페","업체명 : ${i.name}\n주소 : ${i.address}", owner).let {
                                    it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    it.show()
                                }
                                true
                            }
                            markers[FilterDrawer.PET_CAFE]!!.add(marker)
                        }
                        for (i in publicData.carFreeRoad) {
                            val marker = Marker()
                            marker.position = LatLng(i.path[0].x, i.path[0].y)
                            if(sharedPref.getFilterVisible(owner, FilterDrawer.CAR_FREE_ROAD)) {
                                marker.map = map
                            }
                            marker.icon = MarkerIcons.RED
                            marker.height = markerHeightSize
                            marker.width = markerWidthSize
                            marker.isHideCollidedMarkers = true
                            marker.setOnClickListener {
                                CommonDialog("차없는 도로", "도로명 : ${i.name}\n운영시간 : ${i.time}", owner).let {
                                    it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    it.show()
                                }
                                true
                            }
                            markers[FilterDrawer.CAR_FREE_ROAD]!!.add(marker)
                        }
                        for (i in publicData.cafe) {
                            val marker = Marker()
                            marker.position = LatLng(i.x, i.y)
                            if(sharedPref.getFilterVisible(owner, FilterDrawer.CAFE)) {
                                marker.map = map
                            }
                            marker.icon = MarkerIcons.GRAY
                            marker.height = markerHeightSize
                            marker.width = markerWidthSize
                            marker.isHideCollidedMarkers = true
                            marker.setOnClickListener{
                                CommonDialog("카페","업체명 : ${i.name}\n주소 : ${i.address}", owner).let {
                                    it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    it.show()
                                }
                                true
                            }
                            markers[FilterDrawer.CAFE]!!.add(marker)
                        }
                        for (i in publicData.petRestaurant) {
                            val marker = Marker()
                            marker.position = LatLng(i.x, i.y)
                            if(sharedPref.getFilterVisible(owner, FilterDrawer.PET_RESTAURANT)) {
                                marker.map = map
                            }
                            marker.icon = MarkerIcons.PINK
                            marker.height = markerHeightSize
                            marker.width = markerWidthSize
                            marker.isHideCollidedMarkers = true
                            marker.setOnClickListener {
                                CommonDialog("반려견 출입가능 식당", "업체명 : ${i.name}\n주소 : ${i.address}", owner).let {
                                    it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    it.show()
                                }
                                true
                            }
                            markers[FilterDrawer.PET_RESTAURANT]!!.add(marker)
                        }
                        for (i in publicData.publicRestArea) {
                            val marker = Marker()
                            marker.position = LatLng(i.x, i.y)
                            if(sharedPref.getFilterVisible(owner, FilterDrawer.PUBLIC_REST_AREA)) {
                                marker.map = map
                            }
                            marker.icon = MarkerIcons.GREEN
                            marker.height = markerHeightSize
                            marker.width = markerWidthSize
                            marker.isHideCollidedMarkers = true
                            marker.setOnClickListener {
                                CommonDialog("공공쉼터",i.name, owner).let {
                                    it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    it.show()
                                }
                                true
                            }
                            markers[FilterDrawer.PUBLIC_REST_AREA]!!.add(marker)
                        }
                        for (i in publicData.publicToilet) {
                            val marker = Marker()
                            marker.position = LatLng(i.x, i.y)
                            if(sharedPref.getFilterVisible(owner, FilterDrawer.PUBLIC_TOILET)) {
                                marker.map = map
                            }
                            marker.icon = MarkerIcons.YELLOW
                            marker.height = markerHeightSize
                            marker.width = markerWidthSize
                            marker.isHideCollidedMarkers = true
                            marker.setOnClickListener {
                                CommonDialog("공공화장실",i.name, owner).let {
                                    it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    it.show()
                                }
                                true
                            }
                            markers[FilterDrawer.PUBLIC_TOILET]!!.add(marker)
                        }
                        val recommendPath = mutableListOf<RecommendPathDTO>()
                        recommendPath.addAll(publicData.recommendPathMaster)
                        recommendPath.addAll(publicData.recommendPathUser)
                        if(recommendPath.isNotEmpty()) {
                            val courseData = mutableListOf<CourseItem>()
                            for (i in recommendPath) {
                                courseData.add(i.toCourseItem())
                                model.recommendPath.value = courseData
                            }
                            //home에다가 보내기
                        }
                    }
                }

                override fun onFailure(call: Call<PublicData>, t: Throwable) {
                    Log.e("publicDataFetch", "FAILED!")
                }

            })

        model.showCafe.observe(viewLifecycleOwner, {v->
            markers[FilterDrawer.CAFE]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })

        model.showCarFreeRoad.observe(viewLifecycleOwner, {v->
            markers[FilterDrawer.CAR_FREE_ROAD]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })

        model.showPetCafe.observe(viewLifecycleOwner, {v->
            markers[FilterDrawer.PET_CAFE]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })

        model.showPetRestaurant.observe(viewLifecycleOwner, {v->
            markers[FilterDrawer.PET_RESTAURANT]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })

        model.showPublicRestArea.observe(viewLifecycleOwner, {v->
            markers[FilterDrawer.PUBLIC_REST_AREA]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })

        model.showPublicToilet.observe(viewLifecycleOwner, {v->
            markers[FilterDrawer.PUBLIC_TOILET]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })
    }

    /**버튼 텍스트 바꾸고 산책시작**/
    private fun startRecording() {
        timer = timer(period = 1000) {
            model.recordTime(walkTime)
            walkTime++
        }
    }

    /**산책을 일시정지 합니다**/
    private fun pauseRecording() {
        timer.cancel()
    }

    private fun resumeRecording() {
        timer = timer(period = 1000) {
            model.recordTime(walkTime)
            walkTime++
        }
    }

    /**산책을 종료하고 기록을 반환합니다**/
    private fun stopRecording() {
        userPath.map = null
        timer.cancel()
        course.map = null
        courseMarker.map = null
        //기록 및 반환 코드


        val walkRecord = getWalkResult()

        //RetrofitService().createRecord(userKey, walkRecord, walkTag)
        parentFragmentManager.beginTransaction()
                .replace(R.id.bottom_sheet_container, BottomSheetCompleteFragment(owner,walkRecord,userKey, walkTag),
                        HomeFragment.BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(MainActivity.HOME_RESULT_TAG)
                .commit()

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

    /**산책기록을 반환합니다**/
    private fun getWalkResult(): WalkRecord {
        return WalkRecord(
                userPath.coords,
                altitudes.average(),
                speeds.average(),
                walkTime,
                stepCount,
                distance,
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
        map.uiSettings.isLocationButtonEnabled = true

        course.color = Color.parseColor("#2798E7")
        course.width = 15
        course.capType = PolylineOverlay.LineCap.Round
        course.joinType = PolylineOverlay.LineJoin.Round



        var contentPaddingBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 190f, resources.displayMetrics).toInt()
        map.setContentPadding(0,0,0,contentPaddingBottom)

        model.bottomSheetHeight.observe(viewLifecycleOwner, {v ->
            contentPaddingBottom += TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics).toInt()
            map.setContentPadding(0,0,0,contentPaddingBottom)
        })

        courseMarker.icon = MarkerIcons.BLUE

        model.coursePath.observe(viewLifecycleOwner, { v->
            cameraToCourse(v)
        })
        model.walkState.value = WALK_WAITING

        map.addOnLocationChangeListener {
            locationSource.lastLocation?.let {
                onLocationChangedListener(it)
            }
        }
    }

    private fun onLocationChangedListener (location:Location) {
        val lat = location.latitude
        val lng = location.longitude
        val point = LatLng(lat, lng)
        val speed = location.speed
        val alt = location.altitude
        if(!isLocationDataInitialized) {
            model.recordPosition(point)
            initPublicData(lat,lng)
            initialPosition = point
            isLocationDataInitialized=true
        }

        if (allowRecording) {
            if (isRestarted) {
                //초기 점이 비어있을 때
                initUserPath(point)
                isRestarted = false
            }
            if (userPath.coords.isNotEmpty() && userPath.map != null) {
                val lastPoint = userPath.coords.last()
                if (!isUserReachedToTarget(point, lastPoint)) {
                    addUserPath(point, lastPoint, userPath.coords, speed, alt.toFloat())
                }
                if (isCourseSelected) {
                    walkTag = WALK_COURSE
                    if(isCourseInitialized) {
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
                initUserPath(point)
            }
        }
    }

    /**유저 경로 초기화**/
    private fun initUserPath(currentPos: LatLng) {
        val initRoute = mutableListOf(currentPos, currentPos)
        userPath.coords = initRoute
        userPath.map = this.map
    }

    /**목표 점에 도달했는가 (근접해있는가)**/
    private fun isUserReachedToTarget(currentPos: LatLng, lastPos: LatLng): Boolean {
        return createBound(lastPos).contains(currentPos)
    }

    /**유저 경로 추가하면서 생기는 활동들 총집합**/
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
        /**마지막 점과 거리 비교해서 +- 0.00005 으로 지정된 *영역*에
        현재 점이 포함되어있다면 추가하지 않음**/
        currentPath.add(currentPos)
        speeds.add(speed)
        altitudes.add(alt)
        distance += lastPos.distanceTo(currentPos)
        model.recordDistance(distance)
        model.recordCalorie(burnedCalorie)
        userPath.coords = currentPath
    }

    /**순간 칼로리 연산**/
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

    /**산책 경로 도달 시**/
    private fun checkCoursePointArrival(
            currentPos: LatLng,
            course: MutableList<LatLng>
    ) {
        /**현재 점에서 마지막(다가오는) 경로 점의 +- 0.00005 으로 지정된 *영역*에
        현재 점이 포함되어있다면 마지막 경로 점 삭제 및 완료처리**/
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
            walkTag = WALK_COURSE_COMPLETE
        }
    }

    /**비교용 영역 만들기**/
    private fun createBound(point: LatLng): LatLngBounds {
        /** 주어진 좌표에 좌측 상단 0.00007 +
        우측 하단 0.00007 -
        두 점으로 사각형 구역을 만들어 반환**/
        val radius = 0.00007
        return LatLngBounds(
                LatLng(point.latitude - radius, point.longitude - radius),
                LatLng(point.latitude + radius, point.longitude + radius),
        )
    }

    override fun onPause() {
        super.onPause()
        if (!locationThread.isAlive) { //백그라운드 Thread 가 살아 있다면
            locationThread.start()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("RESUME", "RESUME")
        if (locationThread.isAlive) { //백그라운드 Thread 가 살아 있다면
            locationThread.interrupt()
        }
    }

    inner class BackgroundLocationThread(threadName: String , currentProvider: String) : Thread(threadName) {
        /**
         * Fused Location Provider Api 에서
         * 위치 업데이트를위한 서비스 품질등 다양한요청을
         * 설정하는데 사용하는 객체.
         */
        private lateinit var mLocationRequest: LocationRequest
        /**
         * 현재위치정보를 나타내는 객체
         */
        private lateinit var mCurrentLocation: Location

        /**
         * 현재 위치제공자(Provider)와 상호작용하는 진입점
         */
        private lateinit var mFusedLocationClient: FusedLocationProviderClient

        /**
         * 현재 단말기에 설정된 위치 Provider
         */

        override fun run() {
            super.run()

        }
    }
}
