package com.example.ddubuck.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.weather.Main

class MyPageFragment : Fragment() {

    private lateinit var myPageViewModel: MyPageViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        myPageViewModel =
                ViewModelProvider(this).get(MyPageViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_mypage, container, false)

        val rootView : ViewGroup = inflater.inflate(R.layout.fragment_mypage,container,false) as ViewGroup
        //하나의 인플레이터를 참조변수에 넣어준 후
//        val view = inflater.inflate(R.layout.fragment_walk_time,null) //fragment로 불러올 xml파일을 view로 가져온다.
        val timebutton: Button = rootView.findViewById<Button?>(R.id.button_worktime)//click시 fragment를 전환할 event를 발생시킬 버튼을 정의한다.

        timebutton.setOnClickListener {
            fun onClick(v:View){
//                val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
//                transaction.replace(R.id.nav_main_container,WalkTimeFragment).commit()
//                (activity as MainActivity).replaceFragment(WalkTimeFragment)
            }
        }

        return rootView
    }

}