package com.example.vpn.util.view

import android.os.Handler
import android.os.Looper
import androidx.activity.addCallback
import androidx.fragment.app.FragmentActivity
import com.example.vpn.R

private var isSecondaryTap: Boolean = false

fun onBackPressedListener(activity: FragmentActivity) {
    activity.onBackPressedDispatcher.addCallback(activity) {
        if (isSecondaryTap) activity.finish()
        else {
            activity.toast(R.string.tap_to_exit)
            handleTaps()
        }
        isEnabled = true
    }
}

private fun handleTaps() {
    isSecondaryTap = true
    Handler(Looper.getMainLooper()).postDelayed({ isSecondaryTap = false }, 1000)
}
