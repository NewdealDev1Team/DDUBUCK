package com.mapo.ddubuck.mypage

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.mapo.ddubuck.MainActivity
import com.mapo.ddubuck.MainActivityViewModel
import com.mapo.ddubuck.R
import com.mapo.ddubuck.data.home.CourseItem
import com.mapo.ddubuck.data.home.WalkRecord
import com.mapo.ddubuck.sharedpref.UserSharedPreferences

class BookmarkFragment(private val owner : Activity): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val bookmarkViewGroup: ViewGroup = inflater.inflate(R.layout.fragment_bookmark,
            container, false) as ViewGroup
        val adapter = BookmarkCourseAdapter(owner, initArray, parentFragmentManager)
        bookmarkViewGroup.findViewById<ViewPager2>(R.id.bookmark_course_viewpager).let { v->
            v.adapter = adapter
            UserSharedPreferences.getBookmarkedCourse(owner).let { data->
                if(data.size != 0) {
                    adapter.setItems(data)
                }
            }
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