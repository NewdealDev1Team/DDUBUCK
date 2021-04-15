package com.example.ddubuck

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.ddubuck.data.publicdata.RetrofitClient
import com.example.ddubuck.data.publicdata.publicData
import com.example.ddubuck.ui.home.bottomSheet.*
import com.example.ddubuck.ui.badge.BadgeFragment
import com.example.ddubuck.ui.challenge.ChallengeFragment
import com.example.ddubuck.ui.home.HomeFragment
import com.example.ddubuck.ui.mypage.MyPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : FragmentActivity() {
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

        RetrofitClient.instance.getRests().enqueue(object: Callback<publicData>{
            override fun onResponse(call: Call<publicData>, response: Response<publicData>) {
                 val result = response.body()
                result?.let {
                    for (cafe in it.cafe) {
                        println("카페 latitude: ${cafe.latitude} longitude: ${cafe.longitude}")
                    }
                    for(carFreeRoad in it.carFreeRoad){
                        println("차 없는 도로 latitude: ${carFreeRoad.latitude} longitude: ${carFreeRoad.longitude} ")
                    }
                    for(petCafe in it.petCafe){
                        println("애견카페 latitude: ${petCafe.latitude} longitude: ${petCafe.longitude} ")
                    }
                    for(petRestaurant in it.petRestaurant){
                        println("반려견과 함께 latitude: ${petRestaurant.latitude} longitude: ${petRestaurant.longitude} ")
                    }
                    for(publicToilet in it.publicToilet){
                        println("공공화장실 latitude: ${publicToilet.latitude} longitude: ${publicToilet.longitude} ")
                    }
                    for(restArea in it.restArea){
                        println("공공쉼터 latitude: ${restArea.latitude} longitude: ${restArea.longitude} ")
                    }
                }
            }

            override fun onFailure(call: Call<publicData>, t: Throwable) {
                t.message?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                } ?: Toast.makeText(this@MainActivity, "요청 에러", Toast.LENGTH_SHORT).show()
            }

        })
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

}