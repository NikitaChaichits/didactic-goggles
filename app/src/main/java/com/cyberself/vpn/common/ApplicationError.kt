package com.cyberself.vpn.common

sealed class ApplicationError {

    // Common errors

    data class Generic(val cause: Throwable? = null) : ApplicationError()
    object NoInternetConnection : ApplicationError()

    // Http errors

    object BadRequest : ApplicationError()
    object Unauthorized : ApplicationError()
    object NotFound : ApplicationError()
    object TimeOut : ApplicationError()
    object Server : ApplicationError()

    // Client validation errors
    object EmptyFiled : ApplicationError()
    object WeakPassword : ApplicationError()
    object InvalidLogin : ApplicationError()
    object PasswordsDoesNotMatch : ApplicationError()
    object PasswordReset : ApplicationError()
    object ForgotPassword : ApplicationError()

    // QR scanner

    // Login errors
    object UserDoesNotExist : ApplicationError()
}
