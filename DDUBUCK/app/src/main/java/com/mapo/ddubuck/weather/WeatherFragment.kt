package com.mapo.ddubuck.weather

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mapo.ddubuck.R
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.mapo.ddubuck.home.HomeMapViewModel

interface WeatherAPICallback {
    fun onSuccess(
            weatherResponse: WeatherResponse,
            uvRays: UVRays,
            dust: Dust,
            weatherText: TextView,
            tempAndDust: TextView,
            weatherImage: ImageView
    )
}

class WeatherFragment : Fragment(), WeatherAPICallback {

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

        showWeatherInfo(this, weatherText, tempAndDust, weatherImage)
        context?.let { UserSharedPreferences.getPet(it) }?.let { weatherViewModel.setPetValue(it) }

        return weatherView
    }

    // Coroutine 사용 필요
    private fun showWeatherInfo(
            result: WeatherAPICallback,
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
        val uvRays = if (uvRays.response.header.resultCode != "00") {
            "-1"
        } else {
            uvRays.response.body.items.item[0].today
        }

        // 통합 대기 환경 지수
        val dustInfo = if (dust.response.header.resultCode != "00") {
            "-1"
        } else {
            dust.response.body.items[0].khaiValue.toString()
        }


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


        if (dustInfo != "null" && dustInfo != "-") {
            when (dustInfo!!.toInt()) {
                in 0..50 -> {
                    weatherScore += 4
                    dustString = "좋음"
                }
                in 51..101 -> {
                    weatherScore += 3
                    dustString = "보통"
                }
                in 101..250 -> {
                    weatherScore -= 3
                    dustString = "나쁨"
                }
                in 251..500 -> {
                    weatherScore -= 4
                    dustString = "아주 나쁨"
                }
                else -> {
                    dustString = "점검중"
                }

            }

        } else {
            dustString += "점검중"
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

                weatherViewModel.isPetYes.observe(viewLifecycleOwner, { pet ->
                    if (pet) weatherImage.setImageResource(R.drawable.ic_weather_high_pet)
                    else weatherImage.setImageResource(R.drawable.ic_weather_high)
                })

            }
            in 8..9 -> {
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

                weatherViewModel.isPetYes.observe(viewLifecycleOwner, { pet ->
                    if (pet) weatherImage.setImageResource(R.drawable.ic_weather_middle_pet)
                    else weatherImage.setImageResource(R.drawable.ic_weather_middle)
                })


            }
            else -> {
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

                weatherViewModel.isPetYes.observe(viewLifecycleOwner, { pet ->
                    if (pet) weatherImage.setImageResource(R.drawable.ic_weather_low_pet)
                    else weatherImage.setImageResource(R.drawable.ic_weather_low)
                })
            }


        }
    }


}