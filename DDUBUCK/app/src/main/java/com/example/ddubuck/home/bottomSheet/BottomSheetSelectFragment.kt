package com.example.ddubuck.home.bottomSheet

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import com.example.ddubuck.databinding.BottomSheetSelectBinding
import com.example.ddubuck.home.CourseItem
import com.example.ddubuck.home.HomeActivity
import com.example.ddubuck.home.HomeRvAdapter
import com.example.ddubuck.home.WalkRecord
import com.naver.maps.map.e
import java.util.zip.Inflater

class BottomSheetSelectFragment(
        private val owner: Activity
) : Fragment() {

    lateinit var bind:BottomSheetSelectBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = BottomSheetSelectBinding.inflate(inflater, container, false)
        val sheetRecycler : RecyclerView = bind.sheetRecycler
        val mAdapter = HomeRvAdapter(owner.baseContext, fooArray){}
        //리스트가 안불러와지는 오류 해결\
        sheetRecycler.layoutManager = LinearLayoutManager(owner.baseContext, LinearLayoutManager.HORIZONTAL, false)
        sheetRecycler.adapter = mAdapter
        sheetRecycler.setOnClickListener { println("")}
        return inflater.inflate(R.layout.bottom_sheet_select,container, false)
    }


    companion object {
        private val fooArray = arrayListOf(
                CourseItem(
                        true,
                        "자유산책",
                        "자유산책입니다",
                        WalkRecord(listOf(), listOf(), listOf(), 1, 1, 1.0)),
                CourseItem(
                        false,
                        "기록",
                        "코스산책입니다",
                        WalkRecord(listOf(), listOf(), listOf(), 1, 1, 1.0)),
        )
    }
}