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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val root = inflater.inflate(R.layout.fragment_badge, container, false)


        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            val userKey: Int = it.toInt()
            Log.d("userKey--badgePage--", "$userKey")
            RetrofitBadge.instance.getRestsBadge(userKey).enqueue(object : Callback<BadgeImage> {
                override fun onResponse(call: Call<BadgeImage>, response: Response<BadgeImage>) {
                    if (response.isSuccessful) {
                        Log.d("userKey-", "연결성공 배지 이미지")

                        response.body()?.let {
                            Log.d("dddd","${it.challenge}")
                            Log.d("dddd","${it.anniversary}")
                            Log.d("dddd","${it.special}")

                            recyclerview = root.findViewById(R.id.badgeChallengeRecyclerview)
                            recyclerview.isNestedScrollingEnabled = false
                            val challengeAdapter = BadgeAdapter(BadgeChallengeData())
                            root.badgeChallengeRecyclerview.apply {
                                this.adapter = challengeAdapter
                                this.layoutManager =
                                    GridLayoutManager(root.context, 3, GridLayoutManager.VERTICAL, false)
                            }

                            recyclerview = root.findViewById(R.id.badgeAnniversaryRecyclerview)
                            recyclerview.isNestedScrollingEnabled = false
                            val anniversaryAdapter = BadgeAdapter(BadgeAnniversaryData())
                            root.badgeAnniversaryRecyclerview.apply {
                                this.adapter = anniversaryAdapter
                                this.layoutManager =
                                    GridLayoutManager(root.context, 3, GridLayoutManager.VERTICAL, false)
                            }

                            recyclerview = root.findViewById(R.id.badgeSpecialRecyclerview)
                            recyclerview.isNestedScrollingEnabled = false
                            val limitAdapter = BadgeAdapter(BadgeSpecialData(it.special))
                            root.badgeSpecialRecyclerview.apply {
                                this.adapter = limitAdapter
                                this.layoutManager =
                                    GridLayoutManager(root.context, 3, GridLayoutManager.VERTICAL, false)
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<BadgeImage>, t: Throwable) {
                    Log.d("error", t.message.toString())
                }
            })
        }

        return root
    }

    private fun BadgeSpecialData(bimage: List<Special>): MutableList<Badge> {
        val BadgeData = mutableListOf<Badge>()
        BadgeData.add(Badge(bimage.get(1).image.get(0), "벚꽃길"))
        BadgeData.add(Badge(bimage.get(1).image.get(1), "봄 맞이"))
        Log.d("dddd","BadgeSpecialData에 스페셜 이미지 링크 Glid : ${bimage.get(1).image.get(0)}")
        return BadgeData
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
}

