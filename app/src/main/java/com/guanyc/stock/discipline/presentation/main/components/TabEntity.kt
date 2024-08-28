package com.guanyc.stock.discipline.presentation.main.components

enum class TAB_TYPE(val source: Int) {
    TAB_ACTION(source = 0), //00000
    TAB_REASON(source = 1), //10000
    TAB_WATCHLIST(source = 2), //20000
    TAB_SPECIAL(source = 3)  //30000
}


data class TabEntity(
    val title: String = "",
    val note: String = "",
    val enabled: Boolean = true,
    val tabType: TAB_TYPE = TAB_TYPE.TAB_WATCHLIST,
    val isSpecial: Boolean = false,
)



