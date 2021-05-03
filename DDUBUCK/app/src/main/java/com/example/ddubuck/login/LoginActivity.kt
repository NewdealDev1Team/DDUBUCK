package com.example.ddubuck.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.sharedpref.UserSharedPreferences
import com.example.ddubuck.userinfo.UserInfoBirthdayActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {

    lateinit var mOAuthLoginInstance: OAuthLogin
    lateinit var mContext: Context
    var auth: FirebaseAuth? = null
    val GOOGLE_REQUEST_CODE = 99
    val TAG = "googleLogin"

    private lateinit var googleSignInClient: GoogleSignInClient
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_layout)

        // LoginView에서 상단바 제거
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()


        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleSignInBtn: ImageButton = findViewById(R.id.google_login_button)
        googleSignInBtn.setOnClickListener {
            signIn()
        }

        val kakaoLogin: ImageButton = findViewById(R.id.kakao_login_button)
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()

                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", error)
                    } else if (user != null) {
                        val id = user.id.toString()
                        val name = user.kakaoAccount?.profile?.nickname.toString()
                        val birthday = if (user.kakaoAccount?.birthyear == null) {
                            "1990" + "-" + user.kakaoAccount?.birthday.toString()
                                .substring(0, 2) + "-" + user.kakaoAccount?.birthday.toString()
                                .substring(2, 4)
                        } else {
                            user.kakaoAccount?.birthyear + "-" + user.kakaoAccount?.birthday.toString()
                                .substring(0, 2) + "-" + user.kakaoAccount?.birthday.toString()
                                .substring(2, 4)
                        }
                        saveUserInfo(id, name, birthday, "Kakao")
                    }
                }
            }

        }

        // 카톡 있냐 없냐!
        kakaoLogin.setOnClickListener {
            if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        mContext = this
        initData()

    }

    // For Google Login
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                val id = account.id!!.toString()
                val name = if (account.familyName == null) {
                    account.givenName?.toString()!!
                } else {
                    account.familyName?.toString()!! + account.givenName?.toString()
                }
                val birthday = "2000-01-01"
                saveUserInfo(id, name, birthday, "Google")

                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.e(TAG, "Success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    // For naver login
    private fun initData() {
        //초기화
        mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(
            mContext,
            getString(R.string.OAUTH_CLIENT_ID),
            getString(R.string.OAUTH_CLIENT_SECRET),
            getString(R.string.OAUTH_CLIENT_NAME)
        )
        val mOAuthLoginButton: OAuthLoginButton =
            findViewById<View>(R.id.buttonOAuthLoginImg) as OAuthLoginButton

        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler)

        //custom img 변경시 사용
        //mOAuthLoginButton.setBgResourceId(R.drawable.btn_naver_white_kor);
    }

    private val mOAuthLoginHandler: OAuthLoginHandler = @SuppressLint("HandlerLeak")
    object : OAuthLoginHandler() {

        override fun run(success: Boolean) {

            if (success) {

                val accessToken = mOAuthLoginInstance.getAccessToken(mContext)
                val refreshToken = mOAuthLoginInstance.getRefreshToken(mContext)
                val expiresAt = mOAuthLoginInstance.getExpiresAt(mContext)
                val tokenType = mOAuthLoginInstance.getTokenType(mContext)

                var header = "Bearer $accessToken"
                val baseURL = "https://openapi.naver.com/"

                val retrofit = Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val api = retrofit.create(NaverAPI::class.java)
                val callGetUserInfo = api.getUserInfo(header)

                callGetUserInfo.enqueue(object : retrofit2.Callback<UserInfo> {
                    override fun onResponse(
                        call: Call<UserInfo>,
                        response: Response<UserInfo>,
                    ) {
                        val user = response.body()?.response

                        if (user != null) {
                            // 네이버 삽입
                            val id = user.id.toString()
                            val name = user.nickname
                            val birthday = user.birthyear + "-" + user.birthday

                            saveUserInfo(id, name, birthday, "Naver")

                        }

                    }


                    override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                        Log.d("실패", "Naver Login Fail")
                    }
                })


            } else {
                val errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).code
                val errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext)
                Toast.makeText(
                    mContext, "errorCode:" + errorCode
                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loginSuccess(id: String) {

        val userValidation: Retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.6.181:3000/get/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val userValidationServer: UserService = userValidation.create(UserService::class.java)
        val toMainActivity = Intent(this, MainActivity::class.java)
        val toBirthdayActivity = Intent(this, UserInfoBirthdayActivity::class.java)

        userValidationServer.getUserInfo(id)
            .enqueue(object : Callback<UserValidationInfo> {
                override fun onResponse(
                    call: Call<UserValidationInfo>,
                    response: Response<UserValidationInfo>,
                ) {
                    val height = response.body()?.height
                    val weight = response.body()?.weight

                    if (height!!.toInt() == 0 && weight!!.toInt() == 0) {
                        startActivity(toBirthdayActivity)
                        overridePendingTransition(R.anim.activity_slide_in,
                            R.anim.activity_slide_out)
                    } else {
                        startActivity(toMainActivity)
                        overridePendingTransition(R.anim.activity_slide_in,
                            R.anim.activity_slide_out)

                    }
                }

                override fun onFailure(call: Call<UserValidationInfo>, t: Throwable) {
                    t.message?.let { Log.e("Fail", it) }
                }
            })

    }


    private fun saveUserInfo(id: String, name: String, birthday: String, diversion: String) {


        // Shared Preference에 회원 id 저장
        UserSharedPreferences.setUserId(this, id)

        val userInfo: Retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.6.181:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val userServer: UserService = userInfo.create(UserService::class.java)

        userServer.saveUserInfo(id, name, birthday, "0", "0", diversion)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Log.e("Success", response.message())
                    loginViewModel.getResponseValue(true)
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    t.message?.let { Log.e("Fail", it) }
                }
            })

        loginViewModel.isSuccessfulLoginResponse.observe(this, { v ->
            if (v == true) {
                loginSuccess(id)
            }
        })

    }

//    private fun autoLogin(id: String) {
//        val userValidation: Retrofit = Retrofit.Builder()
//            .baseUrl("http://3.37.6.181:3000/get/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val userValidationServer: UserService = userValidation.create(UserService::class.java)
//        val toMainActivity = Intent(this, MainActivity::class.java)
//
//        userValidationServer.getUserInfo(id).enqueue(object : Callback<UserValidationInfo> {
//            override fun onResponse(call: Call<UserValidationInfo>, response: Response<UserValidationInfo>) {
//                val successMessage = response.message()
//
//                Log.e("메시지", successMessage)
//            }
//
//            override fun onFailure(call: Call<UserValidationInfo>, t: Throwable) {
//                t.message?.let { Log.e("Failllll", it) }
//            }
//        })
//    }

}