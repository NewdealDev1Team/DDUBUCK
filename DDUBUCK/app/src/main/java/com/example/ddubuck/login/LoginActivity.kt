package com.example.ddubuck.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.SecondActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {

    // 우리 서버로 저장하는 방식 적용할 것

    private val mAuth: FirebaseAuth? = null
    lateinit var mOAuthLoginInstance: OAuthLogin
    lateinit var mContext: Context
    var auth: FirebaseAuth? = null
    val GOOGLE_REQUEST_CODE = 99
    val TAG = "googleLogin"

    private var database: DatabaseReference = Firebase.database.reference

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LoginView에서 상단바 제거
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        setContentView(R.layout.login_layout)
        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleSignInBtn : ImageButton = findViewById(R.id.google_login_button)
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
                    }
                    else if (user != null) {

                        // 카카오 가입자 정보 전송
                        database.child("users").child("Kakao")
                        database.child("users").child("Kakao").child(user.id.toString())
                        database.child("users").child("Kakao").child(user.id.toString()).child("username").setValue(user.kakaoAccount?.profile?.nickname)
                        database.child("users").child("Kakao").child(user.id.toString()).child("birthday").setValue( user.kakaoAccount?.birthyear+ "-" + user.kakaoAccount?.birthday.toString().substring(0,2) + "-" + user.kakaoAccount?.birthday.toString().substring(2,4) )

                    }
                }

                kakaoSuccess()
            }
        }

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
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
//                println("Google ID")
//                println(account.givenName + " " + account.familyName)


                // 구글 가입자 정보 전송
                database.child("users").child("Google")
                database.child("users").child("Google").child(account.id.toString())
                database.child("users").child("Google").child(account.id.toString()).child("username").setValue(account.familyName + account.givenName)
                database.child("users").child("Google").child(account.id.toString()).child("birthday").setValue("2000-01-01")

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
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "로그인 성공")
                    val user = auth!!.currentUser
                    loginSuccess()
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

                //                mOauthAT.setText(accessToken);
//                mOauthRT.setText(refreshToken);
//                mOauthExpires.setText(String.valueOf(expiresAt));
//                mOauthTokenType.setText(tokenType);
//                mOAuthState.setText(mOAuthLoginInstance.getState(mContext).toString());
                Toast.makeText(mContext, "success:$accessToken", Toast.LENGTH_SHORT).show()
                println("success:$accessToken")

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
                            response: Response<UserInfo>
                    ) {
                        Log.d("결과", "성공 : ${response.raw()}")
                        println("헤더 : " + response.headers())
                        println("바디 : " + response.body()?.response)

                        val user = response.body()?.response
                        val userInfo = emptyArray<String>()
                        if (user != null) {
                            // 네이버 삽입
                            database.child("users").child("Naver")
                            database.child("users").child("Naver").child(user.id)
                            database.child("users").child("Naver").child(user.id).child("username").setValue(user.nickname)
                            database.child("users").child("Naver").child(user.id).child("birthday").setValue(user.birthyear + "-" + user.birthday)

                        }
                    }

                    override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                        Log.d("실패", "우엥")
                    }

                })

                //본인이 이동할 액티비티를 입력
                naverSuccess()

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

    private fun naverSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun kakaoSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun loginSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}