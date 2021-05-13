package com.mapo.ddubuck.challenge.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.R
import kotlinx.android.synthetic.main.challenge_detail_layout.view.*

class ChallengeDetailAdapter() :
    RecyclerView.Adapter<ChallengeDetailAdapter.ChallengeDetailViewHolder>() {

    val challengeDistanceDetailImages = intArrayOf(
        R.drawable.ic_place,
        R.drawable.ic_weather,
    )

    val hiddenChallengeTitles = arrayOf(
        "플레이스",
        "날씨"
    )

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
        holder.challengeDetailItemImage.setImageResource(challengeDistanceDetailImages[position])
        holder.challengeDetailItemTitle.text = hiddenChallengeTitles[position]
    }

    override fun getItemCount(): Int {
        return challengeDistanceDetailImages.size
    }


}
