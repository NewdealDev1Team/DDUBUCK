package com.example.ddubuck.weather

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ddubuck.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        //getCurrentWeather()
        getCurrentDust()
    }

    fun getCurrentWeather() {
        val tv: TextView = findViewById(R.id.tv)
        val res: Call<WeatherResponse> = WeatherClient
                .getInstance()
                .buildRetrofit()
                .getCurrentWeather("37.563598" ,"126.909227", getString(R.string.OPEN_WEATHER_MAP_KEY))

        res.enqueue(object: Callback<WeatherResponse> {

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("TAG", "Failure : ${t.message.toString()}")
            }

            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                val weatherResponse = response.body()
                val cTemp =  weatherResponse!!.main!!.temp - 273.15  //켈빈을 섭씨로 변환
                val minTemp = weatherResponse.main!!.temp_min - 273.15
                val maxTemp = weatherResponse.main!!.temp_max - 273.15
                val stringBuilder =
                        "지역: " + weatherResponse.sys!!.country + "\n" +
                                "현재기온: " + cTemp + "\n" +
                                "최저기온: " + minTemp + "\n" +
                                "최고기온: " + maxTemp + "\n" +
                                "풍속: " + weatherResponse.wind!!.speed+ "\n" +
                                "일출시간: " + weatherResponse.sys!!.sunrise + "\n" +
                                "일몰시간: " + weatherResponse.sys!!.sunset + "\n"+
                                "아이콘: " + weatherResponse.weather.get(0).icon + "\n"

                tv.text = stringBuilder
            }
        })
    }

    fun getCurrentDust() {
        val tv: TextView = findViewById(R.id.tv)
        val res: Call<Dust> = DustClient
                .getInstance()
                .buildRetrofit()
                .getCurrentDust("마포구", "DAILY", 1, 1, "json"
                        , "WUS/HlSHsC8A/VZZlz1//4eSJiXcoh5gfR2EsoqdYGjhybgzun09KJKWZz+slJ85LzMZIIahT9UgeveNhce/yw==")


        res.enqueue(object: Callback<Dust> {

            override fun onFailure(call: Call<Dust>, t: Throwable) {
                Log.d("TAG", "Failure : ${t.message.toString()}")
            }

            override fun onResponse(call: Call<Dust>, response: Response<Dust>) {
                val dustResponse = response.body()?.response

                var dustInfo =
                        "통합대기환경수치 : " + dustResponse?.body!!.items[0].khaiValue + "\n" +
                                "통합대기환경지수 : " + dustResponse.body.items[0].khaiGrade

                Log.d("TAG" , "Success :: ${dustInfo}")
                tv.text = dustInfo
            }
        })


    }
}