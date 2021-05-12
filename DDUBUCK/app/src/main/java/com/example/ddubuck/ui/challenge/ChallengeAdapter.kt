package com.example.ddubuck.ui.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import kotlinx.android.synthetic.main.challenge_card_layout.view.*

class ChallengeAdapter() : RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>() {

    val ddubuckChallengeImages = intArrayOf(
        R.drawable.ic_cumulative_distance,
        R.drawable.ic_walking_count,
        R.drawable.ic_course_complete,
    )

    class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var challengeItemImage: ImageView = itemView.challenge_item_image
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ChallengeViewHolder {
        val challengeView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.challenge_card_layout, viewGroup, false)

        return ChallengeViewHolder(challengeView)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.challengeItemImage.setImageResource(ddubuckChallengeImages[position])
    }

    override fun getItemCount(): Int {
        return ddubuckChallengeImages.size
    }
}