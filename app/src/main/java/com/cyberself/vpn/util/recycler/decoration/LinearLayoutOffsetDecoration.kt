package com.cyberself.vpn.util.recycler.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutOffsetDecoration(
    @RecyclerView.Orientation private val orientation: Int,
    @Px private val offset: Int = 0,
    private val addSpaceToEdgeElements: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = (view.layoutParams as RecyclerView.LayoutParams).absoluteAdapterPosition
        val itemsCount = parent.adapter?.itemCount ?: 0

        val offsets = Rect(0, 0, 0, 0)
        when (orientation) {
            RecyclerView.HORIZONTAL -> {
                offsets.left = calculateOffset(position == 0)
                offsets.right = calculateOffset(position == itemsCount - 1)
            }
            RecyclerView.VERTICAL -> {
                offsets.top = calculateOffset(position == 0)
                offsets.bottom = calculateOffset(position == itemsCount - 1)
            }
        }

        outRect.set(offsets)
    }

    private fun calculateOffset(isEdgeItem: Boolean): Int {
        return if (isEdgeItem) {
            if (addSpaceToEdgeElements) offset else 0
        } else {
            offset / 2
        }
    }
}
