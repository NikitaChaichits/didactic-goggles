package com.cyberself.vpn.util

fun String?.isNotNullAndNotEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}
