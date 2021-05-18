package com.mapo.ddubuck.badge

import android.app.Activity
import android.content.Context
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
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import kotlinx.android.synthetic.main.fragment_badge.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


interface BadgeAPICallback{
    fun onSpecial(
            badgeRecyclerView : RecyclerView,
            badgeImage : BadgeImage,
        )
    fun onAnniversary(
        badgeRecyclerView : RecyclerView,
        badgeImage : BadgeImage,
    )
    fun onChallenge(
        badgeRecyclerView : RecyclerView,
        badgeImage : BadgeImage,
    )
}

class BadgeFragment : Fragment(), BadgeAPICallback {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val badgeDetailView
            = inflater.inflate(R.layout.fragment_badge, container, false)

        val badgeSpecialLayoutManager = GridLayoutManager(badgeDetailView.context,3)
        val badgeAnniversaryLayoutManager = GridLayoutManager(badgeDetailView.context,3)
        val badgeChallengeLayoutManager = GridLayoutManager(badgeDetailView.context,3)

        val badgeSpecialRecyclerView : RecyclerView =
            badgeDetailView.findViewById(R.id.badgeSpecialRecyclerview)
        val badgeAnniversaryRecyclerView : RecyclerView =
            badgeDetailView.findViewById(R.id.badgeAnniversaryRecyclerview)
        val badgeChallengeRecyclerView : RecyclerView =
            badgeDetailView.findViewById(R.id.badgeChallengeRecyclerview)

        badgeSpecialRecyclerView.layoutManager = badgeSpecialLayoutManager
        badgeSpecialRecyclerView.isNestedScrollingEnabled = false

        badgeAnniversaryRecyclerView.layoutManager = badgeAnniversaryLayoutManager
        badgeAnniversaryRecyclerView.isNestedScrollingEnabled = false

        badgeChallengeRecyclerView.layoutManager = badgeChallengeLayoutManager
        badgeChallengeRecyclerView.isNestedScrollingEnabled = false

        setBadgeDetail(badgeSpecialRecyclerView,badgeAnniversaryRecyclerView,badgeChallengeRecyclerView)

        return badgeDetailView
    }

    private fun setBadgeDetail(badgeSpecialRecyclerView: RecyclerView,badgeAnniversaryRecyclerView: RecyclerView,badgeChallengeRecyclerView: RecyclerView) {
        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            val userKey: Int = it.toInt()
            Log.d("userKey--badgePage--", "$userKey")
            RetrofitBadge.instance.getRestsBadge(userKey).enqueue(object : Callback<BadgeImage> {
                override fun onResponse(
                    call: Call<BadgeImage>,
                    response: Response<BadgeImage>
                ){
                    val badgeSpecialElements = response.body()!!
                    val badgeAnniversaryElements = response.body()!!
                    val badgeChallengeElements = response.body()!!
                    onSpecial(badgeSpecialRecyclerView, badgeSpecialElements)
                    onAnniversary(badgeAnniversaryRecyclerView,badgeAnniversaryElements)
                    onChallenge(badgeChallengeRecyclerView, badgeChallengeElements)

                }

                override fun onFailure(call: Call<BadgeImage>, t: Throwable) {
                    Log.d("error", t.message.toString())
                }
            })
        }
    }

    override fun onAnniversary(badgeAnniversaryRecyclerView: RecyclerView, badgeImage: BadgeImage) {
        val anniversary = badgeImage.anniversary

        val anniversaryImage = mutableListOf<String>()
        val anniversaryTitle = mutableListOf<String>()

        for(i in 0 until anniversary[0].title.size){
            anniversaryTitle.add(anniversary[0].title!![i])
            anniversaryImage.add(anniversary[1].image!![i])
        }

        val badgeAdapter = BadgeAdapter(anniversaryTitle,anniversaryImage, context as Activity)
        badgeAnniversaryRecyclerView.adapter = badgeAdapter

    }

    override fun onSpecial(badgeSpecialRecyclerView: RecyclerView, badgeImage: BadgeImage) {
        val special = badgeImage.special

        val specialImage = mutableListOf<String>()
        val specialTitle = mutableListOf<String>()

        for(i in 0 until special[0].title.size){
            specialTitle.add(special[0].title!![i])
            specialImage.add(special[1].image!![i])

            val badgeAdapter = BadgeAdapter(specialTitle, specialImage, context as Activity)
            badgeSpecialRecyclerView.adapter = badgeAdapter
        }
    }

    override fun onChallenge(badgeChallengeRecyclerView: RecyclerView, badgeImage: BadgeImage) {
        val challenge = badgeImage.challenge

        val challengeImage = mutableListOf<String>()
        val challengeTitle = mutableListOf<String>()

        for(i in 0 until challenge[0].title.size){
            challengeTitle.add(challenge[0].title!![i])
            challengeImage.add(challenge[1].image!![i])

            val badgeAdapter = BadgeAdapter(challengeTitle, challengeImage, context as Activity)
            badgeChallengeRecyclerView.adapter = badgeAdapter
        }
    }


}

