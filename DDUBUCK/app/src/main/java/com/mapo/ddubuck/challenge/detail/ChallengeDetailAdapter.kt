package com.mapo.ddubuck.challenge.detail

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.mapo.ddubuck.R
import kotlinx.android.synthetic.main.challenge_detail_layout.view.*
import java.io.File

class ChallengeDetailAdapter(private val title: MutableList<String>, private val image: MutableList<String>, val context: Context) :
    RecyclerView.Adapter<ChallengeDetailAdapter.ChallengeDetailViewHolder>() {

    class ChallengeDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var challengeDetailItemImage: ImageView = itemView.challenge_detail_image
        var challengeDetailItemTitle: TextView = itemView.challenge_detail_title
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): ChallengeDetailViewHolder {
        val challengeDetailView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.challenge_detail_layout, viewGroup, false)

        return ChallengeDetailViewHolder(challengeDetailView)
    }

    override fun onBindViewHolder(
        holder: ChallengeDetailViewHolder,
        position: Int,
    ) {
        holder.challengeDetailItemTitle.text = title[position]

        GlideToVectorYou.justLoadImage(context as Activity?, Uri.parse(image[position]), holder.challengeDetailItemImage)
    }

    override fun getItemCount(): Int {
        return image.size
    }


}
