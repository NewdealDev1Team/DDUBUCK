package com.mapo.ddubuck.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.mapo.ddubuck.MainActivityViewModel
import com.mapo.ddubuck.R
import com.mapo.ddubuck.login.UserService
import com.mapo.ddubuck.login.UserValidationInfo
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.mapo.ddubuck.userinfo.UserBody
import com.mapo.ddubuck.userinfo.UserInfoService
import com.mapo.ddubuck.weather.WeatherViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.mapo.ddubuck.userinfo.NextTimeDialog
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_edit_userinfo.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyPageEditFragment : Fragment() {

    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private lateinit var profileImageViewModel: ProfileImageViewModel
    private var selectedImage: Uri? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainViewModel.toolbarTitle.value = "??? ??????"
        profileImageViewModel = ProfileImageViewModel()

        val myPageEditView = inflater.inflate(R.layout.fragment_edit_userinfo, container, false)
        val myPageView = inflater.inflate(R.layout.fragment_mypage, container, false)

        val mypageEditProfileImage: CircleImageView = myPageEditView.findViewById(R.id.profile_image_edit)
        val mypageProfileImage : CircleImageView = myPageView.findViewById(R.id.profile_image)

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
            mypageEditProfileImage,
            yearAutoCompleteTextView,
            monthAutoCompleteTextView,
            dayAutoCompleteTextView,
            height,
            weight)

        recognizeChange( yearAutoCompleteTextView,
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

            val dialog = NextTimeDialog("?????? ??????",
                "??? ????????? ?????????????????????.",
                context as Activity)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val okButton: TextView = dialog.findViewById(R.id.dialog_ok_button)
            okButton.setOnClickListener {
                dialog.dismiss()
            }

            val cancelButton: TextView = dialog.findViewById(R.id.dialog_cancel_button)
            cancelButton.visibility = View.INVISIBLE

            editButton.isEnabled = false
        }

        setPetInfo(petSwitcher)
        savePetInfo(petSwitcher)

        mypageEditProfileImage.setOnClickListener {
            openImageChooser()
        }


        return myPageEditView
    }

    private fun setUserInfo(
        userName: TextView,
        mypageEditProfileImage: CircleImageView,
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

                    val profileImageUrl = response.body()?.picture

                    val yearGet = response.body()?.birth?.substring(0, 4)
                    val monthGet = response.body()?.birth?.substring(5, 7)
                    val dayGet = response.body()?.birth?.substring(8, 10)

                    val heightGet = response.body()?.height
                    val weightGet = response.body()?.weight

                    userName.text = name.toString()

                    activity?.let { it1 ->
                        Glide.with(it1).load(profileImageUrl).into(mypageEditProfileImage)
                    }

                    year.setText(yearGet)
                    month.setText(monthGet)
                    day.setText(dayGet)
                    height.setText(heightGet.toString())
                    weight.setText(weightGet.toString())

                    mypage_edit_button.isEnabled = false
                }

                override fun onFailure(call: Call<UserValidationInfo>, t: Throwable) {
                    Log.e("Error", t.message.toString())
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

    private fun savePetInfo(@SuppressLint("UseSwitchCompatOrMaterialCode") petSwitcher: SwitchMaterial) {
        petSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                context?.let { UserSharedPreferences.setPet(it, true) }
                weatherViewModel.setPetValue(true)
            } else {
                weatherViewModel.setPetValue(false)
                context?.let { UserSharedPreferences.setPet(it, false) }

            }
        }

    }

    private fun setPetInfo(@SuppressLint("UseSwitchCompatOrMaterialCode") petSwitcher: SwitchMaterial) {
        petSwitcher.isChecked = context?.let { UserSharedPreferences.getPet(it) } == true
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    @SuppressLint("Recycle")
    private fun getRealPathFromURI(uri: Uri): String? {
        val buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            return uri.path
        }
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity?.contentResolver?.query(uri, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            }
        }
        return cursor?.getString(columnIndex)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImage = data?.data
                    profile_image_edit.setImageURI(selectedImage)
                    selectedImage?.let { getRealPathFromURI(it).toString() }?.let {
                        updateProfileImage(it)
                    }

                }
            }
        }
    }

    private fun updateProfileImage(path: String) {
        val file = File(path)
        var fileName = path.replace("@", "").replace(".", "")

        fileName = "$fileName.png"

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("imgFile", fileName, requestBody)

        val imageRetrofit = Retrofit.Builder()
            .baseUrl("http://3.37.6.181:3000/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val imageServer = imageRetrofit.create(ProfileImageAPI::class.java)
        val userKey = context?.let { UserSharedPreferences.getUserId(it) }

        if (userKey != null) {
            imageServer.sendProfileImage(userKey, body).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    profileImageViewModel.setisChangedValue(true)

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("??????", "????????? ????????? ???????????? ??????")
                }

            })
        }
    }


    inner class Editwatcher : TextWatcher {
        // ???????????? ????????? ??????
        override fun afterTextChanged(p0: Editable?) {
            mypage_edit_button.isEnabled = true
        }

        // ???????????? ????????? ??????
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mypage_edit_button.isEnabled = false

        }

        // ???????????? ????????? ????????? ??????
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mypage_edit_button.isEnabled = true
        }


    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }
}


