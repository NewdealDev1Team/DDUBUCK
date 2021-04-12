package com.example.ddubuck.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ddubuck.R

interface APICallback {
    fun onSuccess(weatherResponse: WeatherResponse, uvRays: UVRays, dust: Dust, textView: TextView)
}

class WeatherActivity : Fragment(), APICallback {

    lateinit var weatherViewModel: WeatherViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val weatherView = inflater.inflate(R.layout.fragment_weather, container, false)
        val tv: TextView = weatherView.findViewById(R.id.tv)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        showWeatherInfo(this, tv)

        return weatherView
    }

    private fun showWeatherInfo(result: APICallback, textView: TextView){
        weatherViewModel.weatherInfo.observe(viewLifecycleOwner, { weather ->
            weatherViewModel.uvRaysInfo.observe(viewLifecycleOwner, { uvRays ->
                weatherViewModel.dustInfo.observe(viewLifecycleOwner, { dust ->
                    result.onSuccess(weather, uvRays, dust, textView)
                })
            })
        })
    }


    @SuppressLint("SetTextI18n")
    override fun onSuccess(weatherResponse: WeatherResponse, uvRays: UVRays, dust: Dust, textView: TextView) {
        var weatherScore = 0

        // 현재 날씨 ID
        val weatherID = weatherResponse.weather[0].id

        // 현재 온도
        val tempNowF = weatherResponse.main?.temp?.toInt()
        val tempNowC = tempNowF?.minus(273)

        // 최고 온도 & 최저 온도
        val tempMax = weatherResponse.main?.temp_max?.toInt()?.minus(273)
        val tempMin = weatherResponse.main?.temp_min?.toInt()?.minus(273)

        // 현재 습도
        val tempHumidity = weatherResponse.main?.humidity

        // 불쾌 지수 공식
        val discomfortIndex = (((9 / 5) * tempNowC!!) - (0.55 * (1 - (tempHumidity!! / 100).toInt()) * (9 / 5 * tempNowC - 26)) + 32).toInt()

        // 자외선 지수
        val uvRays = uvRays.response.body.items.item[0].today?.toInt()

        // 통합 대기 환경 지수
        val dustInfo = dust.response.body.items[0].khaiValue?.toInt()
        var dustString = ""

        when (weatherID) {
            in 900..909 -> weatherScore += 1
            in 200..699 -> weatherScore += 2
            in 800..809 -> weatherScore += 3
        }

        if (uvRays != null) {
            if (uvRays <= 6 && weatherID in (900..909)) weatherScore += 1
        }

        weatherScore += when {
            discomfortIndex > 80 -> {
                1
            }
            discomfortIndex in 76..80 -> {
                2
            }
            discomfortIndex in 68..75 -> {
                3
            }
            else -> {
                4
            }
        }

        when (dustInfo) {
            in 0..50 -> {
                weatherScore += 4
                dustString = "좋음"
            }
            in 51..101 -> {
                weatherScore +=  3
                dustString = "보통"
            }
            in 101..250 -> {
                weatherScore += 2
                dustString = "나쁨"
            }
            in 251..500 -> {
                weatherScore += 1
                dustString = "아주 나쁨"
            }
        }

        when (weatherScore) {
            in 10..12 -> {
                textView.text = "산책 지수 : 산책하기 최고의 날 \n 최고 온도 : $tempMax \n 최저온도 : $tempMin \n 미세 : $dustString"
            }
            in 6..9 -> {
                textView.text = "산책 지수 : 산책하기 보통의 날 \n" +
                        " 최고 온도 : $tempMax \n" +
                        " 최저온도 : $tempMin \n" +
                        " 미세 : $dustString"
            }
            in 3..5 -> {
                textView.text = "산책 지수 : 산책하지마 \n" +
                        " 최고 온도 : $tempMax \n" +
                        " 최저온도 : $tempMin \n" +
                        " 미세 : $dustString"
            }
        }
    }


}