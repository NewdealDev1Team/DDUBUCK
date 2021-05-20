package com.mapo.ddubuck.mypage

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.R
import kotlinx.android.synthetic.main.user_course_layout.view.*


class UserRouteAdapter(
    var audit: MutableList<Audit>, private var complete: MutableList<Complete>, val context: Context
) : RecyclerView.Adapter<UserRouteAdapter.MyPageViewHolder>() {

    inner class MyPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userRouteItemTitle: TextView = itemView.user_route_view_title
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
                val auditTitle = audit[position].title.toString()
                val auditPicture = audit[position].picture!!
                val auditDescription = audit[position].description.toString()
                val auditWalkTime = audit[position].walkTime.toString() + "분"
                val auditDistance = audit[position].distance?.toInt().toString() + "km"
                val auditHeight = audit[position].altitude?.toInt().toString() + "m"
                    val auditCreatedAt = audit[position].created_at.toString()

                val dialog = UserCourseDialog(auditTitle,
                    auditPicture,
                    auditDescription,
                    auditWalkTime,
                    auditDistance,
                    auditHeight,
                    "audit",
                    auditCreatedAt,
                    this,
                    context as Activity)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()


            }

        } else {
            holder.userRouteItemTitle.text = complete[position - audit.size].title
            holder.userRouteItemAudit.isInvisible = true
            holder.userRouteItemComplete.isInvisible = false

            val completeTitle = complete[position - audit.size].title.toString()
            val completePicture = complete[position - audit.size].picture!!
            val completeDescription = complete[position - audit.size].description.toString()
            val completeWalkTime = complete[position - audit.size].walkTime.toString() + "분"
            val completeDistance = complete[position - audit.size].distance?.toInt().toString() + "km"
            val completeHeight = complete[position - audit.size].altitude?.toInt().toString() + "m"
            val completeCreatedAt = complete[position - audit.size].created_at.toString()

            holder.itemView.setOnClickListener {
                val dialog = UserCourseDialog(completeTitle,
                    completePicture,
                    completeDescription,
                    completeWalkTime,
                    completeDistance,
                    completeHeight,
                    "complete",
                    completeCreatedAt,
                    this,
                    context as Activity)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()


            }
        }
    }


    override fun getItemCount(): Int {
        return audit.size + complete.size
    }

    fun updateRecyclerView(newAudit: ArrayList<Audit>) {
        audit.clear()
        audit.addAll(newAudit)
        this.notifyDataSetChanged()
    }
}

