package com.savet.local.ocr.ui.manager

import android.content.Context
import com.google.android.flexbox.FlexboxLayoutManager

class ControlScrollLayoutManager(context: Context) : FlexboxLayoutManager(context) {

    var enableScroll = true

    override fun canScrollVertically(): Boolean {
        return enableScroll && super.canScrollVertically()
    }

    override fun canScrollHorizontally(): Boolean {
        return enableScroll && super.canScrollHorizontally()
    }
}