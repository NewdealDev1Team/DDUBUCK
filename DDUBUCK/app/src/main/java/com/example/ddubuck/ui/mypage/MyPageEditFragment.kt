package com.example.ddubuck.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ddubuck.R

class MyPageEditFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myPageEditView = inflater.inflate(R.layout.fragment_edit_userinfo, container, false)

        Log.e("부모가 누구니", childFragmentManager.toString())

        return myPageEditView
    }
}