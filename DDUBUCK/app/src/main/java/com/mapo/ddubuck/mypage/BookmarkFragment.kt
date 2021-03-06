package com.mapo.ddubuck.mypage

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.mapo.ddubuck.R
import com.mapo.ddubuck.home.HomeMapViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.MainActivityViewModel
import com.mapo.ddubuck.challenge.Challenge
import com.mapo.ddubuck.challenge.ChallengeViewModel
import com.mapo.ddubuck.challenge.detail.ChallengeDetailFragment
import com.mapo.ddubuck.sharedpref.BookmarkSharedPreferences
import com.mapo.ddubuck.sharedpref.UserSharedPreferences

class BookmarkFragment(private val owner: Activity) : Fragment() {
    private lateinit var challengeDetailFragment: ChallengeDetailFragment
    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private val homeMapViewModel : HomeMapViewModel by activityViewModels()
    private val challengeViewModel: ChallengeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {



        val bookmarkViewGroup: ViewGroup = inflater.inflate(R.layout.fragment_bookmark,
            container, false) as ViewGroup
        val bookmarkChallenge =
            context?.let { BookmarkSharedPreferences.getBookmarkedChallenge(it) }
        val bookmarkedChallenge: ArrayList<Challenge> = BookmarkSharedPreferences.getBookmarkedChallenge(owner)

        val challengeAdapter = activity?.let {
            BookmarkChallengeAdapter(it, bookmarkedChallenge)
        }
        val challengeHint: TextView = bookmarkViewGroup.findViewById(R.id.bookmark_challenge_hintText)
        val challengeRecyclerView: RecyclerView =
            bookmarkViewGroup.findViewById(R.id.bookmark_recyclerview)
        challengeRecyclerView.isNestedScrollingEnabled = false
        challengeRecyclerView.isNestedScrollingEnabled = false
        challengeRecyclerView.apply {
            this.adapter = challengeAdapter
            this.layoutManager = GridLayoutManager(bookmarkViewGroup.context, 2)

            challengeAdapter?.setItemClickListener(object :
                BookmarkChallengeAdapter.ItemClickListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onClick(view: View, position: Int) {
                    challengeDetailFragment = ChallengeDetailFragment()
                    bookmarkChallenge?.get(position)
                        ?.let {
                            toDetailPage(challengeDetailFragment,
                                it.title,
                                it.position,
                                it.section)
                        }
                }
            })

            if (BookmarkSharedPreferences.getBookmarkedChallenge(owner).size == 0) {
                challengeRecyclerView.visibility = View.INVISIBLE
                challengeHint.visibility = View.VISIBLE
            } else {
                challengeRecyclerView.visibility = View.VISIBLE
                challengeHint.visibility = View.INVISIBLE
            }
        }

        challengeViewModel.isChanged.observe(viewLifecycleOwner, {
            challengeAdapter?.updateRecyclerView(BookmarkSharedPreferences.getBookmarkedChallenge(owner))
            if (BookmarkSharedPreferences.getBookmarkedChallenge(owner).size == 0) {
                challengeRecyclerView.visibility = View.INVISIBLE
                challengeHint.visibility = View.VISIBLE
            } else {
                challengeRecyclerView.visibility = View.VISIBLE
                challengeHint.visibility = View.INVISIBLE
            }
        })

        challengeAdapter?.isBookmarkChanged?.observe(viewLifecycleOwner, {
            challengeViewModel.isChanged.value = true
            if (BookmarkSharedPreferences.getBookmarkedChallenge(owner).size == 0) {
                challengeRecyclerView.visibility = View.INVISIBLE
                challengeHint.visibility = View.VISIBLE
            } else {
                challengeRecyclerView.visibility = View.VISIBLE
                challengeHint.visibility = View.INVISIBLE
            }
        })


        val initArray = UserSharedPreferences.getBookmarkedCourse(owner)
        bookmarkViewGroup.findViewById<TextView>(R.id.bookmark_course_hintText).let { it ->
            if(initArray.size != 0) {
                it.visibility = View.INVISIBLE
            } else {
                it.visibility = View.VISIBLE
            }
        }

        val mAdapter = BookmarkCourseAdapter(
            owner,
            initArray,
            parentFragmentManager)
        bookmarkViewGroup.findViewById<ViewPager2>(R.id.bookmark_course_viewpager).let { v->
            v.adapter = mAdapter
        }

        homeMapViewModel.bookmarkChanged.observe(viewLifecycleOwner, {
            val items = UserSharedPreferences.getBookmarkedCourse(owner)
            bookmarkViewGroup.findViewById<TextView>(R.id.bookmark_course_hintText).let { view ->
                if(items.size != 0) {
                    view.visibility = View.INVISIBLE
                } else {
                    view.visibility = View.VISIBLE
                }
            }
            mAdapter.setItems(UserSharedPreferences.getBookmarkedCourse(owner))
        })

        mAdapter.isBookmarkChanged.observe(viewLifecycleOwner, {
            homeMapViewModel.bookmarkChanged.value = true
        })

        return bookmarkViewGroup

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
            .replace(R.id.bookmark, detailFragment)
            .addToBackStack(MainActivity.CHALLENGE_TAG)
            .commit()

        mainViewModel.toolbarTitle.value = toolbarTitle
    }

}