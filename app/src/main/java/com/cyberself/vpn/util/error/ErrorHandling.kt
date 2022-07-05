package com.cyberself.vpn.util.error

import com.cyberself.vpn.common.ApplicationError
import com.cyberself.vpn.R

fun ApplicationError.message() =

    when (this) {

        is ApplicationError.Generic                -> R.string.generic_error
        ApplicationError.NoInternetConnection      -> R.string.noInternetConnection_error

        ApplicationError.BadRequest                -> R.string.badRequest_error
        ApplicationError.NotFound                  -> R.string.notFound_error
        ApplicationError.Unauthorized              -> R.string.unauthorized_error
        ApplicationError.TimeOut                   -> R.string.timeout_error
        ApplicationError.Server                    -> R.string.server_error

        ApplicationError.EmptyFiled                -> R.string.emptyField_error
        ApplicationError.WeakPassword              -> R.string.weakPassword_error
        ApplicationError.InvalidLogin              -> R.string.invalidLogin_error
        ApplicationError.PasswordsDoesNotMatch     -> R.string.password_doesNot_match_error
        ApplicationError.PasswordReset             -> R.string.passwordReset_error
        ApplicationError.ForgotPassword            -> R.string.passwordForgot_error

        ApplicationError.UserDoesNotExist          -> R.string.userNotExist_error
    }
