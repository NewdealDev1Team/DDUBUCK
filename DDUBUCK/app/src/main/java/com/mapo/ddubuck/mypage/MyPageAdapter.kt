package com.mapo.ddubuck.mypage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.R
import com.mapo.ddubuck.challenge.Challenge
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import kotlinx.android.synthetic.main.fragment_mypage.view.user_route_title
import kotlinx.android.synthetic.main.my_course_layout.view.*

class MyPageAdapter(private val audit: ArrayList<Audit>, private val complete: ArrayList<Complete>) : RecyclerView.Adapter<MyPageAdapter.MyPageViewHolder>() {
    inner class MyPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userRouteItemTitle: TextView = itemView.user_route_title
        var userRouteItemAudit: TextView = itemView.user_route_judge
        var userRouteItemComplete: TextView = itemView.user_route_judge_done

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MyPageViewHolder {
        val myRouteView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.my_course_layout, viewGroup, false)

        return MyPageViewHolder(myRouteView)
    }

    override fun onBindViewHolder(holder: MyPageViewHolder, position: Int) {

        if (position < audit.size) {
            holder.userRouteItemTitle.text = audit[position].title
            holder.userRouteItemComplete.isInvisible = true
            holder.userRouteItemAudit.isInvisible = false

        } else {
            holder.userRouteItemTitle.text = complete[position - audit.size].title
            holder.userRouteItemAudit.isInvisible = true
            holder.userRouteItemComplete.isInvisible = false
        }

    }

    override fun getItemCount(): Int {
        return audit.size + complete.size
    }

}

