package com.example.vpn.util.locale

import android.content.Context
import java.util.*

fun Context.currentDeviceLocale(): Locale = resources.configuration.locales[0]
