package com.savet.local.ocr.ui.info

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.savet.local.baselibrary.utils.LogUtils
import com.savet.local.ocr.BuildConfig
import com.savet.local.ocr.R
import com.savet.local.ocr.ui.dialog.UpdateDialog
import com.savet.local.ocr.utils.sendEMail
import com.savet.network.bean.GetReleaseResponse
import com.savet.network.service.Callback
import com.savet.network.service.GithubRequest


class InfoFragment : PreferenceFragmentCompat() {

    companion object {
        private const val TAG: String = "InfoFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_about)

        findPreference<Preference>("version_name")?.summary = BuildConfig.VERSION_NAME
        networkRequest()
    }

    private fun networkRequest() {
        LogUtils.d(TAG, "networkRequest : ReleasesLatest")
        GithubRequest.getReleasesLatest(object : Callback {
            override fun success(response: GetReleaseResponse) {
                LogUtils.d(TAG, "success, last version : ${response.tagName}")
                val result = compareVersion(BuildConfig.VERSION_NAME, response.tagName)
                when {
                    result < 0 -> {
                        UpdateDialog.show(
                            requireActivity(),
                            response.tagName,
                            updateDesc = response.body,
                            downloadUrl = response.htmlUrl
                        )
                    }
                    else -> println("无可更新 v1:${BuildConfig.VERSION_NAME} v2:${response.tagName} ")
                }
            }

            override fun failure(failCode: Int, errorMsg: String) {
                LogUtils.d(TAG, "failure : $errorMsg($failCode)")
            }

        })
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key == "send_email") {
            context?.sendEMail(getString(R.string.email))
            return true
        }
        return false
    }

    /**
     * 比较两个版本号，格式类似v1.26.041416
     * @param v1
     * @param v2
     * @return 比较结果
     * - 0 两个版本号一致
     * - 1 v1 > v2
     * - -1 v1 < v2
     */
    fun compareVersion(v1: String, v2: String): Int {
        fun parse(version: String): List<Int> {
            return version
                .replace(Regex("[^0-9.]"), "") // 去掉字母
                .split(".")
                .map { it.toIntOrNull() ?: 0 }
        }

        val list1 = parse(v1)
        val list2 = parse(v2)

        val maxSize = maxOf(list1.size, list2.size)

        for (i in 0 until maxSize) {
            val num1 = list1.getOrElse(i) { 0 }
            val num2 = list2.getOrElse(i) { 0 }

            if (num1 > num2) return 1
            if (num1 < num2) return -1
        }

        return 0
    }

}