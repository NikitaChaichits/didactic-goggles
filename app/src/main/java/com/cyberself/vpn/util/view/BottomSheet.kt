package com.cyberself.vpn.util.view

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun <V : View> BottomSheetBehavior<V>.doOnStateChanged(callback: (bottomSheet: View, newState: Int) -> Unit) {
    addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            callback(bottomSheet, newState)
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    })
}
