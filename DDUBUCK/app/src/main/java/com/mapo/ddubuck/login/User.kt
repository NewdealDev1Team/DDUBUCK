package com.mapo.ddubuck.login

data class User (
        var userKey: String? = null,
        var name: String? = null,
        var birth: String? = null,
        var height: Int? = null,
        var weight: Int? = null,

        // 가입 루트
        var division: String? = null
)

