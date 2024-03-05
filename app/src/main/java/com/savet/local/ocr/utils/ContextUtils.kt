package com.savet.local.ocr.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * 检查指定的权限是否获取
 *
 * @param permissions 权限，如[Manifest.permission.READ_EXTERNAL_STORAGE]
 * @return
 */
fun Context.isAllGranted(vararg permissions: String): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}