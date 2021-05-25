package com.mapo.ddubuck.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.*
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.RetrofitClient
import com.mapo.ddubuck.data.RetrofitService
import com.mapo.ddubuck.data.home.CourseItem
import com.mapo.ddubuck.data.home.WalkRecord
import com.mapo.ddubuck.data.publicdata.HiddenChallenge
import com.mapo.ddubuck.data.publicdata.PublicData
import com.mapo.ddubuck.data.publicdata.RecommendPathDTO
import com.mapo.ddubuck.home.bottomSheet.BottomSheetCompleteFragment
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.mapo.ddubuck.ui.CommonDialog
import com.mapo.ddubuck.weather.WeatherViewModel
import com.naver.maps.geometry.LatLng
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
        /**목표 지점 체크 허용거리
         * ex : 5미터 이내일 시 경로 지점 판정 **/
        const val courseCompareDistance = 15.0
        const val hiddenChallengeCompareDistance = 15.0
    }

    //뷰모델
    private val mapModel: HomeMapViewModel by activityViewModels()
    private val weatherModel : WeatherViewModel by activityViewModels()

    //환경설정 변수
    private val userKey : String = UserSharedPreferences.getUserId(owner)
    private lateinit var map: NaverMap
    private lateinit var timer: Timer
    private lateinit var locationSource: FusedLocationSource
    private val sensorManager by lazy {
        owner.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private var markers:HashMap<String, MutableList<Marker>> = HashMap()
    private val sharedPref = UserSharedPreferences
    private var isLocationDataInitialized = false
    private var initialPosition : LatLng = LatLng(0.0,0.0)
    private val hiddenPlaces = mutableListOf<HiddenChallenge>()
    private val completedHiddenPlaces = mutableListOf<HiddenChallenge>()

    //측정 관련 변수
    private var userPath = PathOverlay()
    private var altitudes: MutableList<Float> = mutableListOf()
    private var speeds: MutableList<Float> = mutableListOf()
    private var walkTime: Long = 0
    private var timePoint: Long = 0
    private var stepCount: Int = 0
    private var distance: Double = 0.0
    private var burnedCalorie: Double = 0.0
    private lateinit var hiddenChallengeUserPos : LatLng

    //코스
    private lateinit var courseData : CourseItem
    private var course = PolylineOverlay()
    private var courseMarker = Marker()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mapModel.walkState.value = WALK_START
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        val nMapFragment = fm.findFragmentById(R.id.map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.map, it).commit()
                }
        nMapFragment.getMapAsync(this)

        mapModel.isRecordStarted.observe(viewLifecycleOwner, { v ->
            if (v) {
                //start
                startRecording()
                allowRecording = true
                isRestarted = true
                mapModel.walkState.value = WALK_PROGRESS
            } else {
                //stop
                stopRecording()
                allowRecording = false
                isRestarted = true
                isCourseSelected = false
                isCourseInitialized = false
                mapModel.walkState.value = WALK_WAITING
            }
        })

        mapModel.isRecordPaused.observe(viewLifecycleOwner, { v ->
            allowRecording = if (v) {
                //start
                pauseRecording()
                mapModel.walkState.value = WALK_PAUSE
                false
            } else {
                //stop
                resumeRecording()
                mapModel.walkState.value = WALK_PROGRESS
                true
            }
        })

        mapModel.isCourseWalk.observe(viewLifecycleOwner, { v -> isCourseSelected = v})
        mapModel.courseProgressPath.observe(viewLifecycleOwner, { v -> course.coords = v.toMutableList() })
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Intent(owner, HomeMapService::class.java).also {
            it.action = "ACTION_STOP_SERVICE"
            owner.startService(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**알림 만들기 (HomeMapService 에서 작동)**/
    private fun createNotification(title : String, content : String) {
        /** 서비스 켜졌을 때만 작동함 **/
        if(allowRecording) {
            if(UserSharedPreferences.getPushAlarm(owner)) {
                val builder = NotificationCompat.Builder(owner, HomeMapService.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                HomeMapService.notification.value = builder.build()
                mapModel.vibrate(true)
            }

        } else {
            Log.e("HomeMap", "HomMapService를 활성화해주세요")
        }

    }

    private fun initPublicData(x:Double, y:Double, userKey:String) {

        markers[FilterDrawer.PUBLIC_TOILET] = mutableListOf()
        markers[FilterDrawer.PUBLIC_REST_AREA] = mutableListOf()
        markers[FilterDrawer.PET_RESTAURANT] = mutableListOf()
        markers[FilterDrawer.PET_CAFE] = mutableListOf()
        markers[FilterDrawer.CAR_FREE_ROAD]= mutableListOf()
        markers[FilterDrawer.CAFE]= mutableListOf()

        RetrofitClient.publicDataInstance.getResult(x,y,userKey)
            .enqueue(object : Callback<PublicData> {
                override fun onResponse(call: Call<PublicData>, response: Response<PublicData>) {
                    if(response.body() != null) {
                        val publicData = response.body()!!
                        val markerHeightSize = 80
                        val markerWidthSize = 60
                        markers[FilterDrawer.PET_CAFE]!!.clear()
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
                            markers[FilterDrawer.CAR_FREE_ROAD]!!.add(marker)
                        }
                        markers[FilterDrawer.PET_CAFE]!!.clear()
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
                        markers[FilterDrawer.CAFE]!!.clear()
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
                        markers[FilterDrawer.PET_RESTAURANT]!!.clear()
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
                        markers[FilterDrawer.PUBLIC_REST_AREA]!!.clear()
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
                        markers[FilterDrawer.PUBLIC_TOILET]!!.clear()
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
                        hiddenPlaces.clear()
                        for(i in publicData.hiddenChallenge) {
                            hiddenPlaces.add(i)
                        }
                        val recommendPath = mutableListOf<RecommendPathDTO>()
                        recommendPath.addAll(publicData.recommendPathMaster)
                        recommendPath.addAll(publicData.recommendPathUser)
                        if(recommendPath.isNotEmpty()) {
                            val courseData = mutableListOf<CourseItem>()
                            for (i in recommendPath) {
                                courseData.add(i.toCourseItem())
                                mapModel.recommendPath.value = courseData
                            }
                            //home에다가 보내기
                        }
                    }
                }

                override fun onFailure(call: Call<PublicData>, t: Throwable) {
                    Log.e("publicDataFetch", "FAILED!")
                }

            })

        mapModel.showCafe.observe(viewLifecycleOwner, { v->
            markers[FilterDrawer.CAFE]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })

        mapModel.showCarFreeRoad.observe(viewLifecycleOwner, { v->
            markers[FilterDrawer.CAR_FREE_ROAD]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })

        mapModel.showPetCafe.observe(viewLifecycleOwner, { v->
            markers[FilterDrawer.PET_CAFE]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })

        mapModel.showPetRestaurant.observe(viewLifecycleOwner, { v->
            markers[FilterDrawer.PET_RESTAURANT]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })

        mapModel.showPublicRestArea.observe(viewLifecycleOwner, { v->
            markers[FilterDrawer.PUBLIC_REST_AREA]!!.forEach { m ->
                if(v) {
                    m.map = map
                } else {
                    m.map = null
                }
            }
        })

        mapModel.showPublicToilet.observe(viewLifecycleOwner, { v->
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
        Intent(owner, HomeMapService::class.java).also {
            it.action = "ACTION_START_OR_RESUME_SERVICE"
            owner.startService(it)
        }
        timer = timer(period = 1000) {
            mapModel.recordTime(walkTime)
            walkTime++
        }
    }

    /**산책을 일시정지 합니다**/
    private fun pauseRecording() {
        timer.cancel()
    }

    private fun resumeRecording() {
        timer = timer(period = 1000) {
            mapModel.recordTime(walkTime)
            walkTime++
        }
    }

    /**산책을 종료하고 기록을 반환합니다**/
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun stopRecording() {
        Intent(owner, HomeMapService::class.java).also {
            it.action = "ACTION_STOP_SERVICE"
            owner.startService(it)
        }
        userPath.map = null
        timer.cancel()
        course.map = null
        courseMarker.map = null
        //기록 및 반환 코드

        if(completedHiddenPlaces.size != 0) {
            CommonDialog("히든챌린지 달성!", "멋져요! 오늘 산책에서 히든 챌린지를 달성하셨어요!", owner).let {
                it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.show()
            }
        }


        val walkRecord = getWalkResult()
        val courseTitle = if(isCourseSelected || walkTag == WALK_COURSE_COMPLETE) {
            courseData.title
        } else {
            null
        }

        RetrofitService().createRecord(
            userKey,
            courseTitle,
            UserSharedPreferences.getPet(owner),
            walkRecord,
            weatherModel.weatherKeyword.value,
            burnedCalorie,
            walkTag,
            completedHiddenPlaces
        ) {
            mapModel.recordMywalk.value = true
        }

        completedHiddenPlaces.clear()
        parentFragmentManager.beginTransaction()
                .replace(R.id.bottom_sheet_container, BottomSheetCompleteFragment(owner,walkRecord,userKey, walkTag),
                        HomeFragment.BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(MainActivity.HOME_RESULT_TAG)
                .commit()

        mapModel.walkTime.value = 0
        mapModel.walkCalorie.value = 0.0
        mapModel.walkDistance.value = 0.0

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

        mapModel.bottomSheetHeight.observe(viewLifecycleOwner, { v ->
            contentPaddingBottom += TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics).toInt()
            map.setContentPadding(0,0,0,contentPaddingBottom)
        })

        courseMarker.icon = MarkerIcons.BLUE

        mapModel.courseData.observe(viewLifecycleOwner, { v->
            courseData = v
            cameraToCourse(v.walkRecord.path)
        })
        mapModel.walkState.value = WALK_WAITING

        map.addOnLocationChangeListener { location->
            val lat = location.latitude
            val lng = location.longitude
            val point = LatLng(lat, lng)
            if(!isLocationDataInitialized) {
                mapModel.recordPosition(point)
                initPublicData(lat,lng, userKey)
                initialPosition = point
                isLocationDataInitialized=true
            }
            if(initialPosition.distanceTo(point)==3000.0) {
                isLocationDataInitialized = false
            }
        }
        HomeMapService.currentLocation.observe(viewLifecycleOwner, {v->
            onLocationChangedListener(v, userKey)
        })
    }

    /**사용자 위치 바뀌었을때 할 행동(리스너)**/
    private fun onLocationChangedListener (location:Location, userKey: String) {
        val lat = location.latitude
        val lng = location.longitude
        val point = LatLng(lat, lng)
        val speed = location.speed
        val alt = location.altitude
        if(!isLocationDataInitialized) {
            mapModel.recordPosition(point)
            initPublicData(lat,lng, userKey)
            initialPosition = point
            isLocationDataInitialized=true
        }

        /**산책을 시작했을 때**/
        if (allowRecording) {
            if (isRestarted) {
                //초기 점이 비어있을 때
                initUserPath(point)
                isRestarted = false
            }
            /**사용자 경로가 초기화 되어있는지 확인함**/
            if (userPath.coords.isNotEmpty() && userPath.map != null) {
                val lastPoint = userPath.coords.last()
                /**사용자가 마지막으로 등록한 point와 충분히 멀어졌는지 검사한다**/
                /**현재 점과 마지막 점의 거리차이가 지정된 거리 이하로 나면 점을 추가하지 않음**/
                if (!isUserReachedToTarget(courseCompareDistance,point, lastPoint)) {
                    addUserPath(point, lastPoint, userPath.coords, speed, alt.toFloat())
                }
                /**코스산책일 경우**/
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
                } else {
                    /**히든 챌린지는 자유산책에서만 이용 가능**/
                    /**히든 플레이스 정렬(500미터 주기)**/
                    if(hiddenPlaces.size != 0) {
                        if(!::hiddenChallengeUserPos.isInitialized || point.distanceTo(hiddenChallengeUserPos) >= 500) {
                            val sorted = getSortedHiddenPlaceList(point, hiddenPlaces)
                            hiddenPlaces.clear()
                            hiddenPlaces.addAll(sorted)
                            hiddenChallengeUserPos = point
                        }
                        checkHiddenPlaceArrival(point,hiddenPlaces.first())
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
    private fun isUserReachedToTarget(distance:Double,currentPos: LatLng, targetPos: LatLng): Boolean {
        return currentPos.distanceTo(targetPos) <= distance
    }

    /**유저 경로 추가하면서 생기는 활동들 총집합**/
    private fun addUserPath(
        currentPos: LatLng,
        lastPos: LatLng,
        currentPath: MutableList<LatLng>,
        speed: Float,
        alt: Float,
    ) {
        if (timePoint != 0.toLong()) {
            burnedCalorie += calculateMomentCalorie(speed, walkTime - timePoint)
            timePoint = walkTime
        } else {
            burnedCalorie += calculateMomentCalorie(speed, walkTime)
            timePoint = walkTime
        }
        currentPath.add(currentPos)
        speeds.add(speed)
        altitudes.add(alt)
        distance += lastPos.distanceTo(currentPos)
        mapModel.recordDistance(distance)
        mapModel.recordCalorie(burnedCalorie)
        userPath.coords = currentPath
    }

    /**순간 칼로리 연산**/
    private fun calculateMomentCalorie(speed: Float, passedTime: Long): Double {
        var weight = UserSharedPreferences.getUserWeight(owner).toDouble()
        if(weight==0.0) {
            weight=65.0
        }
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
        course: MutableList<LatLng>,
    ) {
        /**현재 점에서 목표 경로 점과의 거리가 지정된 거리보다 적다면 목표 경로 점 삭제 및 완료처리**/
        if (course.size > 2) {
            if (isUserReachedToTarget(courseCompareDistance,currentPos, course.first())) {
                course.removeAt(0)
                //진동
                mapModel.vibrate(true)
                courseMarker.position = course.first()
                mapModel.passProgressData(course)
                this.course.coords = course
            }
        } else {
            //코스완료
            this.course.map = null
            this.courseMarker.map = null
            walkTag = WALK_COURSE_COMPLETE
        }
    }

    /**가까운 순으로 히든플레이스를 정렬한다**/
    private fun getSortedHiddenPlaceList(
        currentPos: LatLng,
        list : MutableList<HiddenChallenge>
    ) : List<HiddenChallenge> {
        return list.sortedBy { it.selector(currentPos) }
    }

    /**히든 플레이스 도달 시**/
    private fun checkHiddenPlaceArrival(
        currentPos: LatLng,
        hiddenPlace:HiddenChallenge,
    ) {
        if(isUserReachedToTarget(hiddenChallengeCompareDistance,currentPos, hiddenPlace.pos())) {
            Toast.makeText(owner, "히든 챌린지 달성! : ${hiddenPlace.title}", Toast.LENGTH_LONG).show()
            createNotification("히든 챌린지 달성!", "히든 챌린지 달성! : ${hiddenPlace.title}")
            completedHiddenPlaces.add(hiddenPlace)
            hiddenPlaces.removeAt(0)
        } else {
            hintHiddenPlace(currentPos, hiddenPlace)
        }
    }

    /**300미터~100미터 내에 히든플레이스가 있으면 힌트를 준다**/
    private fun hintHiddenPlace(
        currentPos:LatLng,
        hiddenPlace: HiddenChallenge
    ) {
        currentPos.distanceTo(hiddenPlace.pos()).let { dis ->
            when (dis) {
                in 200.1..300.0 -> {
                    if(!hiddenPlace.firstHintShown) {
                        Toast.makeText(owner, "히든 챌린지가 반경 300미터 내에 있습니다!", Toast.LENGTH_LONG).show()
                        createNotification("히든 챌린지 힌트!", "히든 챌린지가 반경 300미터 내에 있습니다!")
                        hiddenPlaces[hiddenPlaces.indexOf(hiddenPlace)].firstHintShown = true
                    }
                }
                in 100.1..200.0 -> {
                    if(!hiddenPlace.secondHintShown) {
                        Toast.makeText(owner, "히든 챌린지가 반경 200미터 내에 있습니다!", Toast.LENGTH_LONG).show()
                        createNotification("히든 챌린지 힌트!", "히든 챌린지가 반경 200미터 내에 있습니다!")
                        hiddenPlaces[hiddenPlaces.indexOf(hiddenPlace)].secondHintShown = true
                    }
                }
                in 16.0..100.0 -> {
                    if(!hiddenPlace.thirdHintShown) {
                        Toast.makeText(owner, "히든 챌린지가 반경 100미터 내에 있습니다!", Toast.LENGTH_LONG).show()
                        createNotification("히든 챌린지 힌트!", "히든 챌린지가 반경 100미터 내에 있습니다!")
                        hiddenPlaces[hiddenPlaces.indexOf(hiddenPlace)].thirdHintShown = true
                    }
                }
                else -> return
            }
        }
    }

}
