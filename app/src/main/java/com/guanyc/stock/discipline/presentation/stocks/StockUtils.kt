package com.guanyc.stock.discipline.presentation.stocks

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.theme.Blue
import com.guanyc.stock.discipline.theme.Green
import com.guanyc.stock.discipline.theme.Orange
import com.guanyc.stock.discipline.theme.Purple
import com.guanyc.stock.discipline.theme.Red

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