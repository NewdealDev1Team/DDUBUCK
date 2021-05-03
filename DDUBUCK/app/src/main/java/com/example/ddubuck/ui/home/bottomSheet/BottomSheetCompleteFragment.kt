package com.example.ddubuck.ui.home.bottomSheet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ddubuck.R
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.ui.home.HomeMapFragment
import com.example.ddubuck.ui.share.ShareActivity
import com.example.ddubuck.ui.share.canvas.CanvasActivity
import com.naver.maps.geometry.LatLng
import java.text.DecimalFormat

class BottomSheetCompleteFragment(
        private val owner: Activity,
        private val walkRecord: WalkRecord,
        private val walkType:Int) : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.bottom_sheet_complete, container, false)
        val titleTv : TextView = rootView.findViewById(R.id.sheet_complete_titleTv)
        val formatter = BottomSheetNumberFormat()
        if(walkType == HomeMapFragment.WALK_COURSE) {
            //TODO
        }
        val walkTimeTv : TextView = rootView.findViewById(R.id.sheet_complete_walTimeTv)
        walkTimeTv.text = DateUtils.formatElapsedTime(walkRecord.walkTime)
        val distanceTv : TextView = rootView.findViewById(R.id.sheet_complete_distanceTv)
        distanceTv.text = formatter.getFormattedDistance(walkRecord.distance)
        val stepTv : TextView = rootView.findViewById(R.id.sheet_complete_stepTv)
        stepTv.text = walkRecord.stepCount.toString()
        val calorieTv : TextView = rootView.findViewById(R.id.sheet_complete_calorieTv)
        calorieTv.text = formatter.getFormattedCalorie(walkRecord.getCalorie(65.0))
        val averageSpeedTv : TextView = rootView.findViewById(R.id.sheet_complete_averageSpeedTv)
        averageSpeedTv.text = formatter.getFormattedSpeed(walkRecord.speed)
        val averageAltitudeTv : TextView = rootView.findViewById(R.id.sheet_complete_averageAltitudeTv)
        averageAltitudeTv.text = formatter.getFormattedAltitude(walkRecord.altitude)
        val shareButton : Button = rootView.findViewById(R.id.sheet_complete_shareButton)
        shareButton.setOnClickListener{
            val intent = Intent(context, ShareActivity::class.java)
            intent.putExtra("walkRecord", walkRecord)
            startActivity(intent)
        }
        val addToMyPathButton : Button = rootView.findViewById(R.id.sheet_complete_addToMyPathButton)
        if(walkType == HomeMapFragment.WALK_COURSE) {
            val buttonLayout : LinearLayout = rootView.findViewById(R.id.sheet_complete_buttonLayout)
            buttonLayout.removeView(addToMyPathButton)
        } else {
            addToMyPathButton.setOnClickListener{
                Log.e("내경로추가버튼", "누르지마!")
            }
        }
        return rootView
    }
}