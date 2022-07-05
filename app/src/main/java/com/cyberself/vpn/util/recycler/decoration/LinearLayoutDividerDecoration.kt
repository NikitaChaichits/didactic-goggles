package com.cyberself.vpn.util.recycler.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cyberself.vpn.util.view.px
import com.cyberself.vpn.R

class LinearLayoutDividerDecoration(
    context: Context,
    @ColorRes colorResId: Int = R.color.colorAccent,
    thicknessDp: Int = 1,
    private val offsetLeftDp: Int = 0,
    private val offsetRightDp: Int = 0,
    private val showLastDivider: Boolean = false,
): RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, colorResId)
        strokeWidth = thicknessDp.toFloat().px.toFloat()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(0, 0, 0, paint.strokeWidth.toInt())
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val offsetVertical = paint.strokeWidth / 2
        val offsetLeft = offsetLeftDp.toFloat().px.toFloat()
        val offsetRight = offsetRightDp.toFloat().px.toFloat()

        for (i in 0 until parent.childCount) {
            if (i == parent.childCount - 1 && !showLastDivider) {
                continue
            }

            val view = parent.getChildAt(i)
            c.drawLine(
                view.left + offsetLeft,
                view.bottom + offsetVertical,
                view.right - offsetRight,
                view.bottom + offsetVertical,
                paint
            )
        }
    }

}
