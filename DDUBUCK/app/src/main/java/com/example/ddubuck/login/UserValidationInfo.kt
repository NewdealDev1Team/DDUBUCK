package com.example.ddubuck.login

data class UserValidationInfo(
        var id: Int?,
        var userKey: String?,
        var picture: String? = null,
        var name: String?,
        var year: String?,
        var month: String?,
        var day: String?,
        var height: Int?,
        var weight: Int?,
        var division: String?,
        var created_at: String?
)