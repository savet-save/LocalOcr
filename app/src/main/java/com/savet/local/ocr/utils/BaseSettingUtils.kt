package com.savet.local.ocr.utils

import com.savet.local.baselibrary.utils.PreferencesUtils

object BaseSettingUtils {
    enum class Item(val defValue: Any) {
        AUTO_LOAD_IMAGE(true),
        FAST_DETECT(true),
        OPEN_ADVANCED_SETTING(false);
    }

    /**
     * 设置是否自动载入图像
     *
     * @param enable true - 启用
     */
    fun setAutoLoadImage(enable: Boolean) {
        PreferencesUtils.setBoolean(Item.AUTO_LOAD_IMAGE.name, enable)
    }

    /**
     * @see setAutoLoadImage
     */
    fun getAutoLoadImage() : Boolean {
        return PreferencesUtils.getBoolean(Item.AUTO_LOAD_IMAGE.name, Item.AUTO_LOAD_IMAGE.defValue as Boolean)
    }

    /**
     * 设置是否启用快速识别
     *
     * @param enable true - 启用
     */
    fun setFastDetect(enable: Boolean) {
        PreferencesUtils.setBoolean(Item.FAST_DETECT.name, enable)
    }

    /**
     * @see setFastDetect
     */
    fun getFastDetect() : Boolean {
        return PreferencesUtils.getBoolean(Item.FAST_DETECT.name, Item.FAST_DETECT.defValue as Boolean)
    }

    /**
     * 高级选项是否可以修改
     *
     * @param enable true - 启用
     */
    fun setOpenAdvancedSetting(enable: Boolean) {
        PreferencesUtils.setBoolean(Item.OPEN_ADVANCED_SETTING.name, enable)
    }

    /**
     * @see setOpenAdvancedSetting
     */
    fun getOpenAdvancedSetting() : Boolean {
        return PreferencesUtils.getBoolean(Item.OPEN_ADVANCED_SETTING.name, Item.OPEN_ADVANCED_SETTING.defValue as Boolean)
    }


    /**
     * 重置所有到默认值
     *
     */
    fun resetAll() {
        setAutoLoadImage(Item.AUTO_LOAD_IMAGE.defValue as Boolean)
        setFastDetect(Item.FAST_DETECT.defValue as Boolean)
        setOpenAdvancedSetting(Item.OPEN_ADVANCED_SETTING.defValue as Boolean)
    }

}