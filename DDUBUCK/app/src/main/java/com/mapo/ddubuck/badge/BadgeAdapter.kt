package com.mapo.ddubuck.badge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.mapo.ddubuck.R
import kotlinx.android.synthetic.main.item_badge.view.*

class BadgeAdapter(
    private val badgeList: MutableList<Badge>
) : RecyclerView.Adapter<BadgeAdapter.BadgeHolder>(){

    inner class BadgeHolder(rowRoot: View) : RecyclerView.ViewHolder(rowRoot) {
        val imageView : ImageView =  rowRoot.findViewById(R.id.item_badge_image)
        val textView : TextView =  rowRoot.findViewById(R.id.item_badge_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_badge, parent, false)
        return BadgeHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeHolder, position: Int) {
        val badgeData = badgeList[position]
        with(holder){
            Glide
                .with(imageView)
                .load(badgeData.image)
                .into(imageView)

            textView.text = badgeData.text
        }
    }

    override fun getItemCount(): Int = badgeList.size
}

