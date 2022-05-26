package com.example.vpn.util.numbers

import android.content.Context
import android.icu.text.NumberFormat
import com.example.vpn.util.locale.currentDeviceLocale
import java.text.ParseException

fun Double.toPriceString(context: Context): String {
    val locale = context.currentDeviceLocale()
    val priceNumberFormatter: NumberFormat = NumberFormat.getNumberInstance(locale)
        .apply {
            maximumFractionDigits = 2
        }
    return priceNumberFormatter.format(this)
}

fun Float.toWeightString(context: Context): String {
    val locale = context.currentDeviceLocale()
    val weightNumberFormatter: NumberFormat = NumberFormat.getNumberInstance(locale)
        .apply {
            maximumFractionDigits = 3
        }
    return weightNumberFormatter.format(this)
}

@Throws(ParseException::class)
fun String.toWeight(context: Context): Float {
    val locale = context.currentDeviceLocale()
    val weightNumberFormatter: NumberFormat = NumberFormat.getNumberInstance(locale)
    return weightNumberFormatter.parse(this).toFloat()
}

fun String.toWeightOrNull(context: Context): Float? {
    return try {
        toWeight(context)
    } catch (e: ParseException) {
        null
    }
}
