package com.example.ddubuck.login

data class User (
        var userKey: String? = null,
        var name: String? = null,
        var birth: String? = null,
        var height: String? = null,
        var weight: String? = null,

        // 가입 루트
        var division: String? = null
)

