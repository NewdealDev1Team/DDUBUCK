package com.example.ddubuck.ui.mypage//package com.example.ddubuck.ui.mypage
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.annotation.BinderThread
//import androidx.recyclerview.widget.RecyclerView
//import com.example.ddubuck.R
//import com.example.ddubuck.data.home.WalkRecord
//
//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [MPChartFragment.newInstance] factory method to
// * create an instance of this fragment.
// */
////일주일 단위의 걸었던 시간
//class MPChartFragment : Fragment() {
//
//
//    internal lateinit var rvGraph:RecyclerView
//    internal lateinit var graphAdapter:GraphAdapter
//
//    private val WalkRecord : ArrayList<WalkRecord> = arrayListOf()
//    private val week = 7    //가로 : 7일
//    private val hour = 24   //세로 : 24시간
//    private val lineWidth = 30      //line크기 30px
//
//    override protected val layoutRes: Int{
//        return R.layout.fragment_m_p_chart
//    }
//
//
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        arguments?.let {
////            param1 = it.getString(ARG_PARAM1)
////            param2 = it.getString(ARG_PARAM2)
////        }
////    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_m_p_chart, container, false)
//    }
//
//    companion object {
//        fun newInstance(param1: String, param2: String) =
//            MPChartFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}