package com.savet.local.ocr.utils

/**
 * 将中文所有单词分为单独的字，英文和数字分为连续的字符串，所有可见符号单独的符号(ascii码大于0x20)成为单独字符串。
 *
 * 如下所示
 * ```
 * "你好savet-save,欢迎购买TV show服务! 你需要花费29元，请支付".splitWord()
 * ```
 * 输出
 * ```
 * [你, 好, savet, -, save, ,, 欢, 迎, 购, 买, TV, show, 分, 为, !, 你, 需, 要, 花, 费, 29, 元, ，, 请, 支, 付]
 * ```
 *
 * @return 分割好的List
 */
fun String.splitWord(): List<String> {
    val characters: MutableList<String> = mutableListOf()
    var i = 0
    val num = this.length
    while (i < num) {
        // 基本字母
        if (this[i].code <= 0x7F) {
            if (this[i].code <= ' '.code) {
                // 不可见字符忽略(空格和之前的)
                i++
                continue
            }
            val sb: StringBuilder = java.lang.StringBuilder()
            // 数字
            if (this[i].isDigit() || this[i].isAsciiLetter()) {
                do {
                    sb.append(this[i])
                    i++
                } while (i < num && (this[i].isDigit() || this[i].isAsciiLetter()))
                characters.add(sb.toString())
            } else {
                // 其他可见字符
                characters.add(this[i].toString())
                i++
            }

        } else {
            characters.add(this[i].toString())
            i++ // +1
        }
    }
    return characters
}

/**
 * 判断是否是a-z或A-Z字符
 *
 * @return true - 是, false - 不是
 */
fun Char.isAsciiLetter(): Boolean {
    return (this.code >= 'a'.code && this.code <= 'z'.code) ||
            (this.code >= 'A'.code && this.code <= 'Z'.code)
}
