package com.example.ddubuck.sharedpref

import android.content.Context
import android.content.SharedPreferences

object UserSharedPreferences {

    fun setUserId(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("ACCOUNT_ID", input)
        editor.commit()
    }

    fun getUserId(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE)
        return prefs.getString("ACCOUNT_ID", "").toString()
    }


}