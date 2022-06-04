package com.example.vpn.domain.model

data class Server (
    var country: String? = null,
    var ovpn: String? = null,
    var ovpnUserName: String? = null,
    var ovpnUserPassword: String? = null
)