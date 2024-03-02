package com.savet.local.ocr.utils

import android.content.Context
import android.util.TypedValue
import android.view.View

/**
 * dp转换为像素单位
 *
 * @param dp dp单位
 * @return 对应像素单位值
 */
fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    ).toInt()
}

/**
 * 像素转换为dp单位
 *
 * @param px 像素单位
 * @return 对应dp值
 */
fun Context.pxToDp(px: Int): Float {
    val density = resources.displayMetrics.density
    return px / density
}


/**
 * dp转换为像素单位
 *
 * @param dp dp单位
 * @return 对应像素单位值
 */
fun View.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}

/**
 * 像素转换为dp单位
 *
 * @param px 像素单位
 * @return 对应dp值
 */
fun View.pxToDp(px: Int): Float {
    val density = context.resources.displayMetrics.density
    return px / density
}
