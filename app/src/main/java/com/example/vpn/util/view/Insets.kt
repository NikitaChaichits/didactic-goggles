package com.example.vpn.util.view

import android.graphics.Rect
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.updatePadding

fun View.addSystemWindowInsetToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    imeSensitive: Boolean = false
) = doOnApplyWindowInsetsToPadding(imeSensitive) { view, systemWindowInsets, initialPadding ->
    view.updatePadding(
        left = initialPadding.left + (if (left) systemWindowInsets.left else 0),
        top = initialPadding.top + (if (top) systemWindowInsets.top else 0),
        right = initialPadding.right + (if (right) systemWindowInsets.right else 0),
        bottom = initialPadding.bottom + (if (bottom) systemWindowInsets.bottom else 0)
    )
}

fun View.addSystemWindowInsetToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    imeSensitive: Boolean = false
) = doOnApplyWindowInsetsToMargin(imeSensitive) { view, systemWindowInsets, initialMargin ->
    view.updateMargin(
        left = initialMargin.left + (if (left) systemWindowInsets.left else 0),
        top = initialMargin.top + (if (top) systemWindowInsets.top else 0),
        right = initialMargin.right + (if (right) systemWindowInsets.right else 0),
        bottom = initialMargin.bottom + (if (bottom) systemWindowInsets.bottom else 0)
    )
}

/** Adjust to window insets via view's padding. */
fun View.doOnApplyWindowInsetsToPadding(
    imeSensitive: Boolean = false,
    block: (View, Insets, Rect) -> Unit
) = doOnApplyWindowInsetsToProperty(imeSensitive, View::getPadding) { v, systemWindowInsets, initialPadding ->
    block(v, systemWindowInsets, initialPadding)
}

/** Adjust to window insets via view's margin. */
fun View.doOnApplyWindowInsetsToMargin(
    imeSensitive: Boolean = false,
    block: (View, Insets, Rect) -> Unit
) = doOnApplyWindowInsetsToProperty(imeSensitive, View::getMargin) { v, systemWindowInsets, initialMargin ->
    block(v, systemWindowInsets, initialMargin)
}

/** Adjust custom view's property based on the Window Insets. */
fun <T> View.doOnApplyWindowInsetsToProperty(
    imeSensitive: Boolean = false,
    viewPropertyGetter: View.() -> T,
    block: (View, Insets, T) -> Unit
) = doOnApplyWindowInsets (viewPropertyGetter) { v, insets, initialPropertyValue ->
    val systemWindowInsets = insets.getSystemInsets(imeSensitive)
    block(v, systemWindowInsets, initialPropertyValue)
    insets
}

fun <T> View.doOnApplyWindowInsets(
    viewPropertyGetter: View.() -> T,
    block: (View, WindowInsetsCompat, T) -> WindowInsetsCompat
) {
    val initialPropertyValue: T = viewPropertyGetter()

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialPropertyValue)
    }

    requestApplyInsetsWhenAttached()
}

/* Internal */

private fun WindowInsetsCompat.getSystemInsets(imeSensitive: Boolean = false): Insets {
    @Type.InsetsType var insetsTypeMask: Int = Type.systemBars()
    if (imeSensitive) insetsTypeMask = insetsTypeMask or Type.ime()
    return this.getInsets(insetsTypeMask)
}

private fun View.requestApplyInsetsWhenAttached() {

    if (isAttachedToWindow) {
        ViewCompat.requestApplyInsets(this)
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(v)
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}
