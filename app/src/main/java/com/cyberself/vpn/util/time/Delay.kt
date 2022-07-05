package com.cyberself.vpn.util.time

import android.os.Handler
import android.os.Looper

private var isActive: Boolean = true

fun handleMultipleCallsWithDelay(time: Long, action: () -> Unit) {
    if (isActive) {
        action().also { isActive = false }
        Handler(Looper.getMainLooper()).postDelayed({ isActive = true }, time)
    }
}

fun handleWithDelay(time: Long, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(action, time)
}
