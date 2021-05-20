package com.mapo.ddubuck.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mapo.ddubuck.R
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


        return bookmarkViewGroup

    }

}