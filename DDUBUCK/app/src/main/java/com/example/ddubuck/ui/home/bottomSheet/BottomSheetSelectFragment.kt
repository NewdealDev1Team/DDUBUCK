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
import java.util.*

class BottomSheetSelectFragment : Fragment() {
    private val homeMapViewModel: HomeMapViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.bottom_sheet_select,container, false)
        val sheetViewPager : ViewPager2 = rootView.findViewById(R.id.sheet_select_rv)
        val mAdapter = BottomSheetSelectRvAdapter(fooArray, parentFragmentManager)
        sheetViewPager.adapter = mAdapter
        sheetViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(i: Int) {
                super.onPageSelected(i)
                if(fooArray[i].isFreeWalk) {
                    homeMapViewModel.passPathData(listOf(LatLng(37.56362279298406, 126.90926225749905),LatLng(37.56362279298406, 126.90926225749905)))
                }else{
                    homeMapViewModel.passPathData(fooArray[i].walkRecord.path)
                }
            }
        })
        return rootView
    }

    companion object {
        private val fooRoute = listOf( //9개
                LatLng(37.56362279298406, 126.90926225749905),
                LatLng(37.56345663522066, 126.9091328029345),
                LatLng(37.56314632623486, 126.90784351195998),
                LatLng(37.56396493508562, 126.90736905196479),
                LatLng(37.56417998056722, 126.90825278385154),
                LatLng(37.56375202367158, 126.90831947940694),
                LatLng(37.56332059071951, 126.90851459284085),
                LatLng(37.56346358071265, 126.909140550899),
                LatLng(37.5637076839577, 126.9092733697774),
        )
        private val fooAltitude = listOf(1.0F,3.0F,2.0F,5.0F,7.0F,1.0F,2.0F,4.0F,2.0F)
        private val fooSpeed = listOf(0.1F,0.2F,0.2F,0.3F,0.6F,3F,5F,3F,5F)
        private val fooArray = arrayListOf(
        CourseItem(
            true,
            "자유산책",
            "자유산책입니다",
            WalkRecord(listOf(), listOf(), listOf(), 1, 1, 1.0, Date())
        ),
        CourseItem(
            false,
            "코스산책",
            "코스산책입니다",
            WalkRecord(fooRoute, fooAltitude, fooSpeed, 325, 5683, 900.0, Date())),
        )

    }
}