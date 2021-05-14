package com.mapo.ddubuck

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle

import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.TextView
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.mapo.ddubuck.home.bottomSheet.*
import com.mapo.ddubuck.badge.BadgeFragment
import com.mapo.ddubuck.challenge.ChallengeFragment
import com.mapo.ddubuck.home.HomeFragment
import com.mapo.ddubuck.home.HomeMapViewModel
import com.mapo.ddubuck.mypage.MyPageFragment
import com.mapo.ddubuck.mypage.SettingFragment

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapo.ddubuck.challenge.detail.ChallengeDetailFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_mypage.*

import kotlinx.android.synthetic.main.fragment_walk_time.*

@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment(this@MainActivity)
    private val challengeFragment = ChallengeFragment()
    private val badgeFragment = BadgeFragment()
    private val myPageFragment = MyPageFragment()
    private val settingFragment = SettingFragment()


    private lateinit var activeFragment: Fragment
    private val mapModel: HomeMapViewModel by viewModels()
    private val activityModel: MainActivityViewModel by viewModels()

    //수치
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
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

//        onCoachMark()

        Log.e("정보 ", UserSharedPreferences.getUserId(this))

        initToolBar()
        initFragmentManager()
        initPermission()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            initVibrator()
        }
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }


    fun onCoachMark() {
        val dialog : Dialog = Dialog(this,R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen)
        //최상의보기로 사용
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setContentView(R.layout.coach_mark) //drawable, coah_mark.xml - 레이아웃 리소스 확장
        dialog.setCanceledOnTouchOutside(true)


        //코치마크 어디든 터치 시 창이 닫힌다.
        val masterView : View = dialog.findViewById(R.id.coach_mark_master_view)//최상의 뷰
        masterView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view:View) {
                dialog.dismiss()
            }
        })
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initVibrator() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        mapModel.vibrationControl.observe(this, {
            vibrator.vibrate(VibrationEffect.createOneShot(100, 85))
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
    }


    private fun initFragmentManager() {
        val fm = supportFragmentManager
        fm.beginTransaction().apply {
            add(R.id.nav_main_container, homeFragment).show(homeFragment)
            add(R.id.nav_main_container, challengeFragment).hide(challengeFragment)
            add(R.id.nav_main_container, badgeFragment).hide(badgeFragment)
            add(R.id.nav_main_container, myPageFragment).hide(myPageFragment)
            add(R.id.nav_main_container, settingFragment).hide(settingFragment)
//            add(R.id.nav_main_container,walkTimeFragment).hide(walkTimeFragment)
//            add(R.id.nav_main_container,courseClearFragment).hide(courseClearFragment)
//            add(R.id.nav_main_container,caloriesFragment).hide(caloriesFragment)
        }.commit()
        activeFragment = homeFragment
        val tbm = supportActionBar
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        if (tbm != null) {
            fm.addOnBackStackChangedListener {
                if (fm.backStackEntryCount != 0) {
                    when (activeFragment) {
                        challengeFragment -> {
                            val backStackTag = CHALLENGE_TAG
                            tbm.setDisplayHomeAsUpEnabled(true)
                            tb.setNavigationOnClickListener {
                                fm.popBackStack(backStackTag,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            }
                        }
                        badgeFragment -> {
                            val backStackTag = BADGE_TAG
                            tbm.setDisplayHomeAsUpEnabled(true)
                            tb.setNavigationOnClickListener {
                                fm.popBackStack(backStackTag,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            }
                        }
                        myPageFragment -> {
                            val backStackTag = MYPAGE_TAG
                            tbm.setDisplayHomeAsUpEnabled(true)
                            tb.setNavigationOnClickListener {
                                fm.popBackStack(backStackTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                tbm.title = "마이페이지"
                            }
                        }
                        else -> {

                        }
                    }
                } else {
                    tbm.setDisplayHomeAsUpEnabled(false)
                    when (activeFragment) {
                        challengeFragment -> {
                            tbm.title = "챌린지"
                        }
                        badgeFragment -> {
                            tbm.title = "뱃지"
                        }
                        myPageFragment -> {
                            tbm.title = "마이페이지"
                            fm.beginTransaction()
                                .detach(activeFragment)
                                .attach(myPageFragment)
                                .commit()

                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun initToolBar() {
        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(tb)
        val tbm = supportActionBar
        if (tbm != null) {
            tbm.setDisplayShowTitleEnabled(false)
            tbm.show()
            activityModel.toolbarTitle.observe(this, { v ->
                tbm.title = v
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        when (activeFragment) {
            homeFragment -> {
                menuInflater.inflate(R.menu.toolbar_menu_home, menu)
            }
            myPageFragment -> {
                menuInflater.inflate(R.menu.toolbar_menu_mypage, menu)
            }
            else -> {

            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {

            }
            R.id.action_bookmark -> {

            }
            R.id.action_settings -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction
                    .hide(activeFragment)
                    .show(settingFragment)
                    .addToBackStack(MYPAGE_TAG).commit()

                activityModel.toolbarTitle.value = "설정"
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.popBackStackImmediate()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(activeFragment).show(fragment).commit()
        supportFragmentManager.popBackStackImmediate()
        activeFragment = fragment
        changeToolBar(fragment)
    }

    private fun changeToolBar(fragment: Fragment) {
        val toolbarTextView: TextView = findViewById(R.id.main_toolbar_text)
        val tbm = supportActionBar
        if (tbm != null) {
            when (fragment) {
                challengeFragment -> {
                    tbm.setDisplayShowTitleEnabled(true)
                    tbm.title = "챌린지"
                    toolbarTextView.text = ""
                    invalidateOptionsMenu()
                }
                badgeFragment -> {
                    tbm.setDisplayShowTitleEnabled(true)
                    tbm.title = "뱃지"
                    toolbarTextView.text = ""
                    invalidateOptionsMenu()
                }
                myPageFragment -> {
                    tbm.setDisplayShowTitleEnabled(true)
                    tbm.title = "마이페이지"
                    toolbarTextView.text = ""
                    invalidateOptionsMenu()
                }
                else -> {
                    tbm.setDisplayShowTitleEnabled(false)
                    toolbarTextView.text = "뚜벅뚜벅"
                    invalidateOptionsMenu()
                }
            }
            tbm.setDisplayHomeAsUpEnabled(false)
        }
    }

    companion object {
        const val HOME_TAG = "HOME"
        const val HOME_RESULT_TAG = "HOME_RESULT"
        const val CHALLENGE_TAG = "CHALLENGE"
        const val BADGE_TAG = "BADGE"
        const val MYPAGE_TAG = "MYPAGE"
    }
}