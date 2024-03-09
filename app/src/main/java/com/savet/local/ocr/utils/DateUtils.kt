package com.savet.local.ocr.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    /**
     * 获得当前格式化时间
     *
     * @return 如 2024_03_09_10_35_22
     */
    fun getCurrentFormatDate(): String {
        val currentTime = Date()
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        return sdf.format(currentTime)
    }
}