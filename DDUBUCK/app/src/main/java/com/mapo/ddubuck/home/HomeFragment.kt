package com.mapo.ddubuck.home

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mapo.ddubuck.R
import com.mapo.ddubuck.home.bottomSheet.BottomSheetSelectFragment
import com.mapo.ddubuck.weather.WeatherFragment
import com.mapo.ddubuck.weather.WeatherViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HomeFragment(private val owner: Activity) : Fragment() {
    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private val mapViewModel : HomeMapViewModel by activityViewModels()
    private lateinit var homeMapFragment: HomeMapFragment
    private lateinit var weatherFragment: WeatherFragment
    private var isWeatherDataReady=false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val fm = parentFragmentManager
        homeMapFragment = HomeMapFragment(fm,owner)
        fm.beginTransaction().add(R.id.home_map_container, homeMapFragment, BOTTOM_SHEET_CONTAINER_TAG).commit()

        weatherFragment = WeatherFragment()
        fm.beginTransaction().add(R.id.home_weather_container, weatherFragment).hide(weatherFragment).commit()
        weatherViewModel.isSuccessfulResponse.observe(viewLifecycleOwner, { v ->
            if (v == true) {
                isWeatherDataReady=true
                fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                    .show(weatherFragment).commit()
            }
        })


        mapViewModel.walkState.observe(viewLifecycleOwner, {v ->
            if(isWeatherDataReady) {
                when(v) {
                    HomeMapFragment.WALK_PROGRESS -> {
                        fm.beginTransaction()
                                .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                                .hide(weatherFragment).commit()
                    }
                    else -> {
                        fm.beginTransaction()
                                .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                                .show(weatherFragment).commit()
                    }
                }
            }
        })

        val bottomSheetFrame = root.findViewById<ConstraintLayout>(R.id.bottom_sheet_frame)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetFrame)
        val bottomSheetSelectFragmentFragment = BottomSheetSelectFragment(owner)
        fm.beginTransaction().add(R.id.bottom_sheet_container, bottomSheetSelectFragmentFragment).commit()

        /*
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65f, resources.displayMetrics)
         */

        mapViewModel.bottomSheetHeight.observe(viewLifecycleOwner, {v ->
            bottomSheetFrame.layoutParams.height += TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics).toInt()
            bottomSheetBehavior.setPeekHeight(bottomSheetFrame.layoutParams.height, false)
        })

        return root
    }

    companion object {
        const val BOTTOM_SHEET_CONTAINER_TAG = "BOTTOM_SHEET_CONTAINER"
    }
}