package com.example.ddubuck.home

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.ddubuck.R
import com.example.ddubuck.home.bottomSheet.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.util.FusedLocationSource


class HomeActivity
        : FragmentActivity(),
        BottomSheetSelectFragment.OnCourseSelectedListener,
        BottomSheetFreeDetailFragment.OnFreeStartClickedListener,
        BottomSheetCourseDetailFragment.OnCourseStartClickedListener {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var homeMapFragment : HomeMapFragment
    private val sensorManager by lazy { // 지연된 초기화는 딱 한 번 실행됨
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fm = supportFragmentManager
        val nMapFragment = fm.findFragmentById(R.id.map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.map, it).commit()
                }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        homeMapFragment = HomeMapFragment(locationSource, sensorManager, nMapFragment, findViewById(R.id.location))
        fm.beginTransaction().add(R.id.home_map_fragment, homeMapFragment, BOTTOM_SHEET_CONTAINER_TAG).commit()

        val bottomSheetSelectFragmentFragment = BottomSheetSelectFragment()
        fm.beginTransaction().add(R.id.bottom_sheet_container, bottomSheetSelectFragmentFragment).commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        when(fragment) {
            is BottomSheetSelectFragment -> fragment.setOnCourseSelectedListener(this)
            is BottomSheetFreeDetailFragment -> fragment.setOnFreeStartClickedListener(this)
            is BottomSheetCourseDetailFragment -> fragment.setOnCourseStartClickedListener(this)
        }


    }

    //코스 선택 시 (자유산책/코스산책)
    override fun onCourseSelected(courseItem: CourseItem) {
        val fm = supportFragmentManager
        if(courseItem.isFreeWalk) {
            val frag = BottomSheetFreeDetailFragment()
            val fmTransaction = fm.beginTransaction()
            fmTransaction.replace(R.id.bottom_sheet_container,frag, BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(DETAIL_PAGE_FRAG).commit()
        } else {
            val frag = BottomSheetCourseDetailFragment(courseItem)
            val fmTransaction = fm.beginTransaction()
            fmTransaction.replace(R.id.bottom_sheet_container,frag, BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(DETAIL_PAGE_FRAG).commit()
        }
    }

    //시작버튼 선택 시 (자유산책)
    override fun onFreeStartClicked() {
        val fm = supportFragmentManager
        fm.popBackStack(DETAIL_PAGE_FRAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val frag = BottomSheetFreeProgressFragment()
        val fmTransaction = fm.beginTransaction()
        fmTransaction.replace(R.id.bottom_sheet_container, frag, BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(null).commit()
    }

    override fun onCourseStartClicked() {
        val fm = supportFragmentManager
        fm.popBackStack(DETAIL_PAGE_FRAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val frag = BottomSheetCourseProgressFragment()
        val fmTransaction = fm.beginTransaction()
        fmTransaction.replace(R.id.bottom_sheet_container, frag, BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(null).commit()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val BOTTOM_SHEET_CONTAINER_TAG = "BOTTOM_SHEET_CONTAINER"
        private const val DETAIL_PAGE_FRAG = "DETAIL_PAGE_FRAG"
    }


}