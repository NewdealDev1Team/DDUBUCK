package com.example.ddubuck.ui.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ddubuck.MainActivity
import com.example.ddubuck.R
import com.example.ddubuck.login.LoginActivity
import com.example.ddubuck.sharedpref.UserSharedPreferences

class SettingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val settingView = inflater.inflate(R.layout.fragment_setting, container, false)

        val versionView: TextView = settingView.findViewById(R.id.version_info_textview)
        val logoutButton: TextView = settingView.findViewById(R.id.logout_button)

        versionView.text = context?.let { getVersion(it) }

        logoutButton.setOnClickListener {
            logout()
        }

        return settingView

    }

    private fun getVersion(context: Context) : String? {
        var versionName = ""
        val pm = context.packageManager.getPackageInfo(context.packageName, 0)
        versionName = pm.versionName

        return versionName
    }

    private fun logout() {
        context?.let { UserSharedPreferences.setUserId(it, "") }
        activity?.let {
            val loginActivity = Intent(context, LoginActivity::class.java)
            startActivity(loginActivity)
        }
    }
}