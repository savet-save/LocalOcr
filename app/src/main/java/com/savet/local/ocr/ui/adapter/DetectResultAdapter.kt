package com.savet.local.ocr.ui.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.savet.local.ocr.databinding.ItemDetectResultBinding
import com.savet.local.ocr.utils.dpToPx

/**
 * 用于处理识别结果的RecyclerView适配器
 *
 * @property dataList 识别结果列表
 */
class DetectResultAdapter(private val dataList: List<AdapterData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG: String = "DetectResultAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemDetectResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DetectResultHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val detectResultHolder = holder as DetectResultHolder
        detectResultHolder.bindData(dataList[position])
    }

    /**
     * 根据类型的序列进行分类
     *
     * @param position 位置
     * @return 类型
     */
    override fun getItemViewType(position: Int): Int {
        if (position < 0 || position >= dataList.size) {
            // 默认返回普通类型
            return Type.NORMAL.ordinal
        }
        return dataList[position].type.ordinal
    }

    inner class DetectResultHolder(private val bind: ItemDetectResultBinding) :
        RecyclerView.ViewHolder(bind.root) {

        private val textView: TextView = bind.detectResultText

        fun bindData(data: AdapterData) {
            textView.text = data.text
            when (data.type) {
                // 进行实际换行操作[Type.RELINE]
                Type.RELINE -> {
                    val layoutParams = bind.root.layoutParams as FlexboxLayoutManager.LayoutParams
                    layoutParams.isWrapBefore = true
//                    layoutParams.flexGrow = 1F
                    // 刷新布局
//                    itemView.requestLayout()
                }
                else -> {
//                    LogUtils.d(TAG, "$data")
                    // nothing
                }
            }
        }
    }

    /**
     * 装饰器，用于实现间隔[Type.INTERVAL]类型
     *
     */
    class DetectItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            // 获取当前项的位置
            val position = parent.getChildAdapterPosition(view)
            if (position == RecyclerView.NO_POSITION) {
                return
            }

            // 根据类型设置间距
            when (parent.adapter?.getItemViewType(position)) {
                Type.INTERVAL.ordinal -> {
                    // 这里以像素设置上下左右间距
                    // 设置实际的间隔
                    outRect.set(0, 0, view.dpToPx(20f), 0)
                }
                else -> {
                    // 对于其他类型的项，也可以设置间距，或者留空不处理
                    outRect.set(0, 0, 0, 0)
                }
            }
        }
    }


    /**
     * 元素类型
     */
    enum class Type {
        /**
         * 普通类型
         */
        NORMAL,

        /**
         * 换行类型
         */
        RELINE,

        /**
         * 间隔类型
         *
         */
        INTERVAL
    }

    /**
     * 检查结果Adapter使用的数据类型
     *
     * @property text 显示的文本
     * @property type 元素类型，默认普通类型
     */
    data class AdapterData(val text: String, val type: Type = Type.NORMAL) {
        override fun toString(): String {
            return "AdapterData(${text}_${type})"
        }
    }

}