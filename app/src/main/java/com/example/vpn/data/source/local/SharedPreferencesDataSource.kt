package com.example.vpn.data.source.local

import android.content.Context
import android.content.SharedPreferences
import com.example.vpn.util.sharedpreferences.FIRST_LAUNCH
import com.example.vpn.util.sharedpreferences.KEY_SHARED_PREFS_NAME
import com.example.vpn.util.sharedpreferences.get
import com.example.vpn.util.sharedpreferences.put
import javax.inject.Inject

class SharedPreferencesDataSource @Inject constructor(applicationContext: Context) {

    private val sharedPrefs : SharedPreferences = applicationContext
        .getSharedPreferences(KEY_SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun setFirstLaunch(boolean: Boolean) = sharedPrefs.put(FIRST_LAUNCH, boolean)

    fun getFirstLaunch(): Boolean = sharedPrefs.get(FIRST_LAUNCH, true)

}
