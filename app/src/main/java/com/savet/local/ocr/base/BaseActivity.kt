package com.savet.local.ocr.base

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.savet.local.baselibrary.utils.LogUtils

open class BaseActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "BaseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 返回按键监听
        onBackPressedDispatcher.addCallback(this, onBackPressed)
    }

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (interceptBackPressed()) {
                LogUtils.d(TAG, "back pressed interrupt")
            } else {
                LogUtils.d(TAG, "finish")
                this@BaseActivity.finish()
            }
        }
    }


    /**
     * 拦截返回按键
     * @return true - 表示处理完成， false - 表示未处理完成
     */
    private fun interceptBackPressed(): Boolean {
        return dealWithBackPressed(supportFragmentManager.fragments)
    }

    /**
     * 处理返回按键
     * @return true - 表示处理完成， false - 表示未处理完成
     */
    private fun dealWithBackPressed(fragmentList: List<Fragment>): Boolean {
        val iterator = fragmentList.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            LogUtils.d(TAG, "hasNext ${next is BackPressedListener}, $next")
            if (next is BackPressedListener && next.handleBackPressed()) {
                return true
            }
            // 处理如NavHostFragment这种本身是Fragment容器的情况
            if (next.childFragmentManager.fragments.isNotEmpty()) {
                // 如果需要拦截就拦截
                if (dealWithBackPressed(next.childFragmentManager.fragments)) return true
                else {
                    // 处理是NavHostFragment的情况
                    if (next is NavHostFragment) {
                        return next.navController.popBackStack()
                    }
                }
            }

        }
        return false
    }


}