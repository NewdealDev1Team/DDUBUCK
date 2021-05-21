package com.mapo.ddubuck.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.home.CourseItem
import com.mapo.ddubuck.data.home.WalkRecord
import com.mapo.ddubuck.login.LoginActivity
import com.mapo.ddubuck.sharedpref.UserSharedPreferences

class BookmarkFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val bookmarkViewGroup: ViewGroup = inflater.inflate(R.layout.fragment_bookmark,
            container, false) as ViewGroup

        bookmarkViewGroup.findViewById<ViewPager2>(R.id.bookmark_course_viewpager).let { v->
            val adapter = BookmarkCourseAdapter(initArray, parentFragmentManager)
            v.adapter = adapter
        }

        return bookmarkViewGroup

    }

    companion object {
        private val initArray = arrayListOf(
            CourseItem(
                true,
                null,
                "",
                "",
                WalkRecord(listOf(), 0.0, 0.0, 1, 1, 1.0)
            ),
        )
    }

}