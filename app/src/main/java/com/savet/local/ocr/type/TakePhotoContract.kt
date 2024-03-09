package com.savet.local.ocr.type

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import androidx.core.content.contentValuesOf
import com.savet.local.baselibrary.utils.LogUtils
import com.savet.local.ocr.utils.DateUtils
import java.io.File

/**
 * 拍照协定
 *
 * [来源](https://blog.csdn.net/zhuyb829/article/details/122746281)
 */
class TakePhotoContract : ActivityResultContract<Unit?, Uri?>() {
    companion object {
        private const val TAG = "TakePhotoContract"
    }

    private var uri: Uri? = null

    override fun createIntent(context: Context, input: Unit?): Intent {
        val mimeType = "image/jpeg"
        val fileName = "IMG_${DateUtils.getCurrentFormatDate()}.jpg"
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上获取图片uri
            val values = contentValuesOf(
                Pair(MediaStore.MediaColumns.DISPLAY_NAME, fileName),
                Pair(MediaStore.MediaColumns.MIME_TYPE, mimeType),
                Pair(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            )
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            // Android 9 及以下获取图片uri
            FileProvider.getUriForFile(
                context, "${context.packageName}.provider",
                File(context.externalCacheDir, "/$fileName")
            )
        }
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        LogUtils.d(TAG, "Take photo, resultCode: $resultCode, uri: $uri")
        if (resultCode == Activity.RESULT_OK) return uri
        return null
    }
}
