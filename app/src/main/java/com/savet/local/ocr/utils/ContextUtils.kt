package com.savet.local.ocr.utils

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
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

/**
 * 获取10分钟内最新的一张图片
 *
 * 需要android.permission.READ_EXTERNAL_STORAGE的权限
 *
 * @return 图片uri
 */
fun Context.getLatestImageUri(): Uri? {
    val contentResolver: ContentResolver = this.contentResolver

    val currentTimeInMillis = System.currentTimeMillis()
    val tenMinutesAgo = currentTimeInMillis - 10 * 60 * 1000  // 10分钟前的时间戳

    // 查询媒体库，获取最近 10 分钟内拍摄的最新照片
    val cursor = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_TAKEN),
        "${MediaStore.Images.Media.DATE_TAKEN} >= ?",
        arrayOf(tenMinutesAgo.toString()),
        "${MediaStore.Images.Media.DATE_TAKEN} DESC"  // 按拍摄时间降序排序
    )

    var imageUri: Uri? = null
    if (cursor != null && cursor.moveToFirst()) {
        val idColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
        val imageId = cursor.getLong(idColumnIndex)
        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId)
        cursor.close()
    }

    return imageUri
}