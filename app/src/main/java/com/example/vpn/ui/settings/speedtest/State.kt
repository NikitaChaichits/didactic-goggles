package com.example.vpn.ui.settings.speedtest

enum class State(val value: String) {
    SELECT("SELECTING"),
    INITIAL("INITIAL"),
    CALCULATING("CHECKING"),
    DONE("DONE"),
    ERROR("ERROR")
}