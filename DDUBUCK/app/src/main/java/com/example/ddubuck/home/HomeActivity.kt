package com.example.ddubuck.home

import android.content.Context
import android.graphics.Color
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
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

        //임시
        val fooArray = arrayListOf<String>()
        for(i in 1..100) {
            fooArray.add(i.toString())
        }
        val sheetRecycler : RecyclerView = findViewById(R.id.sheet_recycler)

        //TODO 시트 아이템 onclick 리스너 달기

        sheetRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //sheetRecycler.adapter = HomeRvAdapter(this, fooArray)
        sheetRecycler.setOnClickListener { }

        val fm = supportFragmentManager
        val nMapFragment = fm.findFragmentById(R.id.map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.map, it).commit()
        }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        val fragmentTransaction = fm.beginTransaction()
        homeMapFragment = HomeMapFragment(locationSource, sensorManager, nMapFragment, findViewById(R.id.location))
        fragmentTransaction.add(R.id.home_map_fragment, homeMapFragment)
        fragmentTransaction.commit()

        val startButton: Button = findViewById(R.id.start_button)
        var walkRecord:WalkRecord
        startButton.setOnClickListener{
            with(homeMapFragment) {
                isRecordStarted=!isRecordStarted
                if(!isRecordStarted) {
                    stopRecording()
                    walkRecord = getWalkResult()
                    startButton.text="시작"
                    startButton.background = ResourcesCompat.getDrawable(resources, R.drawable.start_button_paused_radius, null)
                    startButton.setTextColor(Color.parseColor("#FFFFFF"))
                    showResultDialog(walkRecord)
                } else {
                    startRecording()
                    startButton.text="중지"
                    startButton.background = ResourcesCompat.getDrawable(resources, R.drawable.start_button_started_radius, null)
                    startButton.setTextColor(Color.parseColor("#000000"))
                }
            }
        }
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