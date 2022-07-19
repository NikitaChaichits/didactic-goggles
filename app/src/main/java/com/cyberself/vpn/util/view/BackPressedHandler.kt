package com.cyberself.vpn.util.view

import android.os.Handler
import android.os.Looper
import android.provider.Settings.Global.getString
import androidx.activity.addCallback
import androidx.fragment.app.FragmentActivity
import com.cyberself.vpn.R

private var isSecondaryTap: Boolean = false

fun onBackPressedListener(activity: FragmentActivity) {
    activity.onBackPressedDispatcher.addCallback(activity) {
        if (isSecondaryTap) activity.finish()
        else {
            activity.toast("Press Back again to exit.")
            handleTaps()
        }
        isEnabled = true
    }
}

private fun handleTaps() {
    isSecondaryTap = true
    Handler(Looper.getMainLooper()).postDelayed({ isSecondaryTap = false }, 1000)
}
