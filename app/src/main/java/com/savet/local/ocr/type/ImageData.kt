package com.savet.local.ocr.type


/**
 * 保存图像相关数据
 *
 */
class ImageData {

    var imageType :ImageType = ImageType.URI

    /**
     * 图像数据
     */
    var date : Any? = null

    /**
     * 图像的类型
     *
     */
    enum class ImageType {
        URI,
        BITMAP
    }
}