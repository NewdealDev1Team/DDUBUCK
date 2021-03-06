package com.mapo.ddubuck.home.bottomSheet

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.home.WalkRecord
import com.mapo.ddubuck.home.CourseAddDialog
import com.mapo.ddubuck.home.HomeMapFragment
import com.mapo.ddubuck.home.HomeMapViewModel
import com.mapo.ddubuck.mypage.MypageViewModel
import com.mapo.ddubuck.share.ShareActivity
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.naver.maps.geometry.LatLng


class BottomSheetCompleteFragment(
    private val owner: Activity,
    private val walkRecord: WalkRecord,
    private val userKey : String,
    private val walkType: Int,
) : Fragment() {

    private val homeMapViewModel : HomeMapViewModel by activityViewModels()
    private val mypageViewModel : MypageViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeMapViewModel.bottomSheetHeight.value = 100
        val rootView = inflater.inflate(R.layout.bottom_sheet_complete, container, false)
        val titleTv : TextView = rootView.findViewById(R.id.sheet_complete_titleTv)
        val formatter = BottomSheetNumberFormat()
        if(walkType == HomeMapFragment.WALK_FREE) {
            titleTv.text = "자유 산책"
        } else {
            titleTv.text = "코스 산책"
        }
        val walkTimeTv : TextView = rootView.findViewById(R.id.sheet_complete_walTimeTv)
        walkTimeTv.text = DateUtils.formatElapsedTime(walkRecord.walkTime)
        val distanceTv : TextView = rootView.findViewById(R.id.sheet_complete_distanceTv)
        distanceTv.text = formatter.getFormattedDistance(walkRecord.distance)
        val stepTv : TextView = rootView.findViewById(R.id.sheet_complete_stepTv)
        stepTv.text = walkRecord.stepCount.toString()
        val calorieTv : TextView = rootView.findViewById(R.id.sheet_complete_calorieTv)
        calorieTv.text = formatter.getFormattedCalorie(walkRecord.getCalorie(UserSharedPreferences.getUserWeight(owner).toDouble()))
        val averageSpeedTv : TextView = rootView.findViewById(R.id.sheet_complete_averageSpeedTv)
        averageSpeedTv.text = formatter.getFormattedSpeed(walkRecord.speed)
        val averageAltitudeTv : TextView = rootView.findViewById(R.id.sheet_complete_averageAltitudeTv)
        averageAltitudeTv.text = formatter.getFormattedAltitude(walkRecord.altitude)
        val shareButton : Button = rootView.findViewById(R.id.sheet_complete_shareButton)
        //a
        if(walkRecord.path.size > 2) {
            shareButton.setOnClickListener{
                val intent = Intent(context, ShareActivity::class.java)
                intent.putExtra("walkRecord", walkRecord)
                startActivityForResult(intent, 100)
            }
        } else {
            shareButton.background = ResourcesCompat.getDrawable(resources,
                R.drawable.sheet_button_deactivated,
                null)
            shareButton.setTextColor(Color.GRAY)
        }
        val addToMyPathButton : Button = rootView.findViewById(R.id.sheet_complete_addToMyPathButton)
        if(walkType == HomeMapFragment.WALK_FREE) {
            if(walkRecord.path.size > 2) {
                addToMyPathButton.setOnClickListener{
                    val dialog = CourseAddDialog(walkRecord,userKey,owner)
                    dialog.show(parentFragmentManager, MainActivity.HOME_TAG)
                }
            } else {
                addToMyPathButton.background =  ResourcesCompat.getDrawable(resources,
                    R.drawable.sheet_button_deactivated,
                    null)
                addToMyPathButton.setTextColor(Color.GRAY)
            }
        } else {
            val buttonLayout : LinearLayout = rootView.findViewById(R.id.sheet_complete_buttonLayout)
            buttonLayout.removeView(addToMyPathButton)
        }
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100) {
            mypageViewModel.isImageUpdate.value = true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        homeMapViewModel.bottomSheetHeight.value = -100
    }
}