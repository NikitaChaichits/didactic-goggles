package com.example.vpn.data.list

import com.example.vpn.R
import com.example.vpn.domain.model.Country

object CountryList {
    val list: List<Country> = listOf(
        Country("United States", R.drawable.ic_flag_usa, isBestChoice = true, isChosen = true),
        Country("Singapore", R.drawable.ic_flag_singapore),
        Country("India", R.drawable.ic_flag_india),
        Country("Germany", R.drawable.ic_flag_germany),
        Country("Canada", R.drawable.ic_flag_canada),
        Country("Netherlands", R.drawable.ic_flag_netherlands),
        Country("England", R.drawable.ic_flag_england),
        Country("China", R.drawable.ic_flag_china),
        Country("Czech Republic", R.drawable.ic_flag_czech_republic),
        Country("Israel", R.drawable.ic_flag_israel),
        Country("Japan", R.drawable.ic_flag_japane),
        Country("Denmark", R.drawable.ic_flag_denmark),
        Country("Ireland", R.drawable.ic_flag_ireland)
    )
}