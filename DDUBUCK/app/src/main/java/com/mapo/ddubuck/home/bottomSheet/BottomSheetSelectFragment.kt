package com.mapo.ddubuck.home.bottomSheet


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.home.CourseItem
import com.mapo.ddubuck.data.home.WalkRecord
import com.mapo.ddubuck.home.HomeMapViewModel
import com.mapo.ddubuck.sharedpref.UserSharedPreferences

class BottomSheetSelectFragment(private val owner:Activity) : Fragment() {
    private val homeMapViewModel: HomeMapViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.bottom_sheet_select,container, false)
        val sheetViewPager : ViewPager2 = rootView.findViewById(R.id.sheet_select_rv)
        val mAdapter = BottomSheetSelectRvAdapter(owner, initArray, parentFragmentManager)
        sheetViewPager.adapter = mAdapter
        sheetViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(i: Int) {
                super.onPageSelected(i)
                if(!initArray[i].isFreeWalk) {
                    homeMapViewModel.passPathData(initArray[i].walkRecord.path)
                }
            }
        })

        homeMapViewModel.recommendPath.observe(viewLifecycleOwner, {v ->
            mAdapter.setItems(v)
        })

        homeMapViewModel.bookmarkChanged.observe(viewLifecycleOwner, {
            mAdapter.setBookmarks(UserSharedPreferences.getBookmarkedCourse(owner))
        })

        mAdapter.isBookmarkChanged.observe(viewLifecycleOwner, {
            homeMapViewModel.bookmarkChanged.value = true
        })

        return rootView
    }

    companion object {
        private val initArray = arrayListOf(
            CourseItem(
                true,
                null,
                "",
                "",
                WalkRecord(listOf(), 0.0, 0.0, 1, 1, 1.0)
            ),
        )
    }
}