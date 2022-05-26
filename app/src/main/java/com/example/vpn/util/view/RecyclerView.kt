package com.example.vpn.util.view

import android.graphics.Rect
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager

@Px
fun LinearLayoutManager.getChildBottom(childIndex: Int, defaultValue: Int): Int {
    val lastChild = getChildAt(childIndex)
    return lastChild?.let {
        val lastChildBounds = Rect()
        getDecoratedBoundsWithMargins(it, lastChildBounds)
        lastChildBounds.bottom
    } ?: defaultValue
}
