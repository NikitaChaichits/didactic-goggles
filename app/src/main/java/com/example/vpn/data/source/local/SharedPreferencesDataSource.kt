package com.example.vpn.data.source.local

import android.content.Context
import android.content.SharedPreferences
import com.example.vpn.util.sharedpreferences.*
import javax.inject.Inject

class SharedPreferencesDataSource @Inject constructor(applicationContext: Context) {

    private val sharedPrefs : SharedPreferences = applicationContext
        .getSharedPreferences(KEY_SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun setFirstLaunch(boolean: Boolean) = sharedPrefs.put(FIRST_LAUNCH, boolean)

    fun getFirstLaunch(): Boolean = sharedPrefs.get(FIRST_LAUNCH, true)

    fun setCountryName(name: String) = sharedPrefs.put(COUNTRY_NAME, name)

    fun getCountryName(): String = sharedPrefs.get(COUNTRY_NAME, "US")

    fun setIsPremium(boolean: Boolean) = sharedPrefs.put(IS_PREMIUM, boolean)

    fun getIsPremium(): Boolean = sharedPrefs.get(IS_PREMIUM, false)
}
