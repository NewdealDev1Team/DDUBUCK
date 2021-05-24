package com.mapo.ddubuck.mypage

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mapo.ddubuck.R
import com.mapo.ddubuck.challenge.Challenge
import com.mapo.ddubuck.challenge.ChallengeAdapter
import com.mapo.ddubuck.challenge.ChallengeViewModel
import com.mapo.ddubuck.sharedpref.BookmarkSharedPreferences
import kotlinx.android.synthetic.main.challenge_card_layout.view.*


class BookmarkChallengeAdapter(
    private val owner: Activity,
    private val challenge: ArrayList<Challenge>,
) :
    RecyclerView.Adapter<BookmarkChallengeAdapter.ChallengeViewHolder>() {
    var bookmarkedChallenge: ArrayList<Challenge> = BookmarkSharedPreferences.getBookmarkedChallenge(owner)


    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var challengeItemImage: ImageView = itemView.challenge_item_image
        var challengeItemTitle: TextView = itemView.challenge_card_title
        var challengeItemText: TextView = itemView.challenge_card_text
        var bookmarkButton: ToggleButton = itemView.challenge_bookmark

    }


    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        Glide.with(holder.challengeItemImage)
            .load(challenge[position].image)
            .into(holder.challengeItemImage)

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }

        if (bookmarkedChallenge.contains(challenge[position])) {
            holder.bookmarkButton.isChecked = true
        }

        holder.bookmarkButton.setOnClickListener {

            if (holder.bookmarkButton.isChecked) {
                if (!bookmarkedChallenge.contains(challenge[position])) {
                    bookmarkedChallenge.add(challenge[position])
                }
                BookmarkSharedPreferences.setBookmarkChallenge(owner, bookmarkedChallenge)
            } else {
                if (bookmarkedChallenge.contains(challenge[position])) {
                    bookmarkedChallenge.remove(challenge[position])
                }
                BookmarkSharedPreferences.setBookmarkChallenge(owner, bookmarkedChallenge)
            }
            updateRecyclerView(bookmarkedChallenge)
            Log.e("챌린", BookmarkSharedPreferences.getBookmarkedChallenge(owner).toString())
        }

        holder.challengeItemTitle.text = challenge[position].title
        holder.challengeItemText.text = challenge[position].infoText
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ChallengeViewHolder {
        val challengeView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.challenge_card_layout, viewGroup, false)

        return ChallengeViewHolder(challengeView)
    }


    override fun getItemCount(): Int {
        return challenge.size
    }

    private fun updateRecyclerView(bookmarkChallenge: ArrayList<Challenge>) {
        challenge.clear()
        challenge.addAll(bookmarkChallenge)
        this.notifyDataSetChanged()
    }
}