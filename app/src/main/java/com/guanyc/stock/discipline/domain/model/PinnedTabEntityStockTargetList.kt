package com.guanyc.stock.discipline.domain.model

import androidx.room.Entity
import com.guanyc.stock.discipline.presentation.main.components.TabEntity

@Entity(
    tableName = "tab_pinned_list",
)
data class PinnedTabEntityStockTargetList(

    @androidx.room.PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tab: TabEntity,
    val pinnedList: List<StockTarget> = emptyList(),
)
