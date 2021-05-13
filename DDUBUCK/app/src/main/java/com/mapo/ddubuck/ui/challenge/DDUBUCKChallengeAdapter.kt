package com.example.ddubuck.ui.challenge

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import kotlinx.android.synthetic.main.challenge_card_layout.view.*

class DDUBUCKChallengeAdapter() :
    RecyclerView.Adapter<DDUBUCKChallengeAdapter.ChallengeViewHolder>() {

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListner: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    val ddubuckChallengeImages = intArrayOf(
        R.drawable.ic_cumulative_distance,
        R.drawable.ic_walking_count,
        R.drawable.ic_course_complete,
    )

    val ddubuckChallengeTitles = arrayOf(
        "누적거리",
        "당일 걸음 수",
        "코스완료"
    )

    val ddubuckChallengeText = arrayOf(
        "내가 걸어온 만큼!",
        "과연 오늘은 몇 보?",
        "코스 클리어하는 재미!"
    )


    class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var challengeItemImage: ImageView = itemView.challenge_item_image
        var challengeItemTitle: TextView = itemView.challenge_card_title
        var challengeItemText: TextView = itemView.challenge_card_text

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ChallengeViewHolder {
        val challengeView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.challenge_card_layout, viewGroup, false)

        return ChallengeViewHolder(challengeView)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.challengeItemImage.setImageResource(ddubuckChallengeImages[position])

        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)
        }

        holder.challengeItemTitle.text = ddubuckChallengeTitles[position]
        holder.challengeItemText.text = ddubuckChallengeText[position]
    }

    override fun getItemCount(): Int {
        return ddubuckChallengeImages.size
    }


}