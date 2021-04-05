package com.example.ddubuck

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ddubuck.ui.badge.BadgeFragment
import com.example.ddubuck.ui.challenge.ChallengeFragment
import com.example.ddubuck.ui.home.HomeFragment
import com.example.ddubuck.ui.mypage.MyPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView



class MainActivity : AppCompatActivity() {
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{ item ->
        when(item.itemId){
            R.id.navigation_home -> {
                println("home")
                replaceFragmemnt(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_challenge -> {
                println("challenge")
                replaceFragmemnt(ChallengeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_badge -> {
                println("home")
                replaceFragmemnt(BadgeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_mypage -> {
                println("home")
                replaceFragmemnt(MyPageFragment())
                return@OnNavigationItemSelectedListener true
            }
            else -> false
        }
     }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragmemnt(HomeFragment())
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //화면 상단 바
//        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        //화면 텍스트
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_challenge, R.id.navigation_badge,  R.id.navigation_mypage))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun replaceFragmemnt(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_container,fragment)
    }
}