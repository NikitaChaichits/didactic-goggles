package com.cyberself.vpn.util.intents

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.startNavigation(latitude: Double, longitude: Double) {
    val navUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=($latitude,$longitude)")
    startActivity(Intent(Intent.ACTION_VIEW, navUri))
}
