package com.example.ddubuck.weather

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ddubuck.R
import com.google.gson.JsonObject
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        getCurrentWeather()

    }

    fun getCurrentWeather() {
        val tv: TextView = findViewById(R.id.tv)
        var res: Call<WeatherResponse> = WeatherClient
                .getInstance()
                .buildRetrofit()
                .getCurrentWeather("37.563598" ,"126.909227", getString(R.string.OPEN_WEATHER_MAP_KEY))

        res.enqueue(object: Callback<WeatherResponse> {

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("TAG", "Failure : ${t.message.toString()}")
            }

            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                val weatherResponse = response.body()
                var cTemp =  weatherResponse!!.main!!.temp - 273.15  //켈빈을 섭씨로 변환
                var minTemp = weatherResponse!!.main!!.temp_min - 273.15
                var maxTemp = weatherResponse!!.main!!.temp_max - 273.15
                val stringBuilder =
                        "지역: " + weatherResponse!!.sys!!.country + "\n" +
                                "현재기온: " + cTemp + "\n" +
                                "최저기온: " + minTemp + "\n" +
                                "최고기온: " + maxTemp + "\n" +
                                "풍속: " + weatherResponse!!.wind!!.speed+ "\n" +
                                "일출시간: " + weatherResponse!!.sys!!.sunrise + "\n" +
                                "일몰시간: " + weatherResponse!!.sys!!.sunset + "\n"+
                                "아이콘: " + weatherResponse!!.weather!!.get(0).icon + "\n"

                Log.d("TAG" , "Success :: $response")
                tv.text = stringBuilder

            }
        })
    }
}