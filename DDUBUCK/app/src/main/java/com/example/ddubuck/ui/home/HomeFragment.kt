package com.example.ddubuck.ui.home

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ddubuck.R
import com.example.ddubuck.ui.home.bottomSheet.BottomSheetSelectFragment
import com.example.ddubuck.weather.WeatherFragment
import com.example.ddubuck.weather.WeatherViewModel

class HomeFragment(private val owner: Activity) : Fragment() {

    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private lateinit var homeMapFragment: HomeMapFragment
    lateinit var weatherFragment: WeatherFragment

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val fm = parentFragmentManager
        homeMapFragment = HomeMapFragment(fm, owner)
        fm.beginTransaction().add(R.id.home_map_container, homeMapFragment, BOTTOM_SHEET_CONTAINER_TAG).commit()

        weatherFragment = WeatherFragment()
        fm.beginTransaction().add(R.id.home_weather_container, weatherFragment).hide(weatherFragment).commit()
        weatherViewModel.isSuccessfulResponse.observe(viewLifecycleOwner, { v ->
            if (v == true) {
                fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                    .show(weatherFragment).commit()
            }
        })


        val bottomSheetSelectFragmentFragment = BottomSheetSelectFragment()
        fm.beginTransaction().add(R.id.bottom_sheet_container, bottomSheetSelectFragmentFragment).commit()

        return root
    }

    companion object {
        const val BOTTOM_SHEET_CONTAINER_TAG = "BOTTOM_SHEET_CONTAINER"
        const val DETAIL_PAGE_FRAG = "DETAIL_PAGE_FRAG"
    }
}