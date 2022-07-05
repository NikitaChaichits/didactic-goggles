package com.cyberself.vpn.data.constants

import com.cyberself.vpn.BuildConfig

object RemoteConstants {
    const val BASE_URL = "http://165.227.117.4:2525/"
    const val GET_LIST = "ovpn/list"
    const val GET_CONFIG = "ovpn/config/{ip}"
    const val AUTHORIZATION = "Authorization"

    const val KEY = BuildConfig.AUTHORIZATION_KEY
}