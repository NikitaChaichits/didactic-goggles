package com.example.vpn.util.recycler.manager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

open class ScrollLockLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {
    open val preventVerticalScroll: Boolean = false

    override fun canScrollVertically() = if (preventVerticalScroll) false else super.canScrollVertically()
}
