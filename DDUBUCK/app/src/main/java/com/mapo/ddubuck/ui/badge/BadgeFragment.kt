package com.example.ddubuck.ui.badge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import kotlinx.android.synthetic.main.fragment_badge.view.*

class BadgeFragment : Fragment() {

    private lateinit var recyclerview: RecyclerView
    private lateinit var adapter: BadgeAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_badge, container, false)

        recyclerview = root.findViewById(R.id.badgeChallengeRecyclerview)
        val challengeAdapter = BadgeAdapter(BadgeChallengeData(),this)
        root.badgeChallengeRecyclerview.apply{
            this.adapter = challengeAdapter
            this.layoutManager = GridLayoutManager(root.context,3,GridLayoutManager.VERTICAL,false)
        }

        recyclerview = root.findViewById(R.id.badgeAnniversaryRecyclerview)
        val anniversaryAdapter = BadgeAdapter(BadgeAnniversaryData(),this)
        root.badgeAnniversaryRecyclerview.apply{
            this.adapter = anniversaryAdapter
            this.layoutManager = GridLayoutManager(root.context,3,GridLayoutManager.VERTICAL,false)
        }

        recyclerview = root.findViewById(R.id.badgeLimitRecyclerview)
        val limitAdapter = BadgeAdapter(BadgeLimitData(),this)
        root.badgeLimitRecyclerview.apply{
            this.adapter = limitAdapter
            this.layoutManager = GridLayoutManager(root.context,3,GridLayoutManager.VERTICAL,false)
        }
        return root
    }
    private fun BadgeChallengeData(): MutableList<Badge>{
        val BadgeData = mutableListOf<Badge>()
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_goodjob,"첫 출석"))
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_everyday,"매일 도전"))
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_allclear,"올 클리어"))
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_goodjob,"1km"))
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_everyday,"5km"))
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_allclear,"10km"))
        return BadgeData
    }

    private fun BadgeAnniversaryData(): MutableList<Badge>{
        val BadgeData = mutableListOf<Badge>()
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_goodjob,"세계 여성의날 기념"))
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_everyday,"광복절 기념"))
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_allclear,"세계 한정의 날 기념"))
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_goodjob,"세계 자연의 날 기념"))
        return BadgeData
    }

    private fun BadgeLimitData(): MutableList<Badge>{
        val BadgeData = mutableListOf<Badge>()
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_goodjob,"벚꽃길"))
        BadgeData.add(Badge(R.drawable.ic_badge_challenge_everyday,"봄 맞이"))
        return BadgeData
    }
}