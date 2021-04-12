package com.example.ddubuck.home.bottomSheet

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

class BottomSheetSelectFragment : Fragment() {
    lateinit var bind:BottomSheetSelectBinding

    lateinit var callback: OnCourseSelectedListener

    fun setOnCourseSelectedListener(callback: OnCourseSelectedListener) {
        this.callback = callback
    }

    interface OnCourseSelectedListener {
        fun onCourseSelected(courseItem: CourseItem)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = BottomSheetSelectBinding.inflate(inflater, container, true )
        return inflater.inflate(R.layout.bottom_sheet_select,container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sheetRecycler : RecyclerView = view.findViewById(R.id.sheet_recycler)
        val mAdapter = HomeRvAdapter(fooArray, callback)
        sheetRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        sheetRecycler.adapter = mAdapter
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