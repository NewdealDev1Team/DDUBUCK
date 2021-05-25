package com.mapo.ddubuck.mypage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.home.CourseItem
import com.mapo.ddubuck.data.home.WalkRecord
import com.mapo.ddubuck.home.HomeMapViewModel
import com.mapo.ddubuck.login.LoginActivity
import com.mapo.ddubuck.sharedpref.UserSharedPreferences

class BookmarkFragment(private val owner : Activity): Fragment() {
    private val homeMapViewModel : HomeMapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val bookmarkViewGroup: ViewGroup = inflater.inflate(R.layout.fragment_bookmark,
            container, false) as ViewGroup



        val initArray = UserSharedPreferences.getBookmarkedCourse(owner)
        bookmarkViewGroup.findViewById<TextView>(R.id.bookmark_course_hintText).let { it ->
            if(initArray.size != 0) {
                it.visibility = View.INVISIBLE
            } else {
                it.visibility = View.VISIBLE
            }
        }

        val mAdapter = BookmarkCourseAdapter(owner, initArray, parentFragmentManager)
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
}