package com.mapo.ddubuck.mypage

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.R
import kotlinx.android.synthetic.main.fragment_mypage.view.user_route_title
import kotlinx.android.synthetic.main.user_course_layout.view.*

class MyPageAdapter(private val audit: ArrayList<Audit>, private val complete: ArrayList<Complete>, val context: Context) : RecyclerView.Adapter<MyPageAdapter.MyPageViewHolder>(){

    inner class MyPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userRouteItemTitle: TextView = itemView.user_route_title
        var userRouteItemAudit: TextView = itemView.user_route_judge
        var userRouteItemComplete: TextView = itemView.user_route_judge_done

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MyPageViewHolder {
        val myRouteView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.user_course_layout, viewGroup, false)

        return MyPageViewHolder(myRouteView)
    }

    override fun onBindViewHolder(holder: MyPageViewHolder, position: Int) {

        if (position < audit.size) {
            holder.userRouteItemTitle.text = audit[position].title
            holder.userRouteItemComplete.isInvisible = true
            holder.userRouteItemAudit.isInvisible = false
            holder.itemView.setOnClickListener {
                val dialog = UserCourseDialog( audit[position].title.toString(), audit[position].picture!!, audit[position].description.toString(),
                    audit[position].walkTime.toString()+"분", audit[position].distance?.toInt().toString()+"km",audit[position].altitude?.toInt().toString()+"m", "audit", audit[position].created_at.toString(), context as Activity)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
            }

        } else {
            holder.userRouteItemTitle.text = complete[position - audit.size].title
            holder.userRouteItemAudit.isInvisible = true
            holder.userRouteItemComplete.isInvisible = false
            holder.itemView.setOnClickListener {
                val dialog = UserCourseDialog( complete[position - audit.size].title.toString(), complete[position - audit.size].picture!!, complete[position - audit.size].description.toString(),
                    complete[position - audit.size].walkTime.toString()+"분", complete[position - audit.size].distance?.toInt().toString()+"km",complete[position - audit.size].altitude?.toInt().toString()+"m","complete", audit[position].created_at.toString(), context as Activity)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

            }

        }


    }


    override fun getItemCount(): Int {
        return audit.size + complete.size
    }

}

