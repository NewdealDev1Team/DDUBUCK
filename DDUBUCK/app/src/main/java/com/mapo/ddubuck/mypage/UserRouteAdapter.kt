package com.mapo.ddubuck.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mapo.ddubuck.R
import kotlinx.android.synthetic.main.user_course_layout.view.*
import org.w3c.dom.Text

class UserRouteAdapter(
    private var audit: MutableList<Audit>, private var complete: MutableList<Complete>, val context: Context, val inflater: LayoutInflater
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
                    inflater,
                    context as Activity)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

//                val userRouteDialogBuilder = AlertDialog.Builder(context)
//                val userRouteDialog = inflater.inflate(R.layout.dialog_course_view, null)
//
//                val mUserRouteDialog = userRouteDialogBuilder.setView(userRouteDialog).show()
//
//                mUserRouteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//                val userRouteDialogCloseButton  = mUserRouteDialog.findViewById<ImageView>(R.id.dialog_course_view_closeButton)
//                val userRouteDialogTitle = mUserRouteDialog.findViewById<TextView>(R.id.dialog_course_view_title)
//                val userRouteDialogPicture = mUserRouteDialog.findViewById<ImageView>(R.id.dialog_course_view_image_container)
//                val userRouteDialogDescription = mUserRouteDialog.findViewById<TextView>(R.id.dialog_course_view_text)
//                val userRouteDialogWalkTime = mUserRouteDialog.findViewById<TextView>(R.id.dialog_course_view_timeTv)
//                val userRouteDialogDistance = mUserRouteDialog.findViewById<TextView>(R.id.dialog_course_view_distanceTv)
//                val userRouteDialogHeight = mUserRouteDialog.findViewById<TextView>(R.id.dialog_course_view_elevationTv)
//
//                userRouteDialogTitle.text = auditTitle
//
//                Glide.with(context)
//                    .load(auditPicture)
//                    .placeholder(R.color.grey)
//                    .into(userRouteDialogPicture)
//
//                userRouteDialogDescription.text = auditDescription
//                userRouteDialogWalkTime.text = auditWalkTime
//                userRouteDialogDistance.text = auditDistance
//                userRouteDialogHeight.text = auditHeight
//
//                userRouteDialogCloseButton.setOnClickListener {
//                    Log.e("ㅇㅇㅇㅇ","ㅁㅇㄹ")
//                }


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
                    inflater,
                    context as Activity)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()


            }
        }
    }


    override fun getItemCount(): Int {
        return audit.size + complete.size
    }

}

