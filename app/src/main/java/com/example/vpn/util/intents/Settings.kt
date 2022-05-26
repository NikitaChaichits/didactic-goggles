package com.example.vpn.util.intents

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS

fun Context.goToSettings() {
    val uri = Uri.fromParts("package", packageName, null)
    val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = uri }
    startActivity(intent)
}
