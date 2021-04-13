package com.example.ddubuck.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import com.example.ddubuck.home.bottomSheet.BottomSheetSelectFragment
import com.naver.maps.map.e
import kotlin.math.log

class HomeRvAdapter(private val itemList: ArrayList<CourseItem>,
                    private val callback: BottomSheetSelectFragment.OnCourseSelectedListener,):
    RecyclerView.Adapter<Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_select_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(itemList[position], callback)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}

class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)  {
    private val title = itemView?.findViewById<TextView>(R.id.sheet_item_title)
    private val body = itemView?.findViewById<TextView>(R.id.sheet_item_body)
    private val picture = itemView?.findViewById<ImageView>(R.id.sheet_item_picture)

    fun bind(i: CourseItem,callback : BottomSheetSelectFragment.OnCourseSelectedListener) {
        if(i.isFreeWalk) {
            itemView.setOnClickListener{callback.onCourseSelected(CourseItem(
                true,
                "자유산책",
                "자유산책입니다",
                WalkRecord(listOf(), listOf(), listOf(), 1, 1, 1.0)),)}
            title?.text = "자유산책"
            body?.text = "나만의 자유로운 산책,\n즐길 준비 되었나요?"
            picture?.setImageResource(R.mipmap.ic_launcher)
        } else {
            itemView.setOnClickListener{callback.onCourseSelected(i)}
            title?.text = i.title
            body?.text = "걸음수 : ${i.walkRecord.stepCount}\n거리 : ${i.walkRecord.distance}\n시간 : ${i.walkRecord.walkTime}"
            picture?.setImageResource(R.mipmap.ic_launcher)
        }
    }
}