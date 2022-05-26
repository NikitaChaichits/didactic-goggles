package com.example.vpn.util.time

import android.os.Handler
import android.os.Looper

private var isActive: Boolean = true

fun handleMultipleCallsWithDelay(time: Int, action: () -> Unit) {
    if (isActive) {
        action().also { isActive = false }
        Handler(Looper.getMainLooper()).postDelayed({ isActive = true }, time.toLong())
    }
}
