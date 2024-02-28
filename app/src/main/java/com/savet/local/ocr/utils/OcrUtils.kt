package com.savet.local.ocr.utils

import android.app.Application
import android.graphics.Bitmap
import com.benjaminwan.ocrlibrary.OcrEngine
import com.benjaminwan.ocrlibrary.OcrResult
import com.savet.local.baselibrary.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.math.max

object OcrUtils {

    private const val TAG = "OcrUtils"

    // boxScoreThresh - 文本框置信度(得分阈值)，用于筛选出得分高于该阈值的文本框。[0.01, 1]，默认0.6f

    // boxThresh - 文本框二值化阈值，用于将神经网络输出的特征图进行二值化处理，以便进一步识别文本框的位置。
    //   值越大文本框越小，识别置信度约高,但是识别准确度会降低。[0.01, 1)，默认0.3f

    // doAngle - 是否启用文字方向检测。true则代表启用，默认启用

    // mostAngle - 是否启用角度投票。是否选择最可能的角度，可以加快检查时间。
    //   必须启用doAngle才能启用。true则代表启用，默认启用

    // padding - 缩影响识别时进行缩放的目标大小, 具体为 maxSideLen+padding*2，默认50

    // unClipRatio - 文本框大小倍率。值越大，文本框也越大。[1.0, 3.0]，默认2.0f

    lateinit var ocrEngine: OcrEngine

    /**
     * 初始化
     *
     * @param app 应用
     */
    fun init(app: Application) {
        ocrEngine = OcrEngine(app)
    }

    /**
     * 识别图像转换为文字。该方法为耗时操作，会阻塞当前线程
     *
     * @param img 图像数据
     * @return 识别的结果流，需要使用collect{}来接收
     */
    fun detect(img: Bitmap): OcrResult {
        // 输出的图像
        val boxImg: Bitmap = Bitmap.createBitmap(
            img.width, img.height, Bitmap.Config.ARGB_8888
        )

        val maxSize = max(img.width, img.height)
        // 识别时会缩放原始图像，目标大小 resize = maxSideLen+padding*2
        // 缩放是等比例的，缩放系数 = resize/长的边
        val maxSideLen = (1.0f * maxSize).toInt()

//            Logger.i("selectedImg=${img.height},${img.width} ${img.config}")
        val start = System.currentTimeMillis()

        // 开始识别
        val ocrResult = ocrEngine.detect(img, boxImg, maxSideLen)
        val end = System.currentTimeMillis()
        val time = "time=${end - start}ms"
        LogUtils.d(TAG, "detect use $time")
        return ocrResult
    }

    /**
     * 异步检查图像数据，返回一个flow
     *
     * @param img 图像数据
     */
    fun flowDetect(img: Bitmap) = flow<OcrResult> {
        emit(detect(img))
    }.flowOn(Dispatchers.IO)

}