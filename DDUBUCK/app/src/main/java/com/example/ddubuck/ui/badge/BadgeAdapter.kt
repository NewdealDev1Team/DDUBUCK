package com.example.ddubuck.ui.badge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R

class BadgeAdapter(
    private val badgeList: MutableList<Badge>,
    private val owner: BadgeFragment
) : RecyclerView.Adapter<BadgeAdapter.BadgeHolder>(){

    inner class BadgeHolder(rowRoot: View) : RecyclerView.ViewHolder(rowRoot) {
        val title : TextView =  rowRoot.findViewById(R.id.badge_title)
        val bottomTitle : TextView =  rowRoot.findViewById(R.id.badge_title_bottom_content)
//        val oneLine : LinearLayout = rowRoot.findViewById(R.id.challenge_layout_oneLine)
        val oneLineImage1 : ImageView = rowRoot.findViewById(R.id.badge_image_goodjob)
        val oneLineImage2 : ImageView = rowRoot.findViewById(R.id.badge_image_everyDay)
        val oneLineImage3 : ImageView = rowRoot.findViewById(R.id.badge_image_allClear)
        val twoLineText1 : TextView = rowRoot.findViewById(R.id.badge_text_goodjob)
        val twoLineText2 : TextView = rowRoot.findViewById(R.id.badge_text_everyday)
        val twoLineText3 : TextView = rowRoot.findViewById(R.id.badge_text_allclear)
        val threeLineImage1 : ImageView = rowRoot.findViewById(R.id.badge_image_1km)
        val threeLineImage2 : ImageView = rowRoot.findViewById(R.id.badge_image_5km)
        val threeLineImage3 : ImageView = rowRoot.findViewById(R.id.badge_image_10km)
        val fourLineText1 : TextView = rowRoot.findViewById(R.id.badge_text_1km)
        val fourLineText2 : TextView = rowRoot.findViewById(R.id.badge_text_5km)
        val fourLineText3 : TextView = rowRoot.findViewById(R.id.badge_text_10km)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_badge,parent,false)
        return BadgeHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeHolder, position: Int) {
        val badgeData = badgeList[position]
        with(holder){
            title.text = badgeData.title
            bottomTitle.text = badgeData.bottomTitle
            oneLineImage1.setImageResource(badgeData.oneLineImage1)
            oneLineImage2.setImageResource(badgeData.oneLineImage2)
            oneLineImage3.setImageResource(badgeData.oneLineImage3)
            twoLineText1.text = badgeData.twoLineText1
            twoLineText2.text = badgeData.twoLineText2
            twoLineText3.text = badgeData.twoLineText3
            threeLineImage1.setImageResource(badgeData.threeLineImage1)
            threeLineImage2.setImageResource(badgeData.threeLineImage2)
            threeLineImage3.setImageResource(badgeData.threeLineImage3)
            fourLineText1.text = badgeData.fourLineText1
            fourLineText2.text = badgeData.fourLineText2
            fourLineText3.text = badgeData.fourLineText3
        }
    }

    override fun getItemCount(): Int = badgeList.size
}