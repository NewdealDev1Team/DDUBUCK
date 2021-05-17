package com.mapo.ddubuck.badge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.badge.BadgeImage
import com.mapo.ddubuck.data.badge.RetrofitBadge
import com.mapo.ddubuck.data.badge.Special
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import kotlinx.android.synthetic.main.fragment_badge.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BadgeFragment : Fragment() {

    private lateinit var recyclerview: RecyclerView
    private lateinit var adapter: BadgeAdapter
    private lateinit var badgeImage : BadgeImage


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val root = inflater.inflate(R.layout.fragment_badge, container, false)

//        recyclerview = root.findViewById(R.id.badgeChallengeRecyclerview)
//        val challengeAdapter = BadgeAdapter(BadgeChallengeData(),this)
//        root.badgeChallengeRecyclerview.apply {
//            this.adapter = challengeAdapter
//            this.layoutManager =
//                GridLayoutManager(root.context, 3, GridLayoutManager.VERTICAL, false)
//        }
//
//        recyclerview = root.findViewById(R.id.badgeAnniversaryRecyclerview)
//        val anniversaryAdapter = BadgeAdapter(BadgeAnniversaryData(),this)
//        root.badgeAnniversaryRecyclerview.apply {
//            this.adapter = anniversaryAdapter
//            this.layoutManager =
//                GridLayoutManager(root.context, 3, GridLayoutManager.VERTICAL, false)
//        }

//        recyclerview = root.findViewById(R.id.badgeSpecialRecyclerview)
//        val limitAdapter = BadgeAdapter(BadgeSpecialData())
//        root.badgeSpecialRecyclerview.apply {
//            this.adapter = limitAdapter
//            this.layoutManager =
//                GridLayoutManager(root.context, 3, GridLayoutManager.VERTICAL, false)
//        }

//        context?.let { UserSharedPreferences.getUserId(it) }?.let {
//            val userKey: Int = it.toInt()
//            Log.d("userKey----", "$userKey")
//            RetrofitBadge.instance.getRestsBadge(userKey).enqueue(object : Callback<BadgeImage> {
//                override fun onResponse(call: Call<BadgeImage>, response: Response<BadgeImage>) {
//                    if (response.isSuccessful) {
//                        Log.d("text", "연결성공")
//                        response.body()?.let {
//                            Log.d("dddd","${it}")
//                            recyclerview = root.findViewById(R.id.badgeSpecialRecyclerview)
//                            val limitAdapter = BadgeAdapter(BadgeSpecialData(it.special))
//                            root.badgeSpecialRecyclerview.apply {
//                                this.adapter = limitAdapter
//                                this.layoutManager =
//                                    GridLayoutManager(root.context, 3, GridLayoutManager.VERTICAL, false)
//                            }
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<BadgeImage>, t: Throwable) {
//                    Log.d("error", t.message.toString())
//                }
//            })
//        }
        return root
    }
    private fun BadgeSpecialData(image: List<Special>): MutableList<Badge> {
        val BadgeData = mutableListOf<Badge>()
//        BadgeData.add(Badge(image[0].CherryBlossomTrees, "벚꽃길"))
//        BadgeData.add(Badge(image[0].Spring, "봄 맞이"))
//        Log.d("dddd","${image[0].CherryBlossomTrees}")
//        Log.d("dddd","${image[0].Spring}")
        return BadgeData
    }
}

    private fun BadgeChallengeData(): MutableList<Badge> {
        val BadgeData = mutableListOf<Badge>()
//        BadgeData.add(Badge(R.drawable.ic_badge_challenge_goodjob, "첫 출석"))
//        BadgeData.add(Badge(R.drawable.ic_badge_challenge_everyday, "매일 도전"))
//        BadgeData.add(Badge(R.drawable.ic_badge_challenge_allclear, "올 클리어"))
//        BadgeData.add(Badge(R.drawable.ic_badge_challenge_goodjob, "1km"))
//        BadgeData.add(Badge(R.drawable.ic_badge_challenge_everyday, "5km"))
//        BadgeData.add(Badge(R.drawable.ic_badge_challenge_allclear, "10km"))
        return BadgeData
    }

    private fun BadgeAnniversaryData(): MutableList<Badge> {
        val BadgeData = mutableListOf<Badge>()
//        BadgeData.add(Badge(R.drawable.ic_badge_challenge_goodjob, "세계 여성의날 기념"))
//        BadgeData.add(Badge(R.drawable.ic_badge_challenge_everyday, "광복절 기념"))
//        BadgeData.add(Badge(R.drawable.ic_badge_challenge_allclear, "세계 한정의 날 기념"))
//        BadgeData.add(Badge(R.drawable.ic_badge_challenge_goodjob, "세계 자연의 날 기념"))
        return BadgeData
    }



