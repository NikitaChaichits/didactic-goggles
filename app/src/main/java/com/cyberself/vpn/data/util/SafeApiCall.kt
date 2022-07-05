package com.cyberself.vpn.data.util

import com.cyberself.vpn.common.ApplicationError
import com.cyberself.vpn.domain.result.Result
import retrofit2.HttpException
import java.net.ConnectException
import java.net.HttpURLConnection.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        Result.Success(apiCall.invoke())
    } catch (throwable: Throwable) {
        when (throwable) {
            is SocketTimeoutException -> Result.Error(ApplicationError.TimeOut)
            is ConnectException       -> Result.Error(ApplicationError.NoInternetConnection)
            is UnknownHostException   -> Result.Error(ApplicationError.NoInternetConnection)
            is HttpException          -> {

                val error = throwable.getCustomHttpError().getApplicationErrorOrNull()
                if (error != null) return Result.Error(error)

                when (throwable.code()) {
                    HTTP_BAD_REQUEST    -> Result.Error(ApplicationError.BadRequest)
                    HTTP_UNAUTHORIZED   -> Result.Error(ApplicationError.Unauthorized)
                    HTTP_NOT_FOUND      -> Result.Error(ApplicationError.NotFound)
                    HTTP_INTERNAL_ERROR -> Result.Error(ApplicationError.Server)
                    else                -> Result.Error(ApplicationError.Generic(throwable))
                }
            }
            else                      -> {
                Result.Error(ApplicationError.Generic(throwable))
            }
        }
    }
}