package com.example.ddubuck.ui.badge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ddubuck.R
import kotlinx.android.synthetic.main.fragment_badge.view.*

class BadgeFragment : Fragment() {

    private lateinit var badgeViewModel: BadgeViewModel

    private lateinit var recyclerview: RecyclerView
    private lateinit var adapter: BadgeAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        badgeViewModel =
//                ViewModelProvider(this).get(BadgeViewModel::class.java)
//
////        val textView: TextView = root.findViewById(R.id.text_badge)
//        badgeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        val root = inflater.inflate(R.layout.fragment_badge, container, false)

        recyclerview = root.findViewById(R.id.badgeRecyclerview)
        val adapter = BadgeAdapter(BadgeData(),this)

        root.badgeRecyclerview.apply{
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(root.context,LinearLayoutManager.VERTICAL,false)
        }


        return root
    }
    private fun BadgeData(): MutableList<Badge>{
        val BadgeData = mutableListOf<Badge>()
        BadgeData.add(Badge("챌린지 도전","챌린지를 도전하면서 얻을 수 있는\n배지입니다. 지금 도전해볼까요?",
            R.drawable.ic_badge_challenge_goodjob,R.drawable.ic_badge_challenge_everyday,R.drawable.ic_badge_challenge_allclear,
            "첫 산책","연속 출석","올 클리어",
            R.drawable.ic_badge_challenge_1km,R.drawable.ic_badge_challenge_5km,R.drawable.ic_badge_challenge_10km,
            "1km","5km","10km"))
        BadgeData.add(Badge("기념일","특정 기념일 마다 모을 수 있는 배지\n우리 이 기회를 놓치면 안 되겠죠?",
            R.drawable.ic_badge_anniversary_3_8,R.drawable.ic_badge_anniversary_6_5,R.drawable.ic_badge_anniversary_8_15,
            "세계 여성의 날","세계 환경의 날","광복절 기념",
            R.drawable.ic_badge_anniversary_4_22,0,0,
            "지구의 날","",""))
        BadgeData.add(Badge("특별 한정 도전","날마다 오는 배지가 아닙니다! \n특별한 날에만 모으는 나만의 소중한 배지!",
            R.drawable.ic_badge_special_road,R.drawable.ic_badge_special_spring,0,
            "벛꽃","봄 맞이","",
            0,0,0,
            "","",""))
        return BadgeData
    }
}