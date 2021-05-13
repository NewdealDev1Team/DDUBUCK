package com.mapo.ddubuck.mypage

import java.io.File

data class ProfileImageUpload (
    private val userKey: String,
    private val imgFile: File
)