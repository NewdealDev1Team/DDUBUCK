package com.mapo.ddubuck

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.observe
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapo.ddubuck.badge.BadgeFragment
import com.mapo.ddubuck.challenge.ChallengeFragment
import com.mapo.ddubuck.home.FilterDrawer
import com.mapo.ddubuck.home.HomeFragment
import com.mapo.ddubuck.home.HomeMapViewModel
import com.mapo.ddubuck.home.bottomSheet.*
import com.mapo.ddubuck.mypage.BookmarkFragment
import com.mapo.ddubuck.mypage.MyPageFragment
import com.mapo.ddubuck.mypage.MypageViewModel
import com.mapo.ddubuck.mypage.SettingFragment
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.coach_mark.view.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.fragment_walk_time.*
import java.util.*


@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment(this@MainActivity)
    private val challengeFragment = ChallengeFragment(this)
    private val badgeFragment = BadgeFragment()
    private val myPageFragment = MyPageFragment()
    private val settingFragment = SettingFragment()
    private val bookmarkFragment = BookmarkFragment(this@MainActivity)

    private val drawerFragment = FilterDrawer(this@MainActivity)

    private lateinit var activeFragment: Fragment
    private var isMyPageFragmentShown : Boolean = false
    private val mapModel: HomeMapViewModel by viewModels()
    private val activityModel: MainActivityViewModel by viewModels()
    private val myPageViewModel: MypageViewModel by viewModels()

    //??????
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
                    myPageViewModel.isRouteChanged.value = true
                    myPageViewModel.isImageUpdate.value = true
                    replaceFragment(myPageFragment)
                    return@OnNavigationItemSelectedListener true
                }
                else -> false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Log.e("?????? ", UserSharedPreferences.getUserId(this))

        initToolBar()
        initFragmentManager()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initVibrator()
        }
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        val isCoachMarkOn : Boolean = UserSharedPreferences.getCoachMarkExit(this)
        if (!isCoachMarkOn) {
            onCoachMark()
        }
    }

    private fun onCoachMark(){
        val dialog : Dialog = Dialog(this,R.style.WalkthroughTheme)


        dialog.setContentView(R.layout.coach_mark)
        dialog.setCanceledOnTouchOutside(true)

        //???????????? ????????? ?????? ??? ?????? ?????????.
        val masterView : ImageButton = dialog.findViewById(R.id.coach_mark_exit_button)
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


    private fun initFragmentManager() {
        val fm = supportFragmentManager
        fm.beginTransaction().apply {
            add(R.id.nav_main_container, homeFragment).show(homeFragment)
            add(R.id.nav_main_container, challengeFragment).hide(challengeFragment)
            add(R.id.nav_main_container, badgeFragment).hide(badgeFragment)
            add(R.id.nav_main_container, myPageFragment).hide(myPageFragment)
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
                            }
                            tbm.title = "???????????????"
                            isMyPageFragmentShown = false
                            invalidateOptionsMenu()
                        }
                        else -> {

                        }
                    }
                } else {
                    tbm.setDisplayHomeAsUpEnabled(false)
                    when (activeFragment) {
                        challengeFragment -> {
                            tbm.title = "?????????"
                            fm.beginTransaction()
                                .detach(activeFragment)
                                .attach(challengeFragment)
                                .commit()
                        }
                        badgeFragment -> {
                            tbm.title = "??????"
                        }
                        myPageFragment -> {
                            tbm.title = "???????????????"
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
                isMyPageFragmentShown = true
                invalidateOptionsMenu()
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
                if(isMyPageFragmentShown) {
                    menuInflater.inflate(R.menu.toolbar_menu_empty, menu)
                } else {
                    menuInflater.inflate(R.menu.toolbar_menu_mypage, menu)
                }
            }
            else -> {

            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        when (item.itemId) {
            R.id.action_filter -> {
                val drawerLayout = findViewById<DrawerLayout>(R.id.main_drawerLayout)
                drawerLayout.openDrawer(Gravity.RIGHT)
            }
            R.id.action_bookmark -> {
                supportFragmentManager.popBackStackImmediate()
                fragmentTransaction
                    .replace(R.id.scrollview_mypage, bookmarkFragment)
                    .addToBackStack(MYPAGE_TAG).commit()
                isMyPageFragmentShown = true
                activityModel.toolbarTitle.value = "?????????"
                invalidateOptionsMenu()
            }
            R.id.action_settings -> {
                supportFragmentManager.popBackStackImmediate()
                fragmentTransaction
                    .replace(R.id.scrollview_mypage, settingFragment)
                    .addToBackStack(MYPAGE_TAG).commit()
                isMyPageFragmentShown = true
                activityModel.toolbarTitle.value = "??????"
                invalidateOptionsMenu()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(activeFragment).show(fragment).commit()
        supportFragmentManager.popBackStackImmediate()
        activeFragment = fragment
        isMyPageFragmentShown = false
        changeToolBar(fragment)
    }

    private fun changeToolBar(fragment: Fragment) {
        val toolbarTextView: TextView = findViewById(R.id.main_toolbar_text)
        val drawerLayout : DrawerLayout = findViewById(R.id.main_drawerLayout)
        val tbm = supportActionBar
        if (tbm != null) {
            when (fragment) {
                challengeFragment -> {
                    tbm.setDisplayShowTitleEnabled(true)
                    tbm.title = "?????????"
                    toolbarTextView.text = ""
                    invalidateOptionsMenu()
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                badgeFragment -> {
                    tbm.setDisplayShowTitleEnabled(true)
                    tbm.title = "??????"
                    toolbarTextView.text = ""
                    invalidateOptionsMenu()
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                myPageFragment -> {
                    tbm.setDisplayShowTitleEnabled(true)
                    tbm.title = "???????????????"
                    toolbarTextView.text = ""
                    invalidateOptionsMenu()
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                else -> {
                    tbm.setDisplayShowTitleEnabled(false)
                    toolbarTextView.text = "????????????"
                    invalidateOptionsMenu()
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
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

