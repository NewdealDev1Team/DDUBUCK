package com.example.ddubuck.weather

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ddubuck.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class WeatherActivity : AppCompatActivity() {

    var walkString = WalkScore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        getCurrentWeather()
        getCurrentDust()
        getCurrentUVRays()
    }


    fun getCurrentWeather() {
        val tv: TextView = findViewById(R.id.tv)
        val res: Call<WeatherResponse> = WeatherClient
                .getInstance()
                .buildRetrofit()
                .getCurrentWeather("37.563598", "126.909227", getString(R.string.OPEN_WEATHER_MAP_KEY))

        res.enqueue(object : Callback<WeatherResponse> {

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("TAG", "Failure : ${t.message.toString()}")
            }

            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                val weatherResponse = response.body()
                val cTemp = (weatherResponse!!.main!!.temp - 273.15).roundToInt() //켈빈을 섭씨로 변환
                val minTemp = (weatherResponse.main!!.temp_min - 273.15).roundToInt()
                val maxTemp = (weatherResponse.main!!.temp_max - 273.15).roundToInt()
                val stringBuilder =
                        "현재기온: " + cTemp + "\n" +
                                "최저기온: " + minTemp + "\n" +
                                "최고기온: " + maxTemp + "\n" +
                                "날씨: " + weatherResponse!!.weather[0].id + "\n" +
                                "습도: " + weatherResponse.main!!.humidity + "\n"

                tv.text = stringBuilder
            }
        })
    }

    private fun getCurrentDust() {
        val tv2: TextView = findViewById(R.id.tv2)
        val res: Call<Dust> = DustClient
                .getInstance()
                .buildRetrofit()
                .getCurrentDust("마포구", "DAILY", 1, 1, "json", "WUS/HlSHsC8A/VZZlz1//4eSJiXcoh5gfR2EsoqdYGjhybgzun09KJKWZz+slJ85LzMZIIahT9UgeveNhce/yw==")

        res.enqueue(object : Callback<Dust> {

            override fun onFailure(call: Call<Dust>, t: Throwable) {
                Log.d("TAG", "Failure : ${t.message.toString()}")
            }

            override fun onResponse(call: Call<Dust>, response: Response<Dust>) {
                val dustResponse = response.body()?.response

                val dustInfo =
                        "통합대기환경수치 : " + dustResponse?.body!!.items[0].khaiValue + "\n" +
                                "통합대기환경지수 : " + dustResponse.body.items[0].khaiGrade

                Log.d("TAG", "Success :: $dustInfo")
                tv2.text = dustInfo
            }
        })

    }

    private fun getCurrentUVRays() {
        val tv3: TextView = findViewById(R.id.tv3)
        val res: Call<UVRays> = UVRaysClient
                .getInstance()
                .buildRetrofit()
                .getCurrentUVRays("WUS/HlSHsC8A/VZZlz1//4eSJiXcoh5gfR2EsoqdYGjhybgzun09KJKWZz+slJ85LzMZIIahT9UgeveNhce/yw==",
                         1, 1,  "JSON", "1100000000", "2021040812")

        res.enqueue(object : Callback<UVRays> {

            override fun onFailure(call: Call<UVRays>, t: Throwable) {
                Log.d("TAG", "Failure : ${t.message.toString()}")
            }

            override fun onResponse(call: Call<UVRays>, response: Response<UVRays>) {
                val dustResponse = response.body()?.response

                val uvInfo =
                        "UV : " + dustResponse?.body!!.items.item[0].today

                Log.d("TAG", "Success :: $uvInfo")
                tv3.text = uvInfo
            }
        })
    }

}