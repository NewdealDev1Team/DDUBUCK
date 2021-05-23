package com.mapo.ddubuck.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mapo.ddubuck.challenge.Challenge

object BookmarkSharedPreferences {
    fun setBookmarkChallenge(context: Context, list: ArrayList<Challenge>) {
        val prefs: SharedPreferences =
            context.getSharedPreferences("bookmarkedChallenge", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        var string = "["
        for (i in 0 until list.size) {
            string += Gson().toJson(list[i])
            if (i != list.size - 1) {
                string += ","
            }
        }
        string += "]"
        editor.putString("BOOKMARK_CHALLENGE", string)
        editor.apply()
    }

    fun getBookmarkedChallenge(context: Context): ArrayList<Challenge> {
        val prefs: SharedPreferences =
            context.getSharedPreferences("bookmarkedChallenge", Context.MODE_PRIVATE)
        val list = arrayListOf<Challenge>()
        val array: Array<Challenge>? = Gson().fromJson(prefs.getString("BOOKMARK_CHALLENGE", ""),
            Array<Challenge>::class.java)
        if (array != null) {
            for (i in array) {
                list.add(i)
            }
        }
        return list
    }

}