package com.mapo.ddubuck.mypage

import android.app.Activity
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
import com.mapo.ddubuck.sharedpref.BookmarkSharedPreferences
import kotlinx.android.synthetic.main.challenge_card_layout.view.*


class BookmarkChallengeAdapter(
    private val owner: Activity,
    private val bookmarkedCourse: ArrayList<Challenge>,
) :
    RecyclerView.Adapter<BookmarkChallengeAdapter.ChallengeViewHolder>() {


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
        var bookmarkButton: ToggleButton = itemView.challenge_bookmark
    }


    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        Glide.with(holder.challengeItemImage)
            .load(bookmarkedCourse[position].image)
            .into(holder.challengeItemImage)

        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)
        }
        holder.bookmarkButton.isChecked = bookmarkedCourse.contains(bookmarkedCourse[position])

        holder.bookmarkButton.setOnClickListener {
            if (holder.bookmarkButton.isChecked) {
                if (!bookmarkedCourse.contains(bookmarkedCourse[position])) {
                    bookmarkedCourse.add(bookmarkedCourse[position])
                }
                BookmarkSharedPreferences.setBookmarkChallenge(owner, bookmarkedCourse)

            } else {
                if (bookmarkedCourse.contains(bookmarkedCourse[position])) {
                    bookmarkedCourse.remove(bookmarkedCourse[position])
                }
                BookmarkSharedPreferences.setBookmarkChallenge(owner, bookmarkedCourse)
            }
           updateRecyclerView(BookmarkSharedPreferences.getBookmarkedChallenge(owner))

        }

        holder.challengeItemTitle.text = bookmarkedCourse[position].title
        holder.challengeItemText.text = bookmarkedCourse[position].infoText
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ChallengeViewHolder {
        val challengeView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.challenge_card_layout, viewGroup, false)

        return ChallengeViewHolder(challengeView)
    }


    override fun getItemCount(): Int {
        return bookmarkedCourse.size
    }

    fun updateRecyclerView(bookmarkedChallenge: ArrayList<Challenge>) {
        bookmarkedCourse.clear()
        bookmarkedCourse.addAll(bookmarkedChallenge)
        this.notifyDataSetChanged()
    }

}