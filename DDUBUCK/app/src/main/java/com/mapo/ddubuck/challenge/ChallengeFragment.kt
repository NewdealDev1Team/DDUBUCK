package com.mapo.ddubuck.challenge

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.MainActivityViewModel
import com.mapo.ddubuck.R
import com.mapo.ddubuck.challenge.detail.ChallengeDetailFragment
import com.mapo.ddubuck.login.UserService
import com.mapo.ddubuck.login.UserValidationInfo
import com.mapo.ddubuck.mypage.MypageViewModel
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChallengeFragment : Fragment() {
    private lateinit var challengeViewModel: ChallengeViewModel
    private lateinit var challengeDetailFragment: ChallengeDetailFragment
    private lateinit var challengeFragment: ChallengeFragment

    private val mainViewModel: MainActivityViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        challengeFragment = ChallengeFragment()

        challengeViewModel = ViewModelProvider(this).get(ChallengeViewModel::class.java)
        val challengeView: ViewGroup = inflater.inflate(R.layout.fragment_challenge, container, false) as ViewGroup

        val dduubuckChallengeTitle: TextView =
            challengeView.findViewById(R.id.ddubuck_challenge_title)
        val ddubuckChallengeText: TextView = challengeView.findViewById(R.id.ddubuck_challenge_text)

        dduubuckChallengeTitle.text = "뚜벅뚜벅 챌린지"
        ddubuckChallengeText.text = "우리 함께 기분 좋은 산책,\n" + "시작해볼까요?"

        val ddubuckChallengeAdapter = ChallengeAdapter(ddubuckChallenge)

        val challengeRecyclerView: RecyclerView =
            challengeView.findViewById(R.id.challenge_recyclerView)
        challengeRecyclerView.isNestedScrollingEnabled = false
        challengeRecyclerView.apply {
            this.adapter = ddubuckChallengeAdapter
            this.layoutManager = GridLayoutManager(challengeView.context, 2)

            ddubuckChallengeAdapter.setItemClickListener(object :
                ChallengeAdapter.ItemClickListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onClick(view: View, position: Int) {
                    challengeDetailFragment = ChallengeDetailFragment()
                    toDetailPage(challengeDetailFragment, ddubuckChallenge[position].title, position, 1)
                }
            })
        }

        val hiddenChallengeTitle: TextView = challengeView.findViewById(R.id.hidden_challenge_title)
        val hiddenChallengeText: TextView = challengeView.findViewById(R.id.hidden_challenge_text)

        hiddenChallengeTitle.text = "히든 챌린지"
        hiddenChallengeText.text = "자유산책을 하면서\n" + "숨겨진 챌린지를 찾아보세요"

        val hiddenChallengeAdapter = ChallengeAdapter(hiddenChallenge)

        val hiddenChallengeRecyclerView: RecyclerView =
            challengeView.findViewById(R.id.hidden_challenge_recyclerView)
        hiddenChallengeRecyclerView.isNestedScrollingEnabled = false
        hiddenChallengeRecyclerView.apply {
            this.adapter = hiddenChallengeAdapter
            this.layoutManager = GridLayoutManager(challengeView.context, 2)

            hiddenChallengeAdapter.setItemClickListener(object :
                ChallengeAdapter.ItemClickListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onClick(view: View, position: Int) {
                    challengeDetailFragment = ChallengeDetailFragment()
                    toDetailPage(challengeDetailFragment, hiddenChallenge[position].title, position, 2)
                }
            })
        }

        val petChallengeTitle: TextView = challengeView.findViewById(R.id.pet_challenge_title)
        val petChallengeText: TextView = challengeView.findViewById(R.id.pet_challenge_text)

        petChallengeTitle.text = "반려동물과 함께"
        petChallengeText.text = "나의 소중한 반려동물이 있나요?\n" + "우리 함께 산책해 볼까요?"

        val petChallengeAdapter = ChallengeAdapter(petChallenge)

        val petChallengeRecyclerView: RecyclerView =
            challengeView.findViewById(R.id.pet_challenge_recyclerView)
        petChallengeRecyclerView.isNestedScrollingEnabled = false
        petChallengeRecyclerView.apply {
            this.adapter = petChallengeAdapter
            this.layoutManager = GridLayoutManager(challengeView.context, 2)

            petChallengeAdapter.setItemClickListener(object : ChallengeAdapter.ItemClickListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onClick(view: View, position: Int) {
                    challengeDetailFragment = ChallengeDetailFragment()
                    toDetailPage(challengeDetailFragment, petChallenge[position].title, position, 3)
                }
            })
        }

        return challengeView
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun toDetailPage(detailFragment: Fragment, toolbarTitle: String, index: Int, sectionNumber: Int) {
        val bundle = Bundle()
        bundle.putString("index", index.toString())
        bundle.putString("section", sectionNumber.toString())

        detailFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.challenge_fragment, detailFragment)
            .addToBackStack(MainActivity.CHALLENGE_TAG)
            .commit()

        mainViewModel.toolbarTitle.value = toolbarTitle
    }

    private val ddubuckChallenge: MutableList<Challenge> = mutableListOf(
        Challenge(R.drawable.ic_cumulative_distance, "누적거리", "내가 걸어온 만큼!"),
        Challenge(R.drawable.ic_walking_count, "당일 걸음 수", "과연 오늘은 몇 보?"),
        Challenge(R.drawable.ic_course_complete, "코스완료", "코스 클리어하는 재미!")
    )

    private val hiddenChallenge: MutableList<Challenge> = mutableListOf(
        Challenge(R.drawable.ic_place, "플레이스", "내가 다녀간 핫플은?"),
        Challenge(R.drawable.ic_weather, "날씨", "히든 챌린지의 묘미!")
    )

    private val petChallenge: MutableList<Challenge> = mutableListOf(
        Challenge(R.drawable.ic_pet_distance, "누적거리", "우리함께 걸어요"),
        Challenge(R.drawable.ic_pet_course_complete, "코스완료", "함께 클리어 해요!")
    )
}
