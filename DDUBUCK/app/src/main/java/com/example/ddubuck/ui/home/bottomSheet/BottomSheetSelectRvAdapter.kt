package com.example.ddubuck.ui.home.bottomSheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import com.example.ddubuck.data.home.CourseItem
import com.example.ddubuck.data.home.WalkRecord
import com.example.ddubuck.ui.home.HomeFragment
import com.example.ddubuck.ui.home.bottomSheet.BottomSheetCourseDetailFragment
import com.example.ddubuck.ui.home.bottomSheet.BottomSheetFreeDetailFragment

class BottomSheetSelectRvAdapter(private val itemList: ArrayList<CourseItem>,
                                 private val fm: FragmentManager,):
    RecyclerView.Adapter<BottomSheetSelectRvAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_select_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(itemList[position], fm)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)  {
        private val title = itemView?.findViewById<TextView>(R.id.sheet_select_item_titleTv)
        private val body = itemView?.findViewById<TextView>(R.id.sheet_select_item_bodyTv)
        private val picture = itemView?.findViewById<ImageView>(R.id.sheet_select_item_pictureIv)

        fun bind(i: CourseItem, fm: FragmentManager) {
            if(i.isFreeWalk) {
                itemView.setOnClickListener{selectItem(fm,
                        CourseItem(
                                true,
                                "자유산책",
                                "자유산책입니다",
                                WalkRecord(listOf(), listOf(), listOf(), 1, 1, 1.0)),)}
                title?.text = "자유산책"
                body?.text = "나만의 자유로운 산책,\n즐길 준비 되었나요?"
                picture?.setImageResource(R.mipmap.ic_launcher)
            } else {
                itemView.setOnClickListener{selectItem(fm,i)}
                title?.text = i.title
                body?.text = "걸음수 : ${i.walkRecord.stepCount}\n거리 : ${i.walkRecord.distance}\n시간 : ${i.walkRecord.walkTime}"
                picture?.setImageResource(R.mipmap.ic_launcher)
            }
        }

        fun selectItem(fm:FragmentManager, courseItem : CourseItem) {
            if(courseItem.isFreeWalk) {
                val frag = BottomSheetFreeDetailFragment()
                val fmTransaction = fm.beginTransaction()
                fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                fmTransaction.replace(R.id.bottom_sheet_container,frag, HomeFragment.BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(HomeFragment.DETAIL_PAGE_FRAG).commit()
            } else {
                val frag = BottomSheetCourseDetailFragment(courseItem)
                val fmTransaction = fm.beginTransaction()
                fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                fmTransaction.replace(R.id.bottom_sheet_container,frag, HomeFragment.BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(HomeFragment.DETAIL_PAGE_FRAG).commit()
            }
        }

    }
}
