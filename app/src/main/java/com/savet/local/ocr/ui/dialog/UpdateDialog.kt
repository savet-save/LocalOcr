package com.savet.local.ocr.ui.dialog

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.savet.local.ocr.R

object UpdateDialog {

    fun show(
        context: Context,
        versionName: String,
        downloadUrl: String = "",
        updateDesc: String? = ""
    ) {
        val message = context.getString(R.string.find_new_version, versionName, updateDesc).trimIndent()

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.new_version_available))
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(context.getString(R.string.update_now)) { _, _ ->
                openUrl(context, downloadUrl)
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }


    private fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.update_fail), Toast.LENGTH_SHORT).show()
        }
    }
}