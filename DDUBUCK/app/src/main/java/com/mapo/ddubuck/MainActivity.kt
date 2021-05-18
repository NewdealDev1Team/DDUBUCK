package com.mapo.ddubuck

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle

import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.TextView
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.observe
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.mapo.ddubuck.home.bottomSheet.*
import com.mapo.ddubuck.badge.BadgeFragment
import com.mapo.ddubuck.challenge.ChallengeFragment
import com.mapo.ddubuck.home.HomeFragment
import com.mapo.ddubuck.home.HomeMapViewModel
import com.mapo.ddubuck.mypage.MyPageFragment
import com.mapo.ddubuck.mypage.SettingFragment

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapo.ddubuck.home.FilterDrawer
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

    private val drawerFragment = FilterDrawer(this@MainActivity)

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

        Log.e("정보 ", UserSharedPreferences.getUserId(this))

        initToolBar()
        initFragmentManager()
        initPermission()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            initVibrator()
        }
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val isCoachMarkOn : Boolean = UserSharedPreferences.getCoachMarkExit(this)
        if (!isCoachMarkOn) {
            onCoachMark()
        }
        Log.d("coachmark","${isCoachMarkOn}")
    }


    fun onCoachMark() {
//            val dialog : Dialog = Dialog(this,R.style.mytheme)
////        val dialog : Dialog = Dialog(this,R.style.mytheme)
//
//        dialog.setContentView(R.layout.coach_mark)
//        //검정 배경과 같은 희미 경유 기본 대화 상
//        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
////        dialog.window.setBlurBehindRadius
//        dialog.setCanceledOnTouchOutside(true)
//
//
////        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
////        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
////        dialog.window?.setLayout(
////            WindowManager.LayoutParams.MATCH_PARENT,
////            WindowManager.LayoutParams.MATCH_PARENT
////        )
//            //코치마크 어디든 터치 시 창이 닫힌다.
//            val masterView : ImageButton = dialog.findViewById(R.id.coach_mark_exit_button)//최상의 뷰
//            masterView.setOnClickListener{
//                    UserSharedPreferences.setCoachMarkExit(this,true)
//                    dialog.dismiss()
//            }
//            dialog.show()


        val dialog : Dialog = Dialog(this,R.style.mytheme)
//        val dialog : Dialog = Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
//        val dialog : Dialog = Dialog(this,R.style.mytheme)
        dialog.setContentView(R.layout.coach_mark)
        dialog.setCanceledOnTouchOutside(true)

        //코치마크 어디든 터치 시 창이 닫힌다.
        val masterView : ImageButton = dialog.findViewById(R.id.coach_mark_exit_button)//최상의 뷰
        masterView.setOnClickListener{
            UserSharedPreferences.setCoachMarkExit(this,true)
            dialog.dismiss()
        }
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
            add(R.id.main_drawer_frame, drawerFragment)
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
                                fm.popBackStack(backStackTag,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
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
            val drawerLayout = findViewById<DrawerLayout>(R.id.main_drawerLayout)
            activityModel.showDrawer.observe(this, {v ->
                if(!v) {
                    drawerLayout.closeDrawer(GravityCompat.END)
                }
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
                val drawerLayout = findViewById<DrawerLayout>(R.id.main_drawerLayout)
                drawerLayout.openDrawer(Gravity.RIGHT)
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