package com.savet.local.ocr.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benjaminwan.ocrlibrary.OcrResult
import com.savet.local.baselibrary.LogUtils
import com.savet.local.ocr.ui.adapter.DetectResultAdapter
import com.savet.local.ocr.utils.splitWord

class GalleryViewModel : ViewModel() {

    companion object {
        private const val TAG: String = "GalleryViewModel"
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    /**
     * 获得需要显示的数据数组
     *
     * @param ocrResult OCR识别结果
     * @return 用于显示的数据数组
     */
    fun getDetectAdapterDateList(ocrResult: OcrResult): ArrayList<DetectResultAdapter.AdapterData> {
        val resultList: ArrayList<DetectResultAdapter.AdapterData> = ArrayList()
        var maxYInLine = -1 // 左上角(0, 0)
        var needReline = false;
        // 英文和数字进行分词, 其他进行分字操作
        ocrResult.textBlocks.forEachIndexed { allResultIndex, tb ->
            // 先判断是否需要换行显示
            if (tb.boxPoint[0].y > maxYInLine) { // 判断左上点的左边y值
                maxYInLine = tb.boxPoint[2].y // 保存右下角的左边y值
                if (allResultIndex != 0) {
                    // 设置需要换行
                    needReline = true
                }
            }
            // 添加分割后的普通元素
            val splitWord = tb.text.splitWord()
            splitWord.forEachIndexed { splitWordIndex, text ->
                if (needReline) {
                    // 设置为换行类型, 强制换行来显示该元素
                    resultList.add(
                        DetectResultAdapter.AdapterData(
                            text,
                            DetectResultAdapter.Type.RELINE
                        )
                    )
                    needReline = false
                    LogUtils.d(TAG, "add RELINE : $text-$splitWordIndex-$allResultIndex")
                } else if (splitWordIndex != (splitWord.size - 1)) {
                    // 普通类型
                    resultList.add(DetectResultAdapter.AdapterData(text))
//                    LogUtils.d(TAG, "add NORMAL : $text-$splitWordIndex")
                } else {
                    // 设置为间隔类型，用于保持大一点的间隔
                    resultList.add(
                        DetectResultAdapter.AdapterData(
                            text,
                            DetectResultAdapter.Type.INTERVAL
                        )
                    )
                    LogUtils.d(TAG, "add INTERVAL : $text-$splitWordIndex")
                }
            }
        }
        return resultList
    }
}