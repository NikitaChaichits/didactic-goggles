package com.cyberself.vpn.util.view

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins

val Float.dp: Float get() = this / Resources.getSystem().displayMetrics.density

val Float.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}

fun View.getPadding() = Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)

fun View.getMargin() = Rect(marginLeft, marginTop, marginRight, marginBottom)

fun View.updateMargin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    this.updateLayoutParams {
        (this as? ViewGroup.MarginLayoutParams)?.let {
            updateMargins(
                left = left,
                top = top,
                right = right,
                bottom = bottom
            )
        }
    }
}

fun View.canScrollVertically(): Boolean {
    return canScrollVertically(1) || canScrollVertically(-1)
}

fun View.doOnLayoutChanged(
    skipSameInputs: Boolean = true,
    callback: (left: Int, top: Int, right: Int, bottom: Int) -> Unit
) {
    addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        // Reducing unnecessary calls when view's position is the same.
        if (skipSameInputs && left == oldLeft && top == oldTop && right == oldRight && bottom == oldBottom) {
            return@addOnLayoutChangeListener
        }
        callback(left, top, right, bottom)
    }
}

@ColorInt
fun View.getColorValue(@ColorRes colorRes: Int): Int = ContextCompat.getColor(context, colorRes)
