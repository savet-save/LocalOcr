package com.savet.local.ocr

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.savet.local.baselibrary.utils.LogUtils
import com.savet.local.baselibrary.utils.ToastUtils
import com.savet.local.ocr.databinding.ActivityReadyBinding
import com.savet.local.ocr.utils.OcrUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class ReadyActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "ReadyActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityReadyBinding.inflate(layoutInflater).root)
    }

    override fun onStart() {
        super.onStart()
        // 进行一些初始化工作
        flow {
            // 初始化OCR识别引擎
            LogUtils.i(TAG, "init orc")
            OcrUtils.init(application) // 同步初始化，大约耗时700ms
            LogUtils.i(TAG, "end init orc")
            emit(true)
        }.flowOn(Dispatchers.Default)
            .onEach {
                startMainActivity()
            }.catch {
                ToastUtils.showToast("无法正常启动")
                it.printStackTrace()
            }.launchIn(lifecycleScope)
    }

    /**
     * 启动主活动
     *
     */
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 结束自身
    }
}