package com.mapo.ddubuck.ui.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.R
import kotlinx.android.synthetic.main.challenge_card_layout.view.*

class HiddenChallengeAdapter() :
    RecyclerView.Adapter<HiddenChallengeAdapter.HiddenChallengeViewHolder>() {

    val hiddenChallengeImages = intArrayOf(
        R.drawable.ic_place,
        R.drawable.ic_weather,
    )

    val hiddenChallengeTitles = arrayOf(
        "플레이스",
        "날씨"
    )

    val hiddenChallengeText = arrayOf(
        "내가 다녀간 핫플은?",
        "히든 챌린지의 묘미!"
    )

    class HiddenChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var hiddenChallengeItemImage: ImageView = itemView.challenge_item_image
        var hiddenChallengeItemTitle: TextView = itemView.challenge_card_title
        var hiddenChallengeItemText: TextView = itemView.challenge_card_text

    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): HiddenChallengeViewHolder {
        val challengeView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.challenge_card_layout, viewGroup, false)

        return HiddenChallengeViewHolder(challengeView)
    }

    override fun onBindViewHolder(
        holder: HiddenChallengeViewHolder,
        position: Int,
    ) {
        holder.hiddenChallengeItemImage.setImageResource(hiddenChallengeImages[position])
        holder.hiddenChallengeItemTitle.text = hiddenChallengeTitles[position]
        holder.hiddenChallengeItemText.text = hiddenChallengeText[position]
    }

    override fun getItemCount(): Int {
        return hiddenChallengeImages.size
    }


}