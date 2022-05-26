package com.example.vpn.util.time

import java.util.*
import java.util.concurrent.TimeUnit.DAYS
import java.util.concurrent.TimeUnit.HOURS
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS

/* Common */

val timeNow get() = System.currentTimeMillis()

/* From milliseconds */

fun Long.fromMillisToSeconds() = MILLISECONDS.toSeconds(this)

fun Long.fromMillisToMinutes() = MILLISECONDS.toMinutes(this)

fun Long.fromMillisToHours() = MILLISECONDS.toHours(this)

fun Long.fromMillisToDays() = MILLISECONDS.toDays(this)

/* To milliseconds */

fun Long.fromSecondsToMillis() = SECONDS.toMillis(this)

fun Long.fromMinutesToMillis() = MINUTES.toMillis(this)

fun Long.fromHoursToMillis() = HOURS.toMillis(this)

fun Long.fromDaysToMillis() = DAYS.toMillis(this)

fun Long.fromMillisToDate() = Date(this)
