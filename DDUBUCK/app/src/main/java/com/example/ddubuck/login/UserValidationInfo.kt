package com.example.ddubuck.login

data class UserValidationInfo(
        var id: Int?,
        var userKey: String?,
        var picture: String? = null,
        var name: String?,
        var birth: String?,
        var height: Double?,
        var weight: Double?,
        var division: String?,
        var created_at: String?
)