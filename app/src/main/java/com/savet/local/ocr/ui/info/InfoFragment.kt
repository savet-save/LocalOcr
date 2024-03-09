package com.savet.local.ocr.ui.info

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.savet.local.ocr.BuildConfig
import com.savet.local.ocr.R
import com.savet.local.ocr.utils.sendEMail


class InfoFragment : PreferenceFragmentCompat() {

    companion object {
        private const val TAG: String = "InfoFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_about)

        findPreference<Preference>("version_name")?.summary = BuildConfig.VERSION_NAME

    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key == "send_email") {
            context?.sendEMail(getString(R.string.email))
            return true
        }
        return false
    }

}