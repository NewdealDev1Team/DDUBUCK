package com.example.ddubuck

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle

import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.example.ddubuck.sharedpref.UserSharedPreferences
import com.example.ddubuck.ui.home.bottomSheet.*
import com.example.ddubuck.ui.badge.BadgeFragment
import com.example.ddubuck.ui.challenge.ChallengeFragment
import com.example.ddubuck.ui.home.HomeFragment
import com.example.ddubuck.ui.home.HomeMapViewModel
import com.example.ddubuck.ui.mypage.MyPageFragment
import com.example.ddubuck.ui.mypage.mywalk.CaloriesFragment
import com.example.ddubuck.ui.mypage.mywalk.CoseClearFragment
import com.example.ddubuck.ui.mypage.mywalk.WalkTimeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment(this@MainActivity)
    private val challengeFragment = ChallengeFragment()
    private val badgeFragment = BadgeFragment()

    private val myPageFragment = MyPageFragment()
    private var walktimFm = WalkTimeFragment()
    private var coseClearFm = CoseClearFragment()
    private var caloriesfm = CaloriesFragment()


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
//                val fm:FragmentManager = supportFragmentManager
//                fm.beginTransaction().add(R.id.navigation_mypage,myPageFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
            else -> false
        }
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.e("정보 ", UserSharedPreferences.getUserId(this))

        initFragmentManager()
        initToolBar()
        initPermission()
//        initMyPageFragment()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            initVibrator()
        }
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

//    fun initMyPageFragment(){
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//    }
//    // fragmentA 에서 frameLayoutB에 fragment 추가하기 위해 호출 하는 메서드
//    fun openFragmentOnFrameLayoutB(int: Int){
//    val transaction = supportFragmentManager.beginTransaction()
//    when(int){
//        1 -> transaction.replace(R.id.navigation_mypage,walktimFm)
//        2 -> transaction.replace(R.id.navigation_mypage,coseClearFm)
//        3 -> transaction.replace(R.id.navigation_mypage,caloriesfm)
//    }
//        transaction.commit()
//    }

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
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(tb)
        val tbm = supportActionBar
        if(tbm != null) {
            tbm.setDisplayShowTitleEnabled(false)
            tbm.show()
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }
}