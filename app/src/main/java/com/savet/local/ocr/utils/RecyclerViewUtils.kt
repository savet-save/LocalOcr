package com.savet.local.ocr.utils

import androidx.recyclerview.widget.RecyclerView

/**
 * 通过反射停止惯性滚动
 *
 */
fun RecyclerView.stopInertiaRolling() {
    try {
        // 如果是Support的RecyclerView则需要使用"cancelTouch"
        val field = this.javaClass.getDeclaredMethod("cancelScroll")
        field.isAccessible = true
        field.invoke(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
