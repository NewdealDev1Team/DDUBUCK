package com.mapo.ddubuck.badge

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
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
                .with(imageView.item_badge_image)
                .load(badgeData.image)
                .error(R.drawable.ic_badge_challenge_goodjob)
                .into(imageView.item_badge_image)

            textView.text = badgeData.text
        }
    }
//    .load(Uri.parse(badgeData.image))

    override fun getItemCount(): Int = badgeList.size
}

