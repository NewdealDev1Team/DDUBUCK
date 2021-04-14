package com.example.ddubuck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ddubuck.home.CourseItem
import com.example.ddubuck.home.bottomSheet.*
import com.example.ddubuck.ui.badge.BadgeFragment
import com.example.ddubuck.ui.challenge.ChallengeFragment
import com.example.ddubuck.ui.home.HomeFragment
import com.example.ddubuck.ui.mypage.MyPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : FragmentActivity(),
        BottomSheetSelectFragment.OnCourseSelectedListener,
        BottomSheetFreeDetailFragment.OnFreeStartClickedListener,
        BottomSheetCourseDetailFragment.OnCourseStartClickedListener{
    private val homeFragment = HomeFragment(this@MainActivity)
    private val challengeFragment = ChallengeFragment()
    private val badgeFragment = BadgeFragment()
    private val myPageFragment = MyPageFragment()
    private lateinit var activeFragment : Fragment

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{ item ->
        when(item.itemId){
            R.id.navigation_home -> {
                replaceFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_challenge -> {
                replaceFragment(challengeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_badge -> {
                replaceFragment(badgeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_mypage -> {
                replaceFragment(myPageFragment)
                return@OnNavigationItemSelectedListener true
            }
            else -> false
        }
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragmentManager()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //화면 상단 바
//        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        //화면 텍스트
        //val navController = findNavController(R.id.nav_main_container)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //val appBarConfiguration = AppBarConfiguration(setOf( R.id.navigation_home, R.id.navigation_challenge, R.id.navigation_badge,  R.id.navigation_mypage))
        //setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)
    }

    private fun initFragmentManager() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.nav_main_container, homeFragment).show(homeFragment)
            add(R.id.nav_main_container, challengeFragment).hide(challengeFragment)
            add(R.id.nav_main_container, badgeFragment).hide(badgeFragment)
            add(R.id.nav_main_container, myPageFragment).hide(myPageFragment)
        }.commit()
        activeFragment = homeFragment
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when(fragment) {
            is BottomSheetSelectFragment -> fragment.setOnCourseSelectedListener(this)
            is BottomSheetFreeDetailFragment -> fragment.setOnFreeStartClickedListener(this)
            is BottomSheetCourseDetailFragment -> fragment.setOnCourseStartClickedListener(this)
        }
    }

    //코스 선택 시 (자유산책/코스산책)
    override fun onCourseSelected(courseItem: CourseItem) {
        val fm = supportFragmentManager
        if(courseItem.isFreeWalk) {
            val frag = BottomSheetFreeDetailFragment()
            val fmTransaction = fm.beginTransaction()
            fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            fmTransaction.replace(R.id.bottom_sheet_container,frag, BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(DETAIL_PAGE_FRAG).commit()
        } else {
            val frag = BottomSheetCourseDetailFragment(courseItem)
            val fmTransaction = fm.beginTransaction()
            fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            fmTransaction.replace(R.id.bottom_sheet_container,frag, BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(DETAIL_PAGE_FRAG).commit()
        }
    }

    //시작버튼 선택 시 (자유산책)
    override fun onFreeStartClicked() {
        val fm = supportFragmentManager
        fm.popBackStack(DETAIL_PAGE_FRAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val frag = BottomSheetFreeProgressFragment()
        val fmTransaction = fm.beginTransaction()
        fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
        fmTransaction.replace(R.id.bottom_sheet_container, frag, BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(null).commit()
    }

    //시작버튼 선택 시 (코스산책)
    override fun onCourseStartClicked() {
        val fm = supportFragmentManager
        fm.popBackStack(DETAIL_PAGE_FRAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val frag = BottomSheetCourseProgressFragment()
        val fmTransaction = fm.beginTransaction()
        fmTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
        fmTransaction.replace(R.id.bottom_sheet_container, frag, BOTTOM_SHEET_CONTAINER_TAG).addToBackStack(null).commit()
    }

    companion object {
        private const val BOTTOM_SHEET_CONTAINER_TAG = "BOTTOM_SHEET_CONTAINER"
        private const val DETAIL_PAGE_FRAG = "DETAIL_PAGE_FRAG"
    }
}