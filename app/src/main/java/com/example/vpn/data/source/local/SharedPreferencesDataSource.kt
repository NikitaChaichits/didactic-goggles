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

    fun setCountryIndex(index: Int) = sharedPrefs.put(COUNTRY_INDEX, index)

    fun getCountryIndex(): Int = sharedPrefs.get(COUNTRY_INDEX, 0)
}
