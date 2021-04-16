package com.example.ddubuck.weather

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ddubuck.R
import com.example.ddubuck.ui.home.HomeMapFragment
import com.example.ddubuck.ui.home.HomeMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.util.FusedLocationSource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface APICallback {
    fun onSuccess(
        weatherResponse: WeatherResponse,
        uvRays: UVRays,
        dust: Dust,
        weatherText: TextView,
        tempAndDust: TextView,
        weatherImage: ImageView
    )
}

class WeatherFragment : Fragment(), APICallback {

    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private val locationViewModel: HomeMapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val weatherView = inflater.inflate(R.layout.fragment_weather, container, false)
        val weatherText: TextView = weatherView.findViewById(R.id.weather_text)
        val tempAndDust: TextView = weatherView.findViewById(R.id.temp_and_dust)
        val weatherImage: ImageView = weatherView.findViewById(R.id.weather_image)

//        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        showWeatherInfo(this, weatherText, tempAndDust, weatherImage)

        return weatherView
    }

    // Coroutine 사용 필요
    private fun showWeatherInfo(
        result: APICallback,
        weatherText: TextView,
        tempAndDust: TextView,
        weatherImage: ImageView
    ) {

        locationViewModel.position.observe(viewLifecycleOwner, {location ->
            val lat = location.latitude.toString()
            val lon = location.longitude.toString()
            weatherViewModel.weatherInfo(lat,lon).observe(viewLifecycleOwner, { weather ->
                weatherViewModel.uvRaysInfo.observe(viewLifecycleOwner, { uvRays ->
                    weatherViewModel.dustInfo.observe(viewLifecycleOwner, { dust ->
                        result.onSuccess(weather, uvRays, dust, weatherText, tempAndDust, weatherImage)
                        weatherViewModel.getResponseValue(true)
                    })
                })
            })
        })

    }


    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onSuccess(
        weatherResponse: WeatherResponse,
        uvRays: UVRays,
        dust: Dust,
        weatherText: TextView,
        tempAndDust: TextView,
        weatherImage: ImageView
    ) {
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
        val discomfortIndex =
            (((9 / 5) * tempNowC!!) - (0.55 * (1 - (tempHumidity!! / 100).toInt()) * (9 / 5 * tempNowC - 26)) + 32).toInt()

        // 자외선 지수
        val uvRays = uvRays.response.body.items.item[0].today

        // 통합 대기 환경 지수
        val dustInfo = dust.response.body.items[0].khaiValue?.toInt()
        var dustString = ""

        when (weatherID) {
            in 900..909 -> weatherScore += 1
            in 200..699 -> weatherScore += 2
            in 800..809 -> weatherScore += 3
        }

        if (uvRays != null && uvRays.toString() != "") {
            if (uvRays.toInt() <= 6 && weatherID in (900..909)) weatherScore += 1
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
                weatherScore += 3
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
                weatherText.text = "산책하기 최고의 날!"
                val spanText = SpannableString("$tempMax°C/$tempMin°C    미세 $dustString")
                spanText.setSpan(
                    ForegroundColorSpan(Color.rgb(118, 118, 118)),
                    11,
                    15,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                tempAndDust.text = spanText
                weatherImage.setImageResource(R.drawable.weather_high)
            }
            in 6..9 -> {
                weatherText.text = "산책하기 좋아요!"
                val spanText = SpannableString("$tempMax°C/$tempMin°C    미세 $dustString")
                spanText.setSpan(
                    ForegroundColorSpan(Color.rgb(61, 171, 91)),
                    0,
                    spanText.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                spanText.setSpan(
                    ForegroundColorSpan(Color.rgb(118, 118, 118)),
                    11,
                    15,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                tempAndDust.text = spanText
                weatherImage.setImageResource(R.drawable.weather_middle)
            }
            in 3..5 -> {
                weatherText.text = "주의하며 산책해요."

                val spanText = SpannableString("$tempMax°C/$tempMin°C    미세 $dustString")
                spanText.setSpan(
                    ForegroundColorSpan(Color.rgb(255, 153, 0)),
                    0,
                    spanText.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )

                spanText.setSpan(
                    ForegroundColorSpan(Color.rgb(118, 118, 118)),
                    11,
                    15,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                tempAndDust.text = spanText

                weatherImage.setImageResource(R.drawable.weather_low)
            }

        }
    }


}