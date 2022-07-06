package com.cyberself.vpn.domain.model

import com.cyberself.vpn.R

data class ApiServer(
    val ip : String,
    val clients : Int,
    val info : Info
){
    fun mapToCountry() = Country(
        ip = ip,
        shortName = info.country,
        fullName = when (info.country) {
            "US" -> "United States"
            "CA" -> "Canada"
            "GB" -> "England"
            "NL" -> "Netherlands"
            "DE" -> "Germany"
            "SG" -> "Singapore"
            "IN" -> "India"
            else -> "Unknown country"
        },
        flag = when (info.country) {
            "US" -> R.drawable.ic_flag_usa
            "CA" -> R.drawable.ic_flag_canada
            "GB" -> R.drawable.ic_flag_england
            "NL" -> R.drawable.ic_flag_netherlands
            "DE" -> R.drawable.ic_flag_germany
            "SG" -> R.drawable.ic_flag_singapore
            "IN" -> R.drawable.ic_flag_india
            else -> R.drawable.ic_placeholder
        },
        isBestChoice = info.country == "US",
        isActiveWithoutPremium = info.country == "US",
    )
}
