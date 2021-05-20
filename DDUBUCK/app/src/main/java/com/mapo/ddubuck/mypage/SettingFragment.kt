package com.mapo.ddubuck.mypage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mapo.ddubuck.R
import com.mapo.ddubuck.login.LoginActivity
import com.mapo.ddubuck.sharedpref.UserSharedPreferences
import com.mapo.ddubuck.userinfo.NextTimeDialog

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

            val dialog = NextTimeDialog("로그아웃",
                "정말 로그아웃 하시겠습니까?",
                context as Activity)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val okButton: TextView = dialog.findViewById(R.id.dialog_ok_button)
            okButton.setOnClickListener {
                logout()
                dialog.dismiss()
            }

            val cancelButton: TextView = dialog.findViewById(R.id.dialog_cancel_button)
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

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