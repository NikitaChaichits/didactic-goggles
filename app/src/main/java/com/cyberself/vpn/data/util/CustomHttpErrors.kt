package com.cyberself.vpn.data.util

import com.cyberself.vpn.common.ApplicationError
import com.cyberself.vpn.common.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException

fun HttpException.getCustomHttpError(): Int? {
    return try {
        this.response()?.errorBody()?.charStream()?.let {
            Gson().fromJson(it, ErrorResponse::class.java).error
        }
    } catch (exception: Exception) {
        null
    }
}

fun Int?.getApplicationErrorOrNull() = when (this) {
    1    -> ApplicationError.EmptyFiled
    else -> null
}