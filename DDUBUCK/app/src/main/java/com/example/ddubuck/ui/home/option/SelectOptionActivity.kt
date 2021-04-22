package com.example.ddubuck.ui.home.option

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ddubuck.R
import com.example.ddubuck.data.publicdata.RetrofitClient
import com.example.ddubuck.data.publicdata.publicData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectOptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_option)

        RetrofitClient.instance.getRests().enqueue(object: Callback<publicData> {
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
                    Toast.makeText(this@SelectOptionActivity, it, Toast.LENGTH_SHORT).show()
                } ?: Toast.makeText(this@SelectOptionActivity, "요청 에러", Toast.LENGTH_SHORT).show()
            }

        })
    }
}