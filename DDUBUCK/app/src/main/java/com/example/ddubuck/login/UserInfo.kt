package com.example.ddubuck.login

import android.provider.ContactsContract

data class UserInfo(
        val resultCode: String,
        var message: String,
        val response: Response
)

data class Response(
        val id: String,
        val nickname: String,
        val name: String,
        val email: String,
        val gender: String,
        val age: String,
        val birthday: String,
        val birthyear: String,
        val mobile: String
)