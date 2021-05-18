package com.mapo.ddubuck.badge

import android.app.Activity
import android.content.Context
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
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.mapo.ddubuck.R
import kotlinx.android.synthetic.main.item_badge.view.*

class BadgeAdapter(
    private val title: MutableList<String>,
    private val image: MutableList<String>,
    val context: Context
) : RecyclerView.Adapter<BadgeAdapter.BadgeHolder>(){

    inner class BadgeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val badgeImageView : ImageView =  itemView.findViewById(R.id.item_badge_image)
        val badgeTextView : TextView =  itemView.findViewById(R.id.item_badge_text)
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): BadgeHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_badge, viewGroup, false)

        return BadgeHolder(view)
    }

    override fun onBindViewHolder(
        holder: BadgeHolder,
        position: Int
    ) {
        holder.badgeTextView.text = title[position]
        GlideToVectorYou.justLoadImage(context as Activity?,
            Uri.parse(image[position]),
            holder.badgeImageView)
    }

    override fun getItemCount(): Int = image.size
}

