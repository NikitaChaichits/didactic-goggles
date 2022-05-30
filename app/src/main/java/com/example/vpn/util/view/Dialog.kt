package com.example.vpn.util.view

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.example.vpn.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.dialogBuilder(owner: LifecycleOwner, title: Int): MaterialDialog {
    return MaterialDialog(this)
        .lifecycleOwner(owner)
        .title(title)
        .cornerRadius(res = R.dimen.radius_12)
        .cancelOnTouchOutside(cancelable = true)
}

fun Context.alertDialog(
    @StringRes title: Int,
    message: CharSequence,
    @StringRes positiveText: Int,
    onPositiveClick: () -> Unit
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        .setPositiveButton(positiveText) { _, _ ->
            onPositiveClick()
        }
        .show()
}
