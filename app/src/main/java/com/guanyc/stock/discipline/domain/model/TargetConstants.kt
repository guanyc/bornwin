package com.guanyc.stock.discipline.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guanyc.stock.discipline.presentation.main.components.TAB_TYPE
import com.guanyc.stock.discipline.presentation.main.components.TabEntity


val TargetConstants.tabReasons: List<TabEntity>
    get() {
        return tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
    }

val TargetConstants.tabActions: List<TabEntity>
    get() {
        return tabs.filter { it.tabType == TAB_TYPE.TAB_ACTION }
    }
val TargetConstants.tabWatchList: List<TabEntity>
    get() {
        return tabs.filter { it.tabType == TAB_TYPE.TAB_WATCHLIST }
    }

val TargetConstants.tabSpecialList: List<TabEntity>
    get() {
        return tabs.filter { it.tabType == TAB_TYPE.TAB_SPECIAL }
    }

val TargetConstants.tabOtherList: List<TabEntity>
    get() {
        return tabs.filter { it.tabType == TAB_TYPE.TAB_SPECIAL || it.tabType == TAB_TYPE.TAB_WATCHLIST }
    }

@Entity(
    tableName = "targetholder",
    //indices = [Index(value=["createDate"], unique = true)]
)
data class TargetConstants(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String = "记录标地的一些常量",

    val description: String = "比如选择标的的原因，标的的后续操作等",

    @ColumnInfo(name = "tabs")
    val tabs: List<TabEntity> = emptyList()

)