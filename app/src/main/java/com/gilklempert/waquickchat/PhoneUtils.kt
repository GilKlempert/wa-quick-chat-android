package com.gilklempert.waquickchat

object PhoneUtils {
    fun formatNumber(raw: String): String {
        var n = raw.replace(Regex("[\\s\\-()]"), "")
        if (n.startsWith("+")) n = n.substring(1)
        if (n.startsWith("0")) n = "972" + n.substring(1)
        return n
    }

    fun displayNumber(num: String): String {
        if (num.startsWith("972") && num.length == 12) {
            val local = "0" + num.substring(3)
            return "${local.substring(0, 3)}-${local.substring(3)}"
        }
        return "+$num"
    }
}
