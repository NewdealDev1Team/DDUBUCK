package com.mapo.ddubuck.challenge

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
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
import com.mapo.ddubuck.mypage.Audit
import com.mapo.ddubuck.mypage.MypageViewModel
import com.mapo.ddubuck.sharedpref.BookmarkSharedPreferences
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_bookmark.*
import kotlinx.android.synthetic.main.fragment_challenge_detail.*
import kotlinx.android.synthetic.main.fragment_walk_time.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.OutputStream

class ChallengeFragment(val owner: Activity) : Fragment() {
    private lateinit var challengeDetailFragment: ChallengeDetailFragment
    private lateinit var challengeFragment: ChallengeFragment


    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private val challengeViewModel: ChallengeViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        challengeFragment = ChallengeFragment(owner)

        var bookmarkedChallenge: ArrayList<Challenge> =
            BookmarkSharedPreferences.getBookmarkedChallenge(owner)
        val challengeView: ViewGroup =
            inflater.inflate(R.layout.fragment_challenge, container, false) as ViewGroup

        val dduubuckChallengeTitle: TextView =
            challengeView.findViewById(R.id.ddubuck_challenge_title)
        val ddubuckChallengeText: TextView = challengeView.findViewById(R.id.ddubuck_challenge_text)

        dduubuckChallengeTitle.text = "???????????? ?????????"
        ddubuckChallengeText.text = "?????? ?????? ?????? ?????? ??????,\n" + "???????????????????"

        val ddubuckChallengeAdapter =
            activity?.let { ChallengeAdapter(ddubuckChallenge, bookmarkedChallenge, it) }

        val challengeRecyclerView: RecyclerView =
            challengeView.findViewById(R.id.challenge_recyclerView)
        challengeRecyclerView.isNestedScrollingEnabled = false
        challengeRecyclerView.apply {
            this.adapter = ddubuckChallengeAdapter
            this.layoutManager = GridLayoutManager(challengeView.context, 2)

            ddubuckChallengeAdapter?.setItemClickListener(object :
                ChallengeAdapter.ItemClickListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onClick(view: View, position: Int) {
                    challengeDetailFragment = ChallengeDetailFragment()
                    toDetailPage(challengeDetailFragment,
                        ddubuckChallenge[position].title,
                        position,
                        1)
                }
            })
        }

        val hiddenChallengeTitle: TextView = challengeView.findViewById(R.id.hidden_challenge_title)
        val hiddenChallengeText: TextView = challengeView.findViewById(R.id.hidden_challenge_text)

        hiddenChallengeTitle.text = "?????? ?????????"
        hiddenChallengeText.text = "??????????????? ?????????\n" + "????????? ???????????? ???????????????"

        val hiddenChallengeAdapter =
            activity?.let { ChallengeAdapter(hiddenChallenge, bookmarkedChallenge, it) }


        val hiddenChallengeRecyclerView: RecyclerView =
            challengeView.findViewById(R.id.hidden_challenge_recyclerView)
        hiddenChallengeRecyclerView.isNestedScrollingEnabled = false
        hiddenChallengeRecyclerView.apply {
            this.adapter = hiddenChallengeAdapter
            this.layoutManager = GridLayoutManager(challengeView.context, 2)

            hiddenChallengeAdapter?.setItemClickListener(object :
                ChallengeAdapter.ItemClickListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onClick(view: View, position: Int) {
                    challengeDetailFragment = ChallengeDetailFragment()
                    toDetailPage(challengeDetailFragment,
                        hiddenChallenge[position].title,
                        position,
                        2)
                }
            })
        }

        val petChallengeTitle: TextView = challengeView.findViewById(R.id.pet_challenge_title)
        val petChallengeText: TextView = challengeView.findViewById(R.id.pet_challenge_text)

        petChallengeTitle.text = "??????????????? ??????"
        petChallengeText.text = "?????? ????????? ??????????????? ??????????\n" + "?????? ?????? ????????? ??????????"

        val petChallengeAdapter =
            activity?.let { ChallengeAdapter(petChallenge, bookmarkedChallenge, it) }


        val petChallengeRecyclerView: RecyclerView =
            challengeView.findViewById(R.id.pet_challenge_recyclerView)
        petChallengeRecyclerView.isNestedScrollingEnabled = false
        petChallengeRecyclerView.apply {
            this.adapter = petChallengeAdapter
            this.layoutManager = GridLayoutManager(challengeView.context, 2)

            petChallengeAdapter?.setItemClickListener(object : ChallengeAdapter.ItemClickListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onClick(view: View, position: Int) {
                    challengeDetailFragment = ChallengeDetailFragment()
                    toDetailPage(challengeDetailFragment, petChallenge[position].title, position, 3)
                }
            })
        }

        challengeViewModel.isChanged.observe(viewLifecycleOwner, {
            ddubuckChallengeAdapter?.updateRecyclerView(BookmarkSharedPreferences.getBookmarkedChallenge(
                owner))
        }
        )

        ddubuckChallengeAdapter?.isBookmarkChanged?.observe(viewLifecycleOwner, {
            challengeViewModel.isChanged.value = true
        })


        challengeViewModel.isChanged.observe(viewLifecycleOwner, {
            hiddenChallengeAdapter?.updateRecyclerView(BookmarkSharedPreferences.getBookmarkedChallenge(
                owner))
        }
        )

        hiddenChallengeAdapter?.isBookmarkChanged?.observe(viewLifecycleOwner, {
            challengeViewModel.isChanged.value = true
        })


        challengeViewModel.isChanged.observe(viewLifecycleOwner, {
            petChallengeAdapter?.updateRecyclerView(BookmarkSharedPreferences.getBookmarkedChallenge(
                owner))
        }
        )

        petChallengeAdapter?.isBookmarkChanged?.observe(viewLifecycleOwner, {
            challengeViewModel.isChanged.value = true
        })

        return challengeView
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun toDetailPage(
        detailFragment: Fragment,
        toolbarTitle: String,
        index: Int,
        sectionNumber: Int,
    ) {
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

    private val ddubuckChallenge: ArrayList<Challenge> = arrayListOf(
        Challenge(R.drawable.ic_acumulative_distance, "????????????", "?????? ????????? ??????!", 1, 0),
        Challenge(R.drawable.ic_walking_count, "?????? ?????? ???", "?????? ????????? ??? ????", 1, 1),
        Challenge(R.drawable.ic_course_complete, "????????????", "?????? ??????????????? ??????!", 1, 2)
    )

    private val hiddenChallenge: ArrayList<Challenge> = arrayListOf(
        Challenge(R.drawable.ic_place, "????????????", "?????? ????????? ??????????", 2, 0),
        Challenge(R.drawable.ic_weather, "??????", "?????? ???????????? ??????!", 2, 1)
    )

    private val petChallenge: ArrayList<Challenge> = arrayListOf(
        Challenge(R.drawable.ic_pet_distance, "????????????", "???????????? ?????????", 3, 0),
        Challenge(R.drawable.ic_pet_course_complete, "????????????", "?????? ????????? ??????!", 3, 1)
    )

}
