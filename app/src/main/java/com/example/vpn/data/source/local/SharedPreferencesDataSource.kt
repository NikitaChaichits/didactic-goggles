package com.example.vpn.data.source.local

import android.app.Application
import javax.inject.Inject

class SharedPreferencesDataSource @Inject constructor(private val app: Application) {

    private val sharedPrefs by lazy {}

}
