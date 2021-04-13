package com.example.ddubuck.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.ddubuck.R
import com.example.ddubuck.home.bottomSheet.*


class HomeActivity
        : FragmentActivity(),
        BottomSheetSelectFragment.OnCourseSelectedListener,
        BottomSheetFreeDetailFragment.OnFreeStartClickedListener,
        BottomSheetCourseDetailFragment.OnCourseStartClickedListener {
    private lateinit var homeMapFragment : HomeMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fm = supportFragmentManager
        homeMapFragment = HomeMapFragment(fm, this@HomeActivity)
        fm.beginTransaction().add(R.id.home_map_container, homeMapFragment, BOTTOM_SHEET_CONTAINER_TAG).commit()

        val bottomSheetSelectFragmentFragment = BottomSheetSelectFragment()
        fm.beginTransaction().add(R.id.bottom_sheet_container, bottomSheetSelectFragmentFragment).commit()
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

    //시작버튼 선택 시 (코스산책)
    override fun onCourseStartClicked() {
        val fm = supportFragmentManager
        fm.popBackStack(DETAIL_PAGE_FRAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val frag = BottomSheetCourseProgressFragment()
        val fmTransaction = fm.beginTransaction()
        fmTransaction.replace(R.id.bottom_sheet_container, frag, BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(null).commit()
    }

    companion object {
        private const val BOTTOM_SHEET_CONTAINER_TAG = "BOTTOM_SHEET_CONTAINER"
        private const val DETAIL_PAGE_FRAG = "DETAIL_PAGE_FRAG"
    }
}