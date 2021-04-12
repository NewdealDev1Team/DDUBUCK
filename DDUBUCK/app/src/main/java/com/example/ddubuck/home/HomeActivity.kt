package com.example.ddubuck.home

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import com.example.ddubuck.home.bottomSheet.BottomSheetSelectFragment
import com.naver.maps.map.MapFragment
import com.naver.maps.map.util.FusedLocationSource


class HomeActivity : FragmentActivity() {
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
        fm.beginTransaction().add(R.id.home_map_fragment, homeMapFragment).commit()

        val bottomSheetSelectFragmentFragment = BottomSheetSelectFragment(this@HomeActivity)
        fm.beginTransaction().replace(R.id.bottom_sheet_container, bottomSheetSelectFragmentFragment).commit()

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}