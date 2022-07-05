package com.cyberself.vpn.util.permission

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun Context.checkPermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED

fun Context.checkAllNotificationsPermission() =
    NotificationManagerCompat.from(this).areNotificationsEnabled()

fun Context.checkSpecificNotificationChannelPermission(channelId: String): Boolean {
    return if (checkAllNotificationsPermission()) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(channelId)
        channel?.importance != NotificationManager.IMPORTANCE_NONE
    } else false
}
