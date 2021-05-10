package com.example.ddubuck.ui.mypage

import java.io.File

data class ProfileImageUpload (
    private val userKey: String,
    private val imgFile: File
)