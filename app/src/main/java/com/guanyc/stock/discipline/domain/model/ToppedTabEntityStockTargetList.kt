package com.guanyc.stock.discipline.domain.model

import androidx.room.Entity
import com.guanyc.stock.discipline.presentation.main.components.TabEntity

@Entity(
    tableName = "tab_topped_list",
)
data class ToppedTabEntityStockTargetList(

    @androidx.room.PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tab: TabEntity,
    val toppedList: List<StockTarget> = emptyList(),
)
