package com.mapo.ddubuck.sharedpref

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import kotlinx.android.synthetic.main.coach_mark.view.*

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
//
//    fun passUser(context: Context): String {
//        val prefs : SharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE)
//        return prefs.getString("ACCOUNT_ID", "").toString()
//    }

    fun setAutoLogin(context: Context, autoLoginCheckBox: CheckBox) {
        val prefs : SharedPreferences = context.getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("AUTO_LOGIN", autoLoginCheckBox.isChecked.toString())
        editor.apply()
    }

    fun getAutoLogin(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
        return prefs.getString("AUTO_LOGIN", "").toString()
    }

    fun setCoachMarkExit(context: Context, input: Boolean){
        val prefs : SharedPreferences = context.getSharedPreferences("coachMarkExit", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("COACH_MARK_EXIT", input.toString())
        editor.commit()
    }

    fun getCoachMarkExit(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences("coachMarkExit", Context.MODE_PRIVATE)
        return prefs.getString("COACH_MARK_EXIT", "").toBoolean()
    }


    fun setPet(context: Context, input: Boolean) {
        val prefs : SharedPreferences = context.getSharedPreferences("pet", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("PET", input.toString())
        editor.commit()
    }

    fun getPet(context: Context): Boolean {
        val prefs : SharedPreferences = context.getSharedPreferences("pet", Context.MODE_PRIVATE)
        return prefs.getString("PET","").toBoolean()
    }
}