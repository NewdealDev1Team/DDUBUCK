package com.example.ddubuck.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ddubuck.R
import com.example.ddubuck.home.CourseItem
import com.example.ddubuck.home.HomeActivity
import com.example.ddubuck.home.HomeMapFragment
import com.example.ddubuck.home.bottomSheet.*

class HomeFragment(private val owner:Activity) : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeMapFragment : HomeMapFragment

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.activity_home, container, false)

        val fm = parentFragmentManager
        homeMapFragment = HomeMapFragment(fm,owner)
        fm.beginTransaction().add(R.id.home_map_container, homeMapFragment, BOTTOM_SHEET_CONTAINER_TAG).commit()

        val bottomSheetSelectFragmentFragment = BottomSheetSelectFragment()
        fm.beginTransaction().add(R.id.bottom_sheet_container, bottomSheetSelectFragmentFragment).commit()

        return root
    }

    companion object {
        private const val BOTTOM_SHEET_CONTAINER_TAG = "BOTTOM_SHEET_CONTAINER"
        private const val DETAIL_PAGE_FRAG = "DETAIL_PAGE_FRAG"
    }
}