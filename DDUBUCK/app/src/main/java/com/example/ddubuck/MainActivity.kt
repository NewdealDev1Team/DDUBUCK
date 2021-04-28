package com.example.ddubuck

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle

import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.ddubuck.ui.home.bottomSheet.*
import com.example.ddubuck.ui.badge.BadgeFragment
import com.example.ddubuck.ui.challenge.ChallengeFragment
import com.example.ddubuck.ui.home.HomeFragment
import com.example.ddubuck.ui.home.HomeMapViewModel
import com.example.ddubuck.ui.mypage.MyPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment(this@MainActivity)
    private val challengeFragment = ChallengeFragment()
    private val badgeFragment = BadgeFragment()
    private val myPageFragment = MyPageFragment()
    private lateinit var activeFragment : Fragment
    private val mapModel: HomeMapViewModel by viewModels()
//수치
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{ item ->
        when(item.itemId){
            R.id.navigation_home -> {
                replaceFragment(homeFragment)
                changeToolBar(null)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_challenge -> {
                replaceFragment(challengeFragment)
                changeToolBar("챌린지")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_badge -> {
                replaceFragment(badgeFragment)
                changeToolBar("배지")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_mypage -> {
                replaceFragment(myPageFragment)
                changeToolBar("마이페이지")
                return@OnNavigationItemSelectedListener true
            }
            else -> false
        }
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragmentManager()
        initToolBar()
        initPermission()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            initVibrator()
        }
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun initVibrator() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        mapModel.vibrationControl.observe(this, {
            vibrator.vibrate(VibrationEffect.createOneShot(100,85))
        })
    }


    private fun initPermission() {
        val list = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.VIBRATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.VIBRATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        ActivityCompat.requestPermissions(this, list.toTypedArray(), 1000)
        //TODO 권한 거절 시 대응

    }


    private fun initFragmentManager() {
        val fm = supportFragmentManager
        fm.beginTransaction().apply {
            add(R.id.nav_main_container, homeFragment).show(homeFragment)
            add(R.id.nav_main_container, challengeFragment).hide(challengeFragment)
            add(R.id.nav_main_container, badgeFragment).hide(badgeFragment)
            add(R.id.nav_main_container, myPageFragment).hide(myPageFragment)
        }.commit()
        activeFragment = homeFragment
    }

    private fun initToolBar() {
        val fm = supportFragmentManager
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(tb)
        val tbm = supportActionBar
        if(tbm != null) {
            tbm.setDisplayShowTitleEnabled(false)
            tbm.show()
            fm.addOnBackStackChangedListener {
                if(fm.backStackEntryCount != 0) {
                    val backStackTag = when(activeFragment) {
                        challengeFragment -> CHALLENGE_BACK_STACK_TAG
                        badgeFragment -> BADGE_BACK_STACK_TAG
                        myPageFragment -> MYPAGE_BACK_STACK_TAG
                        else -> ""
                    }
                    if(backStackTag != "") {
                        tbm.setDisplayHomeAsUpEnabled(true)
                        tb.setNavigationOnClickListener {fm.popBackStack(backStackTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)}
                    }
                } else {
                    tbm.setDisplayHomeAsUpEnabled(false)
                }
            }
        }
    }

    private fun changeToolBar(title:String?) {
        val toolbarTextView : TextView = findViewById(R.id.main_toolbar_text)
        val tbm = supportActionBar
        if(tbm != null) {
            if(title != null) {
                tbm.setDisplayShowTitleEnabled(true)
                tbm.title = title
                toolbarTextView.text = ""
            } else {
                tbm.setDisplayShowTitleEnabled(false)
                toolbarTextView.text = "뚜벅뚜벅"
            }
            tbm.setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(activeFragment).show(fragment).commit()
        supportFragmentManager.popBackStackImmediate()
        activeFragment = fragment
    }

    companion object {
        const val HOME_BACK_STACK_TAG = "HOME"
        const val CHALLENGE_BACK_STACK_TAG = "CHALLENGE"
        const val BADGE_BACK_STACK_TAG = "BADGE"
        const val MYPAGE_BACK_STACK_TAG = "MYPAGE"
    }

}