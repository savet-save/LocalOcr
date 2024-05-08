package com.savet.local.ocr.base

import androidx.fragment.app.Fragment

/**
 * 全局通用的基类fragment
 */
abstract class BaseFragment : Fragment(), BackPressedListener {
    /**
     * @see BackPressedListener.handleBackPressed
     */
    override fun handleBackPressed(): Boolean {
        //默认不响应
        return false
    }
}

/**
 * 监听activity的onBackPress事件
 */
interface BackPressedListener {
    /**
     * @return true代表响应back键点击，false代表不响应
     */
    fun handleBackPressed(): Boolean
}