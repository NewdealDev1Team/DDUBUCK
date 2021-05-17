package com.mapo.ddubuck.challenge.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mapo.ddubuck.R
import com.mapo.ddubuck.challenge.Challenge
import kotlinx.android.synthetic.main.challenge_detail_layout.view.*
import kotlinx.android.synthetic.main.item_badge.view.*

class ChallengeDetailAdapter(private val challengeDetail: MutableList<ChallengeDetail>) :
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
        holder.challengeDetailItemTitle.text = challengeDetail[position].title

        Glide.with(holder.challengeDetailItemImage)
            .load(challengeDetail[position].image)
            .into(holder.challengeDetailItemImage)

//        holder.challengeDetailItemImage.setImageResource(challengeDetail[position].image)
    }

    override fun getItemCount(): Int {
        return challengeDetail.size
    }


}
