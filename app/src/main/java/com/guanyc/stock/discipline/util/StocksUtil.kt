package com.guanyc.stock.discipline.util

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.theme.Blue
import com.guanyc.stock.discipline.theme.Green
import com.guanyc.stock.discipline.theme.Orange
import com.guanyc.stock.discipline.theme.Purple
import com.guanyc.stock.discipline.theme.Red

/**
 * 检查给定字符串是否包含6位数字的股票代码。
 *
 * 该函数通过应用正则表达式来判断字符串中是否包含恰好6位数字的序列，
 * 这种序列通常用于表示股票代码。函数返回一个布尔值，表示是否找到这样的序列。
 *
 * @param item 待检查的字符串
 * @return 如果字符串中包含6位数字的股票代码序列，则返回true；否则返回false。
 */
fun containsStockCode(item: String): Boolean {
    // 使用正则表达式 "\\d{6}" 检查字符串中是否包含6位数字的序列
    return item.contains(regex = "\\d{6}".toRegex())
}

fun getStockCode(code: String): String {

    if (code.length == 6) return code
    var index = 0;
    while (index <= code.length - 6) {
        //item.contains("\\d{6}".toRegex())
        if (code.substring(index, index + 6).matches(("\\d{6}".toRegex()))) {
            return code.substring(index, index + 6)
        }
        index++
    }
    return code
}

/**
 * 提取股票名称。
 *
 * 此函数假设物品标识与股票名称之间存在一种映射关系。它被设计为私有函数，说明它应该是类内部的辅助函数，不打算被类外部直接调用。
 *
 * @param item 物品的标识，预计此标识能够唯一映射到一个股票名称。
 * @return 根据物品标识返回对应的股票名称。如果无法映射到股票名称，则应返回一个默认值或抛出异常。
 */

fun getStockName(item: String): String {
    var dots = ".-》:#,， /|1234567890。-》：# "

    var iwnp = item
    while (iwnp.isNotEmpty() && dots.contains(iwnp.get(0))) {
        iwnp = iwnp.substring(1)
    }

    if (iwnp.contains("ST", ignoreCase = false)) {
        return iwnp.substring(iwnp.indexOf("ST"))
    }

    if (iwnp.contains("ETF", ignoreCase = false)) {
        return iwnp.substringBeforeLast("ETF") + "ETF"
    }

    if (iwnp.contains("指数", ignoreCase = false)) {
        return iwnp.substringBeforeLast("指数") + "指数"
    }

    if (iwnp.length <= 4) return iwnp
    else
    //TODO
    // part match "([\u4e00-\u9fa5]|\\w){3,10}"
        return iwnp//
}

private fun isStockName(item: String): Boolean {


    val trim = item.trim()

    if (trim.isEmpty()) return false

    if (trim.equals("柳工") || trim.equals("柳  工")) return true

    if (item.contains("ETF", ignoreCase = false)) return true
    if (item.contains("指数", ignoreCase = false)) return true
    if (item.contains("ST", ignoreCase = false)) return true

    if (containsStockCode(item)) return false

    var dots = ".-》:#,， /|1234567890"

    var p = item
    while (p.isNotEmpty() && dots.contains(p.get(0))) {
        p = p.substring(1)
    }


    if (p.contains("[.-》:#,， /]".toRegex())) return false

    if (p.matches("([\u4e00-\u9fa5]|\\w){3,10}".toRegex())) return true

    return false
}

enum class DialogActionEnum(val value: Int, val action: String) {
    CANCEL(0, "取消"), EDIT(1, "修改"), DELETE(2, "删除"),
}

enum class TargetActionEnumDeleted(val value: Int, val action: String) {
    FIRST(0, "接力"),
    SECOND(1, "放量接力"),
    THIRD(2, "低吸"),
    FOURTH(3, "打板"),
    SIXTH(4, "小时"),
}

enum class StockTargetPeriod(@StringRes val title: Int, val color: Color) {
    WEEK(R.string.stocktarget_period_week, Red),

    MONTH(
        R.string.stocktarget_period_month, Orange
    ),
    QUARTER(R.string.stocktarget_period_quarter, Green),

    YEAR(
        R.string.stocktarget_period_year, Blue
    ),
    CUSTOMIZED(R.string.stocktarget_period_custom, Purple),
}

fun Int.toStockTargetPeriod(): StockTargetPeriod {
    return when (this) {
        0 -> StockTargetPeriod.WEEK
        1 -> StockTargetPeriod.MONTH
        2 -> StockTargetPeriod.QUARTER
        3 -> StockTargetPeriod.YEAR
        4 -> StockTargetPeriod.CUSTOMIZED
        else -> StockTargetPeriod.WEEK

    }
}

fun StockTargetPeriod.toInt(): Int {
    return when (this) {
        StockTargetPeriod.WEEK -> 0
        StockTargetPeriod.MONTH -> 1
        StockTargetPeriod.QUARTER -> 2
        StockTargetPeriod.YEAR -> 3
        StockTargetPeriod.CUSTOMIZED -> 4
    }
}