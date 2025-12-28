package com.savet.local.ocr.ui.manager

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ControlScrollFlowLayoutManager(
    private val context: Context
) : RecyclerView.LayoutManager() {

    /** 是否允许垂直滚动 */
    var enableScroll = true

    private var totalHeight = 0
    private var verticalScrollOffset = 0

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return FlowLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): RecyclerView.LayoutParams {
        return FlowLayoutParams(lp)
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
        return lp is FlowLayoutParams
    }

    override fun canScrollVertically(): Boolean = enableScroll

    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            verticalScrollOffset = 0
            return
        }

        if (state.isPreLayout) return

        detachAndScrapAttachedViews(recycler)

        var offsetX = paddingLeft
        // ⭐ 关键：布局起点 = paddingTop - 当前滚动偏移
        var offsetY = paddingTop - verticalScrollOffset
        var lineMaxHeight = 0

        totalHeight = 0

        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)

            val lp = view.layoutParams as FlowLayoutParams
            val childWidth = getDecoratedMeasuredWidth(view)
            val childHeight = getDecoratedMeasuredHeight(view)

            /** 强制换行（等价 isWrapBefore = true） */
            if (lp.wrapBefore) {
                offsetX = paddingLeft
                offsetY += lineMaxHeight
                lineMaxHeight = 0
            }

            /** 自动换行 */
            if (offsetX + childWidth > width - paddingRight) {
                offsetX = paddingLeft
                offsetY += lineMaxHeight
                lineMaxHeight = 0
            }

            layoutDecorated(
                view,
                offsetX,
                offsetY,
                offsetX + childWidth,
                offsetY + childHeight
            )

            offsetX += childWidth
            lineMaxHeight = maxOf(lineMaxHeight, childHeight)
        }

        totalHeight = maxOf(
            height,
            offsetY + lineMaxHeight + paddingBottom + verticalScrollOffset
        )
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (!enableScroll || childCount == 0) return 0

        var travel = dy

        // 边界控制
        if (verticalScrollOffset + dy < 0) {
            travel = -verticalScrollOffset
        } else if (verticalScrollOffset + dy > totalHeight - height) {
            travel = totalHeight - height - verticalScrollOffset
        }

        verticalScrollOffset += travel
        offsetChildrenVertical(-travel)
        return travel
    }
}

class FlowLayoutParams : RecyclerView.LayoutParams {

    /** 是否在该 item 前强制换行 */
    var wrapBefore: Boolean = false

    constructor(c: Context, attrs: AttributeSet?) : super(c, attrs)
    constructor(width: Int, height: Int) : super(width, height)
    constructor(source: ViewGroup.LayoutParams) : super(source)
}