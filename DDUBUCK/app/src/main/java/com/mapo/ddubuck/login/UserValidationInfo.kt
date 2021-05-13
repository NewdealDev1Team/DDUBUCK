package com.mapo.ddubuck.login

data class UserValidationInfo(
        var id: Int?,
        var userKey: String?,
        var picture: String?,
        var name: String?,
        var birth: String?,
        var height: Double?,
        var weight: Double?,
        var division: String?,
        var created_at: String?
)