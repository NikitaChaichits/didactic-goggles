package com.example.vpn.util

import com.baseproject.data.model.pojo.common.error.ErrorResponse
import com.example.vpn.common.ApplicationError
import com.google.gson.Gson
import retrofit2.HttpException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiErrorMapper(private val gson: Gson) {

    fun toApplicationError(throwable: Throwable): ApplicationError {
        return when (throwable) {
            is SocketTimeoutException -> ApplicationError.TimeOut
            is ConnectException       -> ApplicationError.NoInternetConnection
            is UnknownHostException   -> ApplicationError.NoInternetConnection
            is HttpException          -> {

                val error = throwable.getCustomHttpError(gson).getApplicationErrorOrNull()
                if (error != null) {
                    return error
                }

                when (throwable.code()) {
                    HttpURLConnection.HTTP_BAD_REQUEST    -> ApplicationError.BadRequest
                    HttpURLConnection.HTTP_UNAUTHORIZED   -> ApplicationError.Unauthorized
                    HttpURLConnection.HTTP_NOT_FOUND      -> ApplicationError.NotFound
                    HttpURLConnection.HTTP_INTERNAL_ERROR -> ApplicationError.Server
                    else                                  -> ApplicationError.Generic(throwable)
                }
            }
            else                      -> {
                ApplicationError.Generic(throwable)
            }
        }
    }

}

fun HttpException.getCustomHttpError(gson: Gson): Int? {
    return try {
        this.response()?.errorBody()?.charStream()?.let {
            gson.fromJson(it, ErrorResponse::class.java).error
        }
    } catch (exception: Exception) {
        null
    }
}

fun Int?.getApplicationErrorOrNull() = when (this) {
    1    -> ApplicationError.UserDoesNotExist
    2    -> ApplicationError.PasswordReset
    3    -> ApplicationError.ForgotPassword
    else -> null
}
