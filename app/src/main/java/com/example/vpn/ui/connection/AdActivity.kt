package com.example.vpn.ui.connection

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vpn.R
import com.example.vpn.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdActivity : AppCompatActivity(R.layout.activity_ad){

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        // TODO handle deeplink
        if ("deeplink" == "subscription is exist?" && "DEEPLINK&2&3" != "DEEPLINK"
            && "2" != "is exist" && "3" != "is exist"){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}