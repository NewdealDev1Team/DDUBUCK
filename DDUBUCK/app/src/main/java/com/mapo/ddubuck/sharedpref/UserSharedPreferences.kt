package com.mapo.ddubuck.sharedpref

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import com.google.gson.Gson
import com.mapo.ddubuck.data.home.CourseItem
import com.mapo.ddubuck.data.home.WalkRecord
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

    fun setFilterVisible(context: Context,key:String,input: Boolean) {
        val prefs : SharedPreferences = context.getSharedPreferences("filter", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putBoolean(key, input)
        editor.apply()
    }

    fun getFilterVisible(context:Context, key:String): Boolean {
        val prefs : SharedPreferences = context.getSharedPreferences("filter", Context.MODE_PRIVATE)
        return prefs.getBoolean(key, false)
    }

    fun setUserWeight(context: Context, weight: Double) {
        val prefs : SharedPreferences = context.getSharedPreferences("userWeight", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("USER_WEIGHT", weight.toString())
        editor.apply()
    }

    fun getUserWeight(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences("userWeight", Context.MODE_PRIVATE)
        return prefs.getString("USER_WEIGHT", "")!!.toString()
    }

    fun setBookmarkedCourse(context:Context, list:ArrayList<CourseItem>) {
        val prefs : SharedPreferences = context.getSharedPreferences("bookmarkedCourse", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        var string = "["
        for (i in 0 until list.size) {
            string += Gson().toJson(list[i])
            if(list[i]!=list.last()) {
                string += ","
            }
        }
        string+="]"
        editor.putString("BOOKMARK_COURSE", string)
        editor.apply()
    }

    fun getBookmarkedCourse(context: Context) : ArrayList<CourseItem> {
        val prefs : SharedPreferences = context.getSharedPreferences("bookmarkedCourse", Context.MODE_PRIVATE)
        val list = arrayListOf<CourseItem>()
        val array :Array<CourseItem>? = Gson().fromJson(prefs.getString("BOOKMARK_COURSE", ""),
            Array<CourseItem>::class.java)
        if(array != null) {
            for (i in array) {
                list.add(i)
            }
        }
        return list
    }

}