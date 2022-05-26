@file:Suppress("UnnecessaryVariable")

package com.example.vpn.util.time

import android.content.Context
import com.example.vpn.R
import com.example.vpn.common.constants.*
import com.example.vpn.util.locale.currentDeviceLocale
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/** used for non-locale specific formatters */
val fallbackLocale: Locale = Locale.US
val calendar: Calendar = Calendar.getInstance()

val timeWithDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern(timeWithDate, fallbackLocale)

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(date, fallbackLocale)

val dayTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dayTime, fallbackLocale)

fun getSmartDate(passedTime: Long, context: Context): String {

    val notificationTime = timeNow - passedTime

    val seconds = passedTime.fromMillisToSeconds()
    val minutes = passedTime.fromMillisToMinutes()
    val hours = passedTime.fromMillisToHours()

    val justNowString = context.getString(R.string.notifications_time_now)
    val yesterdayString = context.getString(R.string.notifications_time_yesterday)
    val minuteString = context.getString(R.string.notifications_time_minute)
    val hourString = context.getString(R.string.notifications_time_hour)

    return when {
        seconds < SECONDS_IN_MINUTE -> justNowString
        minutes < MINUTE_IN_HOUR -> minutes.toTimeWithUnit(minuteString)
        hours > SIX_HOURS && notificationTime.isYesterday() -> yesterdayString
        hours < HOURS_IN_DAY -> hours.toTimeWithUnit(hourString)
        else -> {
            notificationTime.toDateWithMonthWord(context.currentDeviceLocale())
        }
    }
}

private fun Long.toDateWithMonthWord(locale: Locale): String {

    calendar.timeInMillis = this

    val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale)
    val date = "${calendar[Calendar.DAY_OF_MONTH]} $month, ${calendar[Calendar.YEAR]}"

    return date
}

private fun Long.isYesterday(): Boolean {

    calendar.timeInMillis = this
    val notificationDay = calendar.get(Calendar.DAY_OF_YEAR)
    val notificationYear = calendar.get(Calendar.YEAR)

    calendar.timeInMillis = timeNow
    val dayNow = calendar.get(Calendar.DAY_OF_YEAR)
    val yearNow = calendar.get(Calendar.YEAR)

    val sameYear = yearNow == notificationYear
    val yesterday = dayNow - notificationDay == 1

    return sameYear && yesterday
}

private fun Long.toTimeWithUnit(timeUnit: String) = "$this $timeUnit"

/** Note: not locale specific. See [timeWithDateFormatter]. */
fun OffsetDateTime.toTimeWithDate(zone: ZoneId = ZoneId.systemDefault()): String =
    timeWithDateFormatter.withZone(zone).format(this)

/** Note: not locale specific. See [dayTimeFormatter]. */
fun OffsetDateTime.toDayTime(zone: ZoneId = ZoneId.systemDefault()): String =
    dayTimeFormatter.withZone(zone).format(this)

/** Note: not locale specific. See [dateFormatter]. */
fun LocalDate.toDate(): String = dateFormatter.format(this)

fun OffsetDateTime.toLocalisedDate(context: Context): String {
    val locale = context.currentDeviceLocale()
    val month = month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
    return "$dayOfMonth. $month $year"
}

fun LocalTime.toDayTime(): String = dayTimeFormatter.format(this)
