package com.cyberself.vpn.util.validation

const val EMAIL =
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"

const val PASSWORD_MIN = 8

const val PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{$PASSWORD_MIN,}\$"

const val MAX_WEIGHT = 25f
