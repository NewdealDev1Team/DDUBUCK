package com.example.ddubuck.ui.mypage

import android.app.Activity
import android.content.Context
import android.app.ActionBar
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
import com.example.ddubuck.databinding.FragmentMypageBinding
import com.example.ddubuck.weather.Main
import kotlinx.android.synthetic.main.fragment_mypage.*


class MyPageFragment : Fragment() {

//     var walktimFm = WalkTimeFragment()
//     var coseClearFm = CoseClearFragment()
//     var caloriesfm = CaloriesFragment()

    private lateinit var myPageViewModel: MyPageViewModel
    private lateinit var myPageBinding: FragmentMypageBinding

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


    ): View? {
        myPageViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_mypage, container, false)
        val rootView : ViewGroup = inflater.inflate(R.layout.fragment_mypage,container,false) as ViewGroup


//        //하나의 인플레이터를 참조변수에 넣어준 후
////        val view = inflater.inflate(R.layout.fragment_walk_time,null) //fragment로 불러올 xml파일을 view로 가져온다.
////        val timebutton: Button = rootView.findViewById<Button?>(R.id.button_worktime)//click시 fragment를 전환할 event를 발생시킬 버튼을 정의한다.
//
////        timebutton.setOnClickListener {
//            fun onClick(v:View){
////                val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
////                transaction.replace(R.id.nav_main_container,WalkTimeFragment).commit()
////                (activity as MainActivity).replaceFragment(WalkTimeFragment)
//            }
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