package com.mapo.ddubuck.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mapo.ddubuck.R
import com.mapo.ddubuck.badge.Badge
import com.mapo.ddubuck.badge.BadgeFragment
import kotlinx.android.synthetic.main.challenge_card_layout.view.*

class ChallengeAdapter(private val challenge: MutableList<Challenge>) :
    RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>() {

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListner: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }


    inner class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var challengeItemImage: ImageView = itemView.challenge_item_image
        var challengeItemTitle: TextView = itemView.challenge_card_title
        var challengeItemText: TextView = itemView.challenge_card_text
        var bookmarkButton: ImageView = itemView.challenge_bookmark

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ChallengeViewHolder {
        val challengeView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.challenge_card_layout, viewGroup, false)

        return ChallengeViewHolder(challengeView)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        Glide.with(holder.challengeItemImage)
            .load(challenge[position].image)
            .into(holder.challengeItemImage)

        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)
        }

        holder.bookmarkButton.setOnClickListener {
            holder.bookmarkButton.setImageResource(R.drawable.ic_bookmark_color)
        }

        holder.challengeItemTitle.text = challenge[position].title
        holder.challengeItemText.text = challenge[position].infoText
    }

    override fun getItemCount(): Int {
        return challenge.size
    }

}