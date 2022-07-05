package com.cyberself.vpn.util.view

import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout

val TextInputLayout.text
    get() = editText?.text.toString()

fun TextInputLayout.error(@StringRes string: Int) {
    this.error = resources.getString(string)
}

fun TextInputLayout.disableError() {
    this.isErrorEnabled = false
}
