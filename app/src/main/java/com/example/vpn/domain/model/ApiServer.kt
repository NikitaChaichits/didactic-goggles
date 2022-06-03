package com.example.vpn.domain.model

data class ApiServer(
    val ip : String,
    val clients : Int,
    val info : Info
)
