package com.example.ddubuck.ui.mypage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.ddubuck.MainActivity
import com.example.ddubuck.MainActivityViewModel
import com.example.ddubuck.R
import com.example.ddubuck.login.UserService
import com.example.ddubuck.login.UserValidationInfo
import com.example.ddubuck.sharedpref.UserSharedPreferences
import com.example.ddubuck.userinfo.UserBody
import com.example.ddubuck.userinfo.UserInfoService
import com.example.ddubuck.weather.WeatherViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_edit_userinfo.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyPageEditFragment : Fragment() {

    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private val mainViewModel: MainActivityViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainViewModel.toolbarTitle.value = "내 정보"

        val myPageEditView = inflater.inflate(R.layout.fragment_edit_userinfo, container, false)

//        val mypageProfileImage: ImageView = myPageEditView.findViewById(R.id.profile_image_edit)

        val mypageNickname: TextView = myPageEditView.findViewById(R.id.edit_info_name)

        val yearNow = SimpleDateFormat("yyyy", Locale("ko", "KR"))
        val yearNowString = yearNow.format(Date(System.currentTimeMillis()))

        val height: TextInputEditText = myPageEditView.findViewById(R.id.height_text_field_input)
        val weight: TextInputEditText = myPageEditView.findViewById(R.id.weight_text_field_input)

        val editButton: Button = myPageEditView.findViewById(R.id.mypage_edit_button)

        val petSwitcher: SwitchMaterial = myPageEditView.findViewById(R.id.pet_switch)

        editButton.isEnabled = false

        val years: ArrayList<String> = arrayListOf()
        val months: ArrayList<String> = arrayListOf()
        val days: ArrayList<String> = arrayListOf()



        for (i in 1900..yearNowString.toInt()) {
            years.add(i.toString())
        }

        for (j in 1..9) {
            months.add("0${j}")
        }

        for (j in 10..12) {
            months.add(j.toString())
        }

        for (k in 1..31) {
            days.add(k.toString())
        }

        val yearAutoCompleteTextView: AutoCompleteTextView =
            myPageEditView.findViewById(R.id.year_input)
        val monthAutoCompleteTextView: AutoCompleteTextView =
            myPageEditView.findViewById(R.id.month_input)
        val dayAutoCompleteTextView: AutoCompleteTextView =
            myPageEditView.findViewById(R.id.day_input)

        // Create adapter and add in AutoCompleteTextView
        val yearAdapter =
            ArrayAdapter(myPageEditView.context, android.R.layout.simple_list_item_1, years)
        val monthAdapter =
            ArrayAdapter(myPageEditView.context, android.R.layout.simple_list_item_1, months)
        val dayAdapter =
            ArrayAdapter(myPageEditView.context, android.R.layout.simple_list_item_1, days)

        yearAutoCompleteTextView.setAdapter(yearAdapter)
        monthAutoCompleteTextView.setAdapter(monthAdapter)
        dayAutoCompleteTextView.setAdapter(dayAdapter)

        setUserInfo(mypageNickname,
            yearAutoCompleteTextView,
            monthAutoCompleteTextView,
            dayAutoCompleteTextView,
            height,
            weight)
        recognizeChange(yearAutoCompleteTextView,
            monthAutoCompleteTextView,
            dayAutoCompleteTextView,
            height,
            weight)

        editButton.setOnClickListener {
            var birthday = if (monthAutoCompleteTextView.text.toString()
                    .toInt() <= 9 && monthAutoCompleteTextView.text.toString().length == 1
            ) {
                "${yearAutoCompleteTextView.text}-0${monthAutoCompleteTextView.text}-"
            } else {
                "${yearAutoCompleteTextView.text}-${monthAutoCompleteTextView.text}-"
            }

            birthday += if (dayAutoCompleteTextView.text.toString().toInt() < 10) {
                "0${dayAutoCompleteTextView.text.toString().toInt()}"
            } else {
                "${dayAutoCompleteTextView.text.toString().toInt()}"
            }

            saveHeightWeight(birthday,
                height.text.toString().toDouble(),
                weight.text.toString().toDouble())

            editButton.isEnabled = false
        }

        setPetInfo(petSwitcher)
        savePetInfo(petSwitcher)


        return myPageEditView
    }

    private fun setUserInfo(
        userName: TextView,
        year: AutoCompleteTextView,
        month: AutoCompleteTextView,
        day: AutoCompleteTextView,
        height: TextInputEditText,
        weight: TextInputEditText,
    ) {
        val userValidation: Retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.6.181:3000/get/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userValidationServer: UserService = userValidation.create(UserService::class.java)

        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            userValidationServer.getUserInfo(it).enqueue(object : Callback<UserValidationInfo> {
                override fun onResponse(
                    call: Call<UserValidationInfo>,
                    response: Response<UserValidationInfo>,
                ) {
                    val name = response.body()?.name

                    val yearGet = response.body()?.birth?.substring(0, 4)
                    val monthGet = response.body()?.birth?.substring(5, 7)
                    val dayGet = response.body()?.birth?.substring(8, 10)

                    val heightGet = response.body()?.height
                    val weightGet = response.body()?.weight

                    userName.text = name.toString()
                    year.setText(yearGet)
                    month.setText(monthGet)
                    day.setText(dayGet)
                    height.setText(heightGet.toString())
                    weight.setText(weightGet.toString())

                    mypage_edit_button.isEnabled = false
                }

                override fun onFailure(call: Call<UserValidationInfo>, t: Throwable) {
                    Log.e("Error", "user 정보 가져오기 실패")
                }
            })
        }
    }

    private fun recognizeChange(
        year: AutoCompleteTextView,
        month: AutoCompleteTextView,
        day: AutoCompleteTextView,
        height: TextInputEditText,
        weight: TextInputEditText,
    ) {
        val watcher = Editwatcher()
        year.addTextChangedListener(watcher)
        month.addTextChangedListener(watcher)
        day.addTextChangedListener(watcher)
        height.addTextChangedListener(watcher)
        weight.addTextChangedListener(watcher)

    }

    private fun saveHeightWeight(birth: String, height: Double, weight: Double) {

        val userInfo: Retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.6.181:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val userMoreServer: UserInfoService = userInfo.create(UserInfoService::class.java)

        context?.let { UserSharedPreferences.getUserId(it) }?.let {
            userMoreServer.saveUserBodyInfo(it, birth, height, weight)
                .enqueue(object : Callback<UserBody> {
                    override fun onResponse(call: Call<UserBody>, response: Response<UserBody>) {
                        Log.e("Success", response.message())
                    }

                    override fun onFailure(call: Call<UserBody>, t: Throwable) {
                        t.message?.let { Log.e("Fail", it) }
                    }
                })
        }

    }

    private fun savePetInfo(petSwitcher: SwitchMaterial) {
        petSwitcher.setOnCheckedChangeListener { _, isChecked ->
            context?.let { UserSharedPreferences.setPet(it, isChecked) }
        }
    }

    private fun setPetInfo(petSwitcher: SwitchMaterial) {
        petSwitcher.isChecked = context?.let { UserSharedPreferences.getPet(it) } == true
    }


    inner class Editwatcher : TextWatcher {
        // 문자열이 바뀌고 나서
        override fun afterTextChanged(p0: Editable?) {
            mypage_edit_button.isEnabled = true
        }

        // 문자열이 바뀌기 전에
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mypage_edit_button.isEnabled = false

        }

        // 문자열이 바뀌는 동시에 사용
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mypage_edit_button.isEnabled = true
        }

    }

}