package com.example.ddubuck

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.widget.Button
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

class MapFragmentActivity : FragmentActivity(), OnMapReadyCallback  {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var map: NaverMap
    private var isRecordStarted=false
    private var path : PathOverlay = PathOverlay()
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

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync (this)

        val startButton : Button = findViewById(R.id.start_button)
        startButton.setOnClickListener{
            isRecordStarted=!isRecordStarted
            if(!isRecordStarted) {
                startButton.text="시작"
                startButton.background = ResourcesCompat.getDrawable(resources, R.drawable.start_button_paused_radius, null)
                startButton.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                startButton.text="중지"
                startButton.background = ResourcesCompat.getDrawable(resources, R.drawable.start_button_started_radius, null)
                startButton.setTextColor(Color.parseColor("#000000"))
            }
        }

        //코스 프리셋 1번을 불러옵니다
        val resetCourseButton : Button = findViewById(R.id.test_button_1)
        resetCourseButton.setOnClickListener{
            customPath.map = this.map
            //앱 제작 시 아래 firstRoute를
            //유저가 지정한 루트로 변경
            customPath.coords = firstRoute
        }

        //현재 지정된(기록된) 코스를 저장합니다
        val createCourseButton : Button = findViewById(R.id.test_button_2)
        createCourseButton.setOnClickListener{
            val userPath = path.coords
            //앱 제작 시 저장대상을 서버로 변경
            customPath.coords = userPath
            //TODO 유저 경로 삭제 코드 강화할 것
            //path = PathOverlay()
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
        this.map = naverMap

        map.locationSource = locationSource
        map.locationTrackingMode = LocationTrackingMode.Face
        map.uiSettings.isLocationButtonEnabled = true

        val customCourse = firstRoute
        customPath.coords = customCourse
        customPath.map = naverMap
        customPath.color = Color.CYAN

        map.addOnLocationChangeListener {
            if(isRecordStarted) {
                val lat = locationSource.lastLocation?.latitude
                val lng = locationSource.lastLocation?.longitude
                if(lat!=null&&lng!=null) {
                    val point = LatLng(lat, lng)
                    if(path.coords.isNotEmpty()) {
                        val points = path.coords
                        if(points.isNotEmpty()) {
                            //안 비어있을때
                            //마지막 점과 거리 비교해서 +- 0.00005 으로 지정된 *영역*에
                                //영역 관련 회의 필요.
                            //현재 점이 포함되어있다면 추가하지 않음
                            val lastPoint = path.coords.last()
                            if(!createBound(lastPoint).contains(point)) {
                                points.add(point)
                                if(customCourse.isNotEmpty()) {
                                    if(createBound(customCourse.first()).contains(point)) {
                                        customCourse.removeAt(0)
                                        customPath.coords = customCourse
                                    }
                                }

                            }
                        } else {
                            points.add(point)
                        }
                        //이렇게 합쳐주지 않으면 지도에 반영 안됩니다 ㅠ
                        path.coords = points
                    } else {
                        //초기 점이 비어있을 때
                        val initRoute = mutableListOf(
                            LatLng(lat,lng),
                            LatLng(lat,lng)
                        )
                        path.coords = initRoute
                        path.map = naverMap
                    }
                }
            }
        }

        map.setOnMapClickListener{p: PointF, l: LatLng ->
            println(l)
        }
    }

    private fun createBound(point:LatLng):LatLngBounds {
        // 주어진 좌표에 좌측 상단의 경우 0.000050 +
        // 우측 하단의 경우 0.000050 -
        val radius = 0.00005
        return LatLngBounds(
            LatLng(point.latitude-radius, point.longitude-radius),
            LatLng(point.latitude+radius, point.longitude+radius),
        )
    }



    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}