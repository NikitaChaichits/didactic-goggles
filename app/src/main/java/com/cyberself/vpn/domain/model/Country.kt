package com.cyberself.vpn.domain.model

data class Country(
    val ip: String,
    val shortName: String,
    val fullName: String,
    val flag: Int,
    val isBestChoice: Boolean = false,
    val isChosen: Boolean = false,
)
