package com.savet.local.ocr.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream

/**
 * 用于便捷操作资产文件
 * @author savet
 * @date 2024/2/28
 */
object AssetsUtils {

    /**
     * 打开资产中的图像文件，如果成功则获得对应的Bitmap数据
     *
     * @param context context
     * @param filePath 文件路径
     * @return Bitmap数据
     */
    fun getBitmapFromAsset(context: Context, filePath: String): Bitmap? {
        var inputStream: InputStream? = null
        var bitmap: Bitmap? = null

        try {
            inputStream = context.assets.open(filePath)
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return bitmap
    }
}