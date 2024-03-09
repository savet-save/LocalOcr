package com.savet.local.ocr.utils

import android.app.Application
import android.graphics.Bitmap
import com.benjaminwan.ocrlibrary.OcrEngine
import com.benjaminwan.ocrlibrary.OcrResult
import com.savet.local.baselibrary.utils.LogUtils
import com.savet.local.baselibrary.utils.PreferencesUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.math.max

object OcrUtils {

    private const val TAG = "OcrUtils"

    /**
     * 用于保存/读取SharePreferences中
     *
     * @property defValue
     */
    private enum class OcrMap(val defValue: Any) {
        BOX_SCORE_THRESH_F(0.6f),
        BOX_THRESH_F(0.3f),
        DO_ANGLE_B(true),
        MOST_ANGLE_B(true),
        PADDING_I(50),
        UN_CLIP_RATIO_F(2.0f),
        MAX_SIDE_LEN_RATIO_F(1f);
    }

    /**
     * 用于保存快速结果的相关参数
     *
     * @property value
     */
    enum class FastMap(val value: Any) {
        PADDING_I(25),
        MAX_SIDE_LEN_RATIO_F(0.5f),
        DO_ANGLE_B(false);
    }

    // boxScoreThresh - 文本框置信度(得分阈值)，用于筛选出得分高于该阈值的文本框。[0.01, 1]，默认0.6f

    // boxThresh - 文本框二值化阈值，用于将神经网络输出的特征图进行二值化处理，以便进一步识别文本框的位置。
    //   值越大文本框越小，识别置信度约高,但是识别准确度会降低。[0.01, 1]，默认0.3f

    // doAngle - 是否启用文字方向检测。true则代表启用，默认启用

    // mostAngle - 是否启用角度投票。是否选择最可能的角度，可以加快检查时间。
    //   必须启用doAngle才能启用。true则代表启用，默认启用

    // padding - 缩影响识别时进行缩放的目标大小, 具体为 maxSideLen+padding*2，默认50

    // unClipRatio - 文本框大小倍率。值越大，文本框也越大。[1.0, 3.0]，默认2.0f

    lateinit var ocrEngine: OcrEngine

    var initSuccess: Boolean = false

    /**
     * 缩影响识别时进行缩放的目标大小, 具体为 srcMaxLinePix * maxSideLenRatio + padding * 2
     */
    private var maxSideLenRatio: Float = 1.0f

    /**
     * 从SharePreferences读取数据到ocrEngine中
     */
    private fun loadFromPreferences() {
        setBoxScoreThresh(
            PreferencesUtils.getFloat(
                OcrMap.BOX_SCORE_THRESH_F.name,
                OcrMap.BOX_SCORE_THRESH_F.defValue as Float
            )
        )
        setBoxThresh(
            PreferencesUtils.getFloat(
                OcrMap.BOX_THRESH_F.name,
                OcrMap.BOX_THRESH_F.defValue as Float
            )
        )
        setDoAngle(
            PreferencesUtils.getBoolean(
                OcrMap.DO_ANGLE_B.name,
                OcrMap.DO_ANGLE_B.defValue as Boolean
            )
        )
        setMostAngle(
            PreferencesUtils.getBoolean(
                OcrMap.MOST_ANGLE_B.name,
                OcrMap.MOST_ANGLE_B.defValue as Boolean
            )
        )
        setMaxSideLenRatio(
            PreferencesUtils.getFloat(
                OcrMap.MAX_SIDE_LEN_RATIO_F.name,
                OcrMap.MAX_SIDE_LEN_RATIO_F.defValue as Float
            )
        )
        setPadding(
            PreferencesUtils.getInt(
                OcrMap.PADDING_I.name,
                OcrMap.PADDING_I.defValue as Int
            )
        )
        setUnClipRatio(
            PreferencesUtils.getFloat(
                OcrMap.UN_CLIP_RATIO_F.name,
                OcrMap.UN_CLIP_RATIO_F.defValue as Float
            )
        )
    }

    /**
     * 初始化，调用前需要先初始化[PreferencesUtils]
     *
     * @param app 应用
     */
    fun init(app: Application) {
        if (initSuccess) {
            return // 避免重复初始化
        }
        synchronized(OcrUtils::class.java) { // 避免多线程导致的异步
            if (initSuccess) {
                return // 双重锁
            }
            ocrEngine = OcrEngine(app)
            loadFromPreferences()
            initSuccess = true
        }
    }

    /**
     * 识别图像转换为文字。该方法为耗时操作，会阻塞当前线程
     *
     * @param img 图像数据
     * @return 识别的结果
     */
    fun detect(img: Bitmap): OcrResult {
        // 输出的图像
        val boxImg: Bitmap = Bitmap.createBitmap(
            img.width, img.height, Bitmap.Config.ARGB_8888
        )

        val fastDetect = BaseSettingUtils.getFastDetect();

        val maxSize = max(img.width, img.height)
        // 识别时会缩放原始图像，目标大小 resize = maxSideLen+padding*2
        // 缩放是等比例的，缩放系数 = resize/长的边
        val maxSideLen  = if (fastDetect) {
            (0.6 * maxSize). toInt()
        } else {
            (maxSideLenRatio * maxSize).toInt()
        }

        LogUtils.i("selectedImg=[h:${img.height},w:${img.width}] ${img.config}")
        val start = System.currentTimeMillis()

        // 开始识别
        val ocrResult = ocrEngine.detect(img, boxImg, maxSideLen)

        val end = System.currentTimeMillis()
        val time = "time=${end - start}ms"
        LogUtils.d(TAG, "detect use $time")
        return ocrResult
    }

    /**
     * 回收检测结果
     *
     * @param result OcrResult
     */
    fun recycleOcrResultBitmap(result: OcrResult) {
        if (!result.boxImg.isRecycled) {
            result.boxImg.recycle()
        }
    }

    /**
     * 设置文本框置信度(得分阈值)，用于筛选出得分高于该阈值的文本框。
     *
     * @param boxScoreThresh [0.01, 1]，默认0.6f
     */
    fun setBoxScoreThresh(boxScoreThresh: Float) {
        if (boxScoreThresh < 0.01f || boxScoreThresh > 1f) {
            LogUtils.i(TAG, "set invalid boxScoreThresh, ignore")
            return
        }
        if (ocrEngine.boxScoreThresh == boxScoreThresh) {
            return // 相同不做改变
        }
        ocrEngine.boxScoreThresh = boxScoreThresh
        PreferencesUtils.setFloat(OcrMap.BOX_SCORE_THRESH_F.name, boxScoreThresh)
    }

    /**
     * @see setBoxScoreThresh
     */
    fun getBoxScoreThresh(): Float {
        return ocrEngine.boxScoreThresh
    }

    /**
     * 设置文本框二值化阈值，用于将神经网络输出的特征图进行二值化处理，以便进一步识别文本框的位置。
     *
     * @param boxThresh 值越大文本框越小，识别置信度约高,但是识别准确度会降低。[0.01, 1]，默认0.3f
     */
    fun setBoxThresh(boxThresh: Float) {
        if (boxThresh < 0.01f || boxThresh > 1f) {
            LogUtils.i(TAG, "set invalid boxThresh, ignore")
            return
        }
        if (ocrEngine.boxThresh == boxThresh) {
            return // 相同不做改变
        }
        ocrEngine.boxThresh = boxThresh
        PreferencesUtils.setFloat(OcrMap.BOX_THRESH_F.name, boxThresh)
    }

    /**
     * @see setBoxThresh
     */
    fun getBoxThresh(): Float {
        return ocrEngine.boxThresh
    }

    /**
     * 是否启用文字方向检测。
     *
     * @param doAngle true则代表启用，默认启用
     */
    fun setDoAngle(doAngle: Boolean) {
        if (ocrEngine.doAngle == doAngle) {
            return // 相同不做改变
        }
        ocrEngine.doAngle = doAngle
        PreferencesUtils.setBoolean(OcrMap.DO_ANGLE_B.name, doAngle)
    }

    /**
     * @see setDoAngle
     */
    fun getDoAngle(): Boolean {
        return ocrEngine.doAngle
    }

    /**
     * 是否启用角度投票。是否选择最可能的角度，可以加快检查时间。必须启用doAngle才能启用。
     *
     * @param mostAngle true则代表启用，默认启用
     */
    fun setMostAngle(mostAngle: Boolean) {
        if (ocrEngine.mostAngle == mostAngle) {
            return // 相同不做改变
        }
        if (ocrEngine.doAngle) {
            ocrEngine.mostAngle = mostAngle
            PreferencesUtils.setBoolean(OcrMap.MOST_ANGLE_B.name, mostAngle)
        } else if (!mostAngle) {
            ocrEngine.mostAngle = false
            PreferencesUtils.setBoolean(OcrMap.MOST_ANGLE_B.name, false)
        } else {
            LogUtils.i(TAG, "set invalid mostAngle, ignore")
        }
    }

    /**
     * @see setMostAngle
     */
    fun getMostAngle(): Boolean {
        return ocrEngine.mostAngle
    }

    /**
     * 缩影响识别时进行缩放的目标大小, 具体为 maxSideLen+padding*2
     *
     * @param padding 默认50
     */
    fun setPadding(padding: Int) {
        if (ocrEngine.padding == padding) {
            return // 相同不做改变
        }
        ocrEngine.padding = padding
        PreferencesUtils.setInt(OcrMap.PADDING_I.name, padding)
    }

    /**
     * @see setPadding
     */
    fun getPadding(): Int {
        return ocrEngine.padding
    }

    /**
     * 文本框大小倍率。值越大，文本框也越大。
     *
     * @param unClipRatio [1.0, 3.0]，默认2.0f
     */
    fun setUnClipRatio(unClipRatio: Float) {
        if (unClipRatio < 1f || unClipRatio > 3f) {
            LogUtils.i(TAG, "set invalid unClipRatio, ignore")
            return
        }
        if (ocrEngine.unClipRatio == unClipRatio) {
            return // 相同不做改变
        }
        ocrEngine.unClipRatio = unClipRatio
        PreferencesUtils.setFloat(OcrMap.UN_CLIP_RATIO_F.name, unClipRatio)
    }

    /**
     * @see setUnClipRatio
     */
    fun getUnClipRatio(): Float {
        return ocrEngine.unClipRatio
    }

    /**
     * 设置图像的缩放比例
     *
     * @param maxSideLenRatio \[0.1, 1]，默认1
     */
    fun setMaxSideLenRatio(maxSideLenRatio: Float) {
        if (maxSideLenRatio < 0.1f || maxSideLenRatio > 1f) {
            LogUtils.i(TAG, "set invalid maxSideLenRatio, ignore")
            return
        }
        if (this.maxSideLenRatio == maxSideLenRatio) {
            return // 相同不做改变
        }
        this.maxSideLenRatio = maxSideLenRatio
        PreferencesUtils.setFloat(OcrMap.MAX_SIDE_LEN_RATIO_F.name, maxSideLenRatio)
    }

    /**
     * @see setMaxSideLenRatio
     */
    fun getMaxSideLenRatio(): Float {
        return this.maxSideLenRatio
    }

    /**
     * 重置所有恢复到到默认值
     */
    fun resetAll() {
        setBoxScoreThresh(OcrMap.BOX_SCORE_THRESH_F.defValue as Float)
        setBoxThresh(OcrMap.BOX_THRESH_F.defValue as Float)
        setDoAngle(OcrMap.DO_ANGLE_B.defValue as Boolean)
        setMostAngle(OcrMap.MOST_ANGLE_B.defValue as Boolean)
        setMaxSideLenRatio(OcrMap.MAX_SIDE_LEN_RATIO_F.defValue as Float)
        setPadding(OcrMap.PADDING_I.defValue as Int)
        setUnClipRatio(OcrMap.UN_CLIP_RATIO_F.defValue as Float)
    }

}