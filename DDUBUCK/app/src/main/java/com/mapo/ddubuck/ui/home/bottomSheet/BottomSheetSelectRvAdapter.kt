package com.mapo.ddubuck.ui.home.bottomSheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.home.CourseItem
import com.mapo.ddubuck.data.home.WalkRecord
import com.mapo.ddubuck.ui.home.HomeFragment
import kotlin.collections.ArrayList

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

    fun addItem(courseItem: CourseItem) {
        itemList.add(courseItem)
        this.notifyDataSetChanged()
    }

    fun setItems(items : List<CourseItem>) {
        itemList.clear()
        //자유산책 추가
        itemList.add(CourseItem(
            true,
            null,
            "자유산책",
            "자유산책입니다",
            WalkRecord(listOf(), 0.0, 0.0, 1, 1, 1.0)
        ))
        //전달받은 요소 추가
        itemList.addAll(items)
        this.notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        this.notifyDataSetChanged()
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
                itemView.setOnClickListener{
                    selectItem(fm,
                        CourseItem(
                            true,
                            null,
                            "자유산책",
                            "자유산책입니다",
                            WalkRecord(listOf(), 0.0, 0.0, 1, 1, 1.0)
                        ),
                    )
                }
                title?.text = "자유산책"
                body?.text = "나만의 자유로운 산책,\n즐길 준비 되었나요?"
                picture?.setImageResource(R.mipmap.ic_launcher)
            } else {
                itemView.setOnClickListener{selectItem(fm,i)}
                title?.text = i.title
                body?.text = i.description
                picture?.let { v ->
                    Glide.with(itemView).load(i.imgFile).into(v)
                }
                picture?.setBackgroundResource(R.drawable.sheet_select_item_rounded)
                picture?.clipToOutline = true
            }
        }

        private fun selectItem(fm:FragmentManager, courseItem : CourseItem) {
            if(courseItem.isFreeWalk) {
                val frag = BottomSheetFreeDetailFragment()
                val fmTransaction = fm.beginTransaction()
                fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                fmTransaction.replace(R.id.bottom_sheet_container,frag, HomeFragment.BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(MainActivity.HOME_TAG).commit()
            } else {
                val frag = BottomSheetCourseDetailFragment(courseItem)
                val fmTransaction = fm.beginTransaction()
                fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
                fmTransaction.replace(R.id.bottom_sheet_container,frag, HomeFragment.BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(MainActivity.HOME_TAG).commit()
            }
        }

    }
}