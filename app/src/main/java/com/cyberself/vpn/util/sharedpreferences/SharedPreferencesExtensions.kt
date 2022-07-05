@file:Suppress("UNCHECKED_CAST")

package com.cyberself.vpn.util.sharedpreferences

import android.annotation.SuppressLint
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T): T {
    when (T::class) {
        Boolean::class -> return getBoolean(key, defaultValue as Boolean) as T
        Float::class -> return getFloat(key, defaultValue as Float) as T
        Int::class -> return getInt(key, defaultValue as Int) as T
        Long::class -> return getLong(key, defaultValue as Long) as T
        String::class -> return getString(key, defaultValue as String?) as T
        else           -> {
            if (defaultValue is Set<*>) {
                return getStringSet(key, defaultValue as Set<String>) as T
            }
        }
    }
    return defaultValue
}


@SuppressLint("ApplySharedPref")
inline fun <reified T> SharedPreferences.put(key: String, value: T, synchronously: Boolean = false) {
    val editor = edit()
    when (T::class) {
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Float::class -> editor.putFloat(key, value as Float)
        Int::class -> editor.putInt(key, value as Int)
        Long::class -> editor.putLong(key, value as Long)
        String::class -> editor.putString(key, value as String?)
        else           -> {
            if (value is Set<*>) {
                editor.putStringSet(key, value as Set<String>)
            }
        }
    }

    if (synchronously) {
        editor.commit()
    } else {
        editor.apply()
    }
}

@SuppressLint("ApplySharedPref")
fun SharedPreferences.remove(key: String, synchronously: Boolean = false) {
    edit().remove(key).also {
        if (synchronously) {
            it.commit()
        } else {
            it.apply()
        }
    }
}

@SuppressLint("ApplySharedPref")
fun SharedPreferences.clear( synchronously: Boolean = false) {
    edit().clear().also {
        if (synchronously) {
            it.commit()
        } else {
            it.apply()
        }
    }
}

/* Async versions of extensions */

suspend inline fun <reified T> SharedPreferences.getAsync(key: String, defaultValue: T): T =
    withContext(Dispatchers.IO) {
        get(key, defaultValue)
    }

suspend inline fun <reified T> SharedPreferences.putAsync(key: String, value: T) =
    withContext(Dispatchers.IO) {
        put(key, value, true)
    }

suspend fun SharedPreferences.removeAsync(key: String) = withContext(Dispatchers.IO) { remove(key, true) }

suspend fun SharedPreferences.clearAsync() = withContext(Dispatchers.IO) { clear(true) }
