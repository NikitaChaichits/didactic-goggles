package com.example.vpn.domain.model

data class Country(
    val name: String,
    val flag: Int,
    val isBestChoice: Boolean = false,
    val isChosen: Boolean = false
)
