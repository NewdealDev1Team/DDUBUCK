package com.example.ddubuck.ui.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import kotlinx.android.synthetic.main.challenge_card_layout.view.*

class PetChallengeAdapter: RecyclerView.Adapter<PetChallengeAdapter.PetChallengeViewHolder>() {


    val petChallengeImages = intArrayOf(
        R.drawable.ic_pet_distance,
        R.drawable.ic_pet_course_complete,
    )

    val petChallengeTitles = arrayOf(
        "누적거리",
        "코스완료"
    )

    val petChallengeText = arrayOf(
        "우리함께 걸어요",
        "함께 클리어 해요!"
    )

    class PetChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var petChallengeItemImage: ImageView = itemView.challenge_item_image
        var petChallengeItemTitle: TextView = itemView.challenge_card_title
        var petChallengeItemText: TextView = itemView.challenge_card_text

    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): PetChallengeViewHolder {
        val petChallengeView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.challenge_card_layout, viewGroup, false)

        return PetChallengeViewHolder(petChallengeView)
    }

    override fun onBindViewHolder(
        holder: PetChallengeViewHolder,
        position: Int,
    ) {
        holder.petChallengeItemImage.setImageResource(petChallengeImages[position])
        holder.petChallengeItemTitle.text = petChallengeTitles[position]
        holder.petChallengeItemText.text = petChallengeText[position]
    }

    override fun getItemCount(): Int {
        return petChallengeImages.size
    }
}