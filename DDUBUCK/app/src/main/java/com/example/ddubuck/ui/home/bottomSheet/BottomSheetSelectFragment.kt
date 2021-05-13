package com.example.ddubuck.ui.home.bottomSheet


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.ddubuck.R
import com.example.ddubuck.data.home.CourseItem
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.ui.home.HomeMapViewModel
import com.naver.maps.geometry.LatLng

class BottomSheetSelectFragment : Fragment() {
    private val homeMapViewModel: HomeMapViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.bottom_sheet_select,container, false)
        val sheetViewPager : ViewPager2 = rootView.findViewById(R.id.sheet_select_rv)
        val mAdapter = BottomSheetSelectRvAdapter(initArray, parentFragmentManager)
        sheetViewPager.adapter = mAdapter
        sheetViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(i: Int) {
                super.onPageSelected(i)
                if(initArray[i].isFreeWalk) {
                    homeMapViewModel.passPathData(listOf(LatLng(37.56362279298406, 126.90926225749905),LatLng(37.56362279298406, 126.90926225749905)))
                }else{
                    homeMapViewModel.passPathData(initArray[i].walkRecord.path)
                }
            }
        })

        homeMapViewModel.recommendPath.observe(viewLifecycleOwner, {v ->
            mAdapter.setItems(v)
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