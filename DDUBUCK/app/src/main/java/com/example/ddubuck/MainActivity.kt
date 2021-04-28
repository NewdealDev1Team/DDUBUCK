package com.example.ddubuck

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
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
                supportFragmentManager.popBackStack()
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
        supportFragmentManager.beginTransaction().apply {
            add(R.id.nav_main_container, homeFragment).show(homeFragment)
            add(R.id.nav_main_container, challengeFragment).hide(challengeFragment)
            add(R.id.nav_main_container, badgeFragment).hide(badgeFragment)
            add(R.id.nav_main_container, myPageFragment).hide(myPageFragment)
        }.commit()
        activeFragment = homeFragment
    }

    private fun initToolBar() {
        //TODO 재사용 가능하게 수정하기
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(tb)
        val tbm = supportActionBar
        if(tbm != null) {
            tbm.setDisplayShowTitleEnabled(false)
            tbm.show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }

//    fun onFragmentChaneged(index: Int){
//        if(index==0){
//            supportFragmentManager.beginTransaction().replace(R.id.navigation_mypage,myPageFragment).commit()
//        } else if(index==1){
//            supportFragmentManager.beginTransaction().replace(R.id.WalkTimeFragment,walk)
//        }
//    }

}