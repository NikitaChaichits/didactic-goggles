package com.example.vpn.data.vpn.util

import android.content.Context
import android.content.SharedPreferences
import com.example.vpn.domain.model.Server

class SharedPreference(context: Context) {

    private val APP_PREFS_NAME = "VPNPreference"

    private var mPreference: SharedPreferences? = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE)
    private var mPrefEditor: SharedPreferences.Editor? = mPreference?.edit()

    private val SERVER_COUNTRY = "server_country"
    private val SERVER_OVPN = "server_ovpn"
    private val SERVER_OVPN_USER = "server_ovpn_user"
    private val SERVER_OVPN_PASSWORD = "server_ovpn_password"


    /**
     * Save server details
     * @param server details of ovpn server
     */
    fun saveServer(server: Server) {
        mPrefEditor!!.putString(SERVER_COUNTRY, server.country)
        mPrefEditor!!.putString(SERVER_OVPN, server.ovpn)
        mPrefEditor!!.putString(SERVER_OVPN_USER, server.ovpnUserName)
        mPrefEditor!!.putString(SERVER_OVPN_PASSWORD, server.ovpnUserPassword)
        mPrefEditor!!.commit()
    }

    /**
     * Get server data from shared preference
     * @return server model object
     */
    fun getServer(): Server {
        return Server(
            mPreference!!.getString(SERVER_COUNTRY, "Japan"),
            mPreference!!.getString(SERVER_OVPN, "japan.ovpn"),
            mPreference!!.getString(SERVER_OVPN_USER, "vpn"),
            mPreference!!.getString(SERVER_OVPN_PASSWORD, "vpn")
        )
    }
}