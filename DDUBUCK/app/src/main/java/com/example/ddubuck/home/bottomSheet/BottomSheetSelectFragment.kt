package com.example.ddubuck.home.bottomSheet


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import com.example.ddubuck.home.CourseItem
import com.example.ddubuck.home.HomeRvAdapter
import com.example.ddubuck.home.WalkRecord

class BottomSheetSelectFragment : Fragment() {
    lateinit var callback: OnCourseSelectedListener

    fun setOnCourseSelectedListener(callback: OnCourseSelectedListener) {
        this.callback = callback
    }

    interface OnCourseSelectedListener {
        fun onCourseSelected(courseItem: CourseItem)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //xml 에 인위적으로 작성된 ui 구현 코드 수정하기 (bottom_sheet_select.xml 주석 참조 할 것)
        val rootView = inflater.inflate(R.layout.bottom_sheet_select,container, false)
        val sheetRecycler : RecyclerView = rootView.findViewById(R.id.sheet_recycler)
        val mAdapter = HomeRvAdapter(fooArray, callback)
        sheetRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        sheetRecycler.adapter = mAdapter
        return rootView
    }

    companion object {
        private val fooArray = arrayListOf(
        CourseItem(
            true,
            "자유산책",
            "자유산책입니다",
            WalkRecord(listOf(), listOf(), listOf(), 1, 1, 1.0)
        ),
        CourseItem(
            false,
            "코스산책",
            "코스산책입니다",
            WalkRecord(listOf(), listOf(), listOf(), 1, 1, 1.0)),
        )
    }
}