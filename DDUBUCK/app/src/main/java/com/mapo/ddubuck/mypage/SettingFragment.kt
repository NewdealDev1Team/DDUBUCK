package com.mapo.ddubuck.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mapo.ddubuck.R
import com.mapo.ddubuck.login.LoginActivity
import com.mapo.ddubuck.sharedpref.UserSharedPreferences

class SettingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val settingViewGroup: ViewGroup = inflater.inflate(R.layout.fragment_setting,
            container, false) as ViewGroup

        val versionView: TextView = settingViewGroup.findViewById(R.id.version_info_textview)
        val logoutButton: TextView = settingViewGroup.findViewById(R.id.logout_button)

        versionView.text = context?.let { getVersion(it) }

        logoutButton.setOnClickListener {
            logout()
        }

        return settingViewGroup

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