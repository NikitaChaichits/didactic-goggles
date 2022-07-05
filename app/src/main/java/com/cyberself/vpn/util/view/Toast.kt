@file:Suppress("DEPRECATION")

package com.cyberself.vpn.util.view

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.cyberself.vpn.R

fun Context.toast(@StringRes messageStringRes: Int) {

    val toast = Toast.makeText(this, messageStringRes, Toast.LENGTH_SHORT)
    val view = toast.view
    val text = view?.findViewById(android.R.id.message) as TextView?
    val color = getColor(this, R.color.colorAccent)

    view?.background?.colorFilter =
        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_IN)
    text?.setTextColor(getColor(this, android.R.color.white))

    toast.show()
}
