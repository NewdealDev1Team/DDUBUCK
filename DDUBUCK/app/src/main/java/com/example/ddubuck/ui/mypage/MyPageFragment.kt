package com.example.ddubuck.ui.mypage

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.ui.home.HomeFragment
import com.example.ddubuck.ui.mypage.mywalk.CaloriesFragment
import com.example.ddubuck.ui.mypage.mywalk.CoseClearFragment
import com.example.ddubuck.ui.mypage.mywalk.WalkTimeFragment
import com.example.ddubuck.weather.Main
import kotlinx.android.synthetic.main.fragment_mypage.*


class MyPageFragment : Fragment() {

//     var walktimFm = WalkTimeFragment()
//     var coseClearFm = CoseClearFragment()
//     var caloriesfm = CaloriesFragment()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_mypage, container, false)
            //버튼
            val walkButton : Button = rootView.findViewById(R.id.button_worktime)

            walkButton.setOnClickListener{
                val d = Log.d("success","버튼클릭?")

                val fm = parentFragmentManager
                val walkTimeFM = WalkTimeFragment()
                val fmTransaction = fm.beginTransaction()
//                fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit)
                fmTransaction.replace(R.id.navigation_mypage,walkTimeFM).commit()
            }
            return rootView
        }
}

//        override fun onActivityCreated(savedInstanceState: Bundle?) {
//            super.onActivityCreated(savedInstanceState)
//        }

// frontFragment로 전환하는 함수
//        fun front(){
//            childFragmentManager.beginTransaction()
//                .replace(R.id.fragemnt, frontFragment)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit()
//        }
//
//        // backFragment로 전환하는 함수
//        fun back(){
//            childFragmentManager.beginTransaction()
//                .replace(R.id.fragment, backFragment)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit()
//        }

//class MyPageFragment : Fragment() {
//
//    private var _binding : FragmentMypageBinding? = null
//    private val binding get() = _binding!!
//    var mainActivity: MainActivity? = null
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mainActivity = context as MainActivity
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, //뷰를 그려주는 역할
//        container: ViewGroup?,    //부모뷰
//        savedInstanceState: Bundle?
//    ): View? {
////        myPageViewModel = iewModelProvider(this).get(MyPageViewModel::class.java)
//        _binding = FragmentMypageBinding.inflate(inflater,container,false)
//
//        binding.buttonWorktime.setOnClickListener{
//            mainActivity!!.openFragmentOnFrameLayoutB(1)
//        }
//
//        binding.buttonCalories.setOnClickListener{
//            mainActivity!!.openFragmentOnFrameLayoutB(2)
//        }
//
//        binding.buttonCalories.setOnClickListener{
//            mainActivity!!.openFragmentOnFrameLayoutB(3)
//        }
//        return binding.root
//    }
//}

//Log.d("life_cycle","onCreateView")
//val rootView = inflater.inflate(R.layout.fragment_mypage, container, false) //어떤 뷰를 그려주 역할인지
//val timeButton : Button = rootView.findViewById(R.id.button_worktime)
//
//timeButton.setOnClickListener{
//    val fm = parentFragmentManager
//    val fmTransaction = fm.beginTransaction()
//    val walktime = WalkTimeFragment()
//
//    fmTransaction.replace(R.id.navigation_mypage, walktime)
//    fmTransaction.commit()
//}
//return rootView
//자식뷰로서 fragment에 들러붙는 것!!