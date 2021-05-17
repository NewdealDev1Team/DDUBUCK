package com.mapo.ddubuck.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mapo.ddubuck.MainActivityViewModel
import com.mapo.ddubuck.R
import com.mapo.ddubuck.databinding.FragmentHomeDrawerBinding
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import kotlinx.android.synthetic.main.fragment_home_drawer.view.*

class FilterDrawer(private val owner:Activity) :Fragment() {
    private val mainViewModel : MainActivityViewModel by activityViewModels()
    private val homeMapViewModel : HomeMapViewModel by activityViewModels()
    private val sharedPref = UserSharedPreferences

    companion object {
        const val CAFE = "CAFE"
        const val CAR_FREE_ROAD = "CAR_FREE_ROAD"
        const val PET_CAFE = "PET_CAFE"
        const val PET_RESTAURANT = "PET_RESTAURANT"
        const val PUBLIC_REST_AREA = "PUBLIC_REST_AREA"
        const val PUBLIC_TOILET = "PUBLIC_TOILET"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home_drawer, container, false)


        root.findViewById<CheckBox>(R.id.home_drawer_cafe).let { v ->
            v.isChecked = sharedPref.getFilterVisible(owner, CAFE)
            v.setOnClickListener {
                homeMapViewModel.showCafe.value = v.isChecked
                sharedPref.setFilterVisible(owner, CAFE, v.isChecked)
            }
        }

        root.findViewById<CheckBox>(R.id.home_drawer_carFreeRoad).let { v ->
            v.isChecked = sharedPref.getFilterVisible(owner, CAR_FREE_ROAD)
            v.setOnClickListener {
                homeMapViewModel.showCarFreeRoad.value = v.isChecked
                sharedPref.setFilterVisible(owner, CAR_FREE_ROAD, v.isChecked)
            }
        }

        root.findViewById<CheckBox>(R.id.home_drawer_petCafe).let { v ->
            v.isChecked = sharedPref.getFilterVisible(owner, PET_CAFE)
            v.setOnClickListener {
                homeMapViewModel.showPetCafe.value = v.isChecked
                sharedPref.setFilterVisible(owner, PET_CAFE, v.isChecked)
            }
        }

        root.findViewById<CheckBox>(R.id.home_drawer_petRestaurant).let { v ->
            v.isChecked = sharedPref.getFilterVisible(owner, PET_RESTAURANT)
            v.setOnClickListener {
                homeMapViewModel.showPetRestaurant.value = v.isChecked
                sharedPref.setFilterVisible(owner, PET_RESTAURANT, v.isChecked)
            }
        }

        root.findViewById<CheckBox>(R.id.home_drawer_publicResetArea).let { v ->
            v.isChecked = sharedPref.getFilterVisible(owner, PUBLIC_REST_AREA)
            v.setOnClickListener {
                homeMapViewModel.showPublicRestArea.value = v.isChecked
                sharedPref.setFilterVisible(owner, PUBLIC_REST_AREA, v.isChecked)
            }
        }

        root.findViewById<CheckBox>(R.id.home_drawer_publicToilet).let { v ->
            v.isChecked = sharedPref.getFilterVisible(owner, PUBLIC_TOILET)
            v.setOnClickListener {
                homeMapViewModel.showPublicToilet.value = v.isChecked
                sharedPref.setFilterVisible(owner, PUBLIC_TOILET, v.isChecked)
            }
        }

        root.findViewById<Button>(R.id.home_drawer_confirmButton).setOnClickListener {
            mainViewModel.showDrawer.value = false
        }
        return root
    }
}

