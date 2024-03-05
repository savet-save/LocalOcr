package com.savet.local.ocr.ui.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.savet.local.ocr.databinding.ItemDetectResultBinding
import com.savet.local.ocr.ui.manager.ControlScrollLayoutManager
import com.savet.local.ocr.utils.dpToPx
import com.savet.local.ocr.utils.stopInertiaRolling
import kotlin.math.max
import kotlin.math.min

/**
 * 用于处理识别结果的RecyclerView适配器
 *
 * @property dataList 识别结果列表
 */
class DetectResultAdapter(private val dataList: List<AdapterData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG: String = "DetectResultAdapter"
        private const val FLUSH_SELECT: Int = 1
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

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val detectResultHolder = holder as DetectResultHolder
            detectResultHolder.setSelect(dataList[position].isSelect)
        }
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

    /**
     * 设置其元素的选中状态，并更新其显示状态
     *
     * @param position 元素所在位置
     * @param isSelect 是否被选中
     */
    fun setSelect(position: Int, isSelect: Boolean) {
        if (position < 0 || position >= dataList.size) {
            // 越界则不做处理
            return
        }
        if (dataList[position].isSelect != isSelect) {
            dataList[position].isSelect = isSelect
            notifyItemChanged(position, FLUSH_SELECT)
        }
    }

    /**
     * 设置选中状态取反，并更新其显示状态
     *
     * @param position 位置
     */
    fun setSelect(position: Int) {
        if (position < 0 || position >= dataList.size) {
            // 越界则不做处理
            return
        }
        setSelect(position, !dataList[position].isSelect)
    }

    /**
     * 获得指定项的选中状态
     *
     * @param position position
     * @return 选中状态
     */
    fun getSelect(position: Int): Boolean {
        if (position < 0 || position >= dataList.size) {
            // 越界则不做处理
            return false
        }
        return dataList[position].isSelect
    }

    /**
     * 获得所有选中项的内容
     *
     * @return 所有选中项的内容
     */
    fun getSelectContent() : String {
        val content : StringBuilder = java.lang.StringBuilder()
        dataList.forEach {
            if (it.isSelect) {
                content.append(it.text)
            }
        }
        return content.toString()
    }

    inner class DetectResultHolder(private val bind: ItemDetectResultBinding) :
        RecyclerView.ViewHolder(bind.root) {

        private val textView: TextView = bind.detectResultText

        fun bindData(data: AdapterData) {
            textView.text = data.text
            textView.isSelected = data.isSelect
            when (data.type) {
                // 进行实际换行操作[Type.RELINE]
                Type.RELINE -> {
                    val layoutParams = bind.root.layoutParams as FlexboxLayoutManager.LayoutParams
                    layoutParams.isWrapBefore = true
                }
                else -> {
//                    LogUtils.d(TAG, "$data")
                    // nothing
                }
            }
        }

        /**
         * 设置选中状态
         *
         * @param isSelect 是否选中
         */
        fun setSelect(isSelect: Boolean) {
            textView.isSelected = isSelect
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
                // 如果是没有获得，则返回
                return
            }

            // 根据类型设置间距
            when (parent.adapter?.getItemViewType(position)) {
                Type.INTERVAL.ordinal -> {
                    // 这里以像素设置左上右下间距
                    // 设置实际的间隔
                    outRect.set(0, 0, view.dpToPx(20f), 0)
                }
                else -> {
                    // 对于其他类型的项，以像素设置左上右下间距
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
     * @property isSelect 是否被选中
     */
    data class AdapterData(
        val text: String,
        val type: Type = Type.NORMAL,
        var isSelect: Boolean = false
    ) {
        override fun toString(): String {
            return "AdapterData(${text}_${type})"
        }
    }

    /**
     * 用于支持滑动选中功能
     *
     */
    class SelectListener : RecyclerView.OnItemTouchListener {
        companion object {
            /**
             * is [RecyclerView.NO_POSITION]
             */
            private const val NO_INDEX = RecyclerView.NO_POSITION
        }

        /**
         * 用于保存最后一个被划到的item索引，防止view在一次滑动时被多次处理
         */
        private var lastItemIndex = NO_INDEX

        /**
         * 用于保存获取到的item选中状态
         */
        private var selectStatus: Boolean? = null

        /**
         * 表示是否移动到其他item过
         */
        private var moveFlag: Boolean = false

        /**
         * 表示是否右改变item的选中状态
         */
        private var changeSelectStatus: Boolean = false

        /**
         * 处理触摸事件
         * @return Boolean  返回该触摸是否被拦截
         */
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            // 开始分发触摸事件
            when (e.action) {
                // 用户抬起手指后 置空lastView 防止内存泄漏以及回收拦截（防止第二次点击该view时无法响应事件）
                MotionEvent.ACTION_UP -> {
                    rv.findChildViewUnder(e.x, e.y)?.let { view ->
                        val newItemIndex = rv.getChildAdapterPosition(view)
                        if (newItemIndex != NO_INDEX // 需要是有效的
                            && newItemIndex == lastItemIndex // 按下和抬起是同一项
                            && !moveFlag
                        ) { // 且中间没有移动到其他项
                            (rv.adapter as DetectResultAdapter).setSelect(newItemIndex)
                            changeSelectStatus = true
                        }
                    }

                    if (changeSelectStatus) { // 有改变选中状态
                        rv.stopInertiaRolling() // 取消当前的惯性滑动
                        rv.requestLayout() // 修复部分情况下item显示不全的问题
                    }

                    // 允许滑动
                    (rv.layoutManager as ControlScrollLayoutManager).enableScroll = true

                    // 重置
                    resetFlag()
                }

                // 用户按下手指
                MotionEvent.ACTION_DOWN -> {
                    // 尝试获取其控件(可能为空)
                    rv.findChildViewUnder(e.x, e.y)?.let {
                        lastItemIndex = rv.getChildAdapterPosition(it)
                        if (lastItemIndex != NO_INDEX) {
                            val detectResultAdapter = (rv.adapter as DetectResultAdapter)
                            selectStatus = detectResultAdapter.getSelect(lastItemIndex)
                            // 禁止滑动
                            (rv.layoutManager as ControlScrollLayoutManager).enableScroll = false
                        }
                    }
                }

                // 用户手指滑动的时候 处理事务
                MotionEvent.ACTION_MOVE -> {

                    // 先获取当前手机所在的view
                    val view = rv.findChildViewUnder(e.x, e.y) ?: return false

                    // 获取当前view的所在项索引
                    val newItemIndex = rv.getChildAdapterPosition(view)
                    if (NO_INDEX == newItemIndex) {
                        return false
                    }

                    // 防止手机在一个view上来回摩擦时抖动
                    if (newItemIndex == lastItemIndex) {
                        return false
                    }

                    if (!moveFlag) {
                        moveFlag = true // 移动到了其他控件上
                    }

                    if (rv.adapter != null && selectStatus != null) {
                        val detectResultAdapter = rv.adapter as DetectResultAdapter
                        val maxIndex = max(lastItemIndex, newItemIndex)
                        val minIndex = min(lastItemIndex, newItemIndex)
                        for (i in (minIndex).rangeTo(maxIndex)) {
                            // 同步lastView的选则状态
                            detectResultAdapter.setSelect(i, !selectStatus!!)
                        }
                        if (maxIndex >= 0) {
                            changeSelectStatus = true
                        }
                        // 禁止滑动
                        (rv.layoutManager as ControlScrollLayoutManager).enableScroll = false
                    }

                    // 最后保存view的索引所在位置
                    lastItemIndex = newItemIndex

                    return false

                }
            }
            return false
        }

        private fun resetFlag() {
            selectStatus = null
            lastItemIndex = NO_INDEX
            moveFlag = false
            changeSelectStatus = false
        }

        // onInterceptTouchEvent 返回true后执行
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }

    }

}