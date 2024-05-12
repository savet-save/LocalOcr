package com.savet.local.ocr.ui.info

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.savet.local.baselibrary.utils.LogUtils
import com.savet.local.ocr.BuildConfig
import com.savet.local.ocr.R
import com.savet.local.ocr.utils.sendEMail
import com.savet.network.BaseCallback
import com.savet.network.bean.GetReleaseResponse
import com.savet.network.manager.ApiManager
import com.savet.network.service.Callback
import com.savet.network.service.GithubApi
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
        GithubRequest.getReleasesLatest(object  : Callback {
            override fun success(response: GetReleaseResponse) {
                LogUtils.d(TAG, "success : ${response.tag_name}")
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

}