package com.guanyc.stock.discipline.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.guanyc.stock.discipline.presentation.main.components.TAB_TYPE
import com.guanyc.stock.discipline.presentation.main.components.TabEntity


val StockTarget.targetActionList: List<TabEntity>
    get() {
        return tabs.filter { it.tabType == TAB_TYPE.TAB_ACTION }
    }
val StockTarget.targetReasonList: List<TabEntity>
    get() {
        return tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
    }

val StockTarget.targetWatchListList: List<TabEntity>
    get() {
        return tabs.filter { it.tabType == TAB_TYPE.TAB_WATCHLIST }
    }

val StockTarget.targetSpecialListList: List<TabEntity>
    get() {
        return tabs.filter { it.tabType == TAB_TYPE.TAB_SPECIAL }
    }

val StockTarget.targetOtherListList: List<TabEntity>
    get() {
        return tabs.filter { it.tabType == TAB_TYPE.TAB_SPECIAL || it.tabType == TAB_TYPE.TAB_WATCHLIST }
    }


/**
 * @param stockNoteId
 */
@Entity(
    tableName = "targets",
    //indices = [Index(value=["code, createDate"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = StockNote::class,
            parentColumns = arrayOf("stockNoteId"),
            childColumns = arrayOf("stockNoteId"),
            onDelete = CASCADE,
            onUpdate = androidx.room.ForeignKey.NO_ACTION
        )
    ]
)

data class StockTarget(

    @PrimaryKey(autoGenerate = true)
    var stockTargetId: Long = 0,


    @ColumnInfo(name = "stockNoteId", index = true)
    var stockNoteId: Long = 0,

    //创建日期 和 stocknote的createDate是一样的，
    //统一日期下的 stocktarget属于相同的一个stocknote
    var createDate: String = "",


    //timemillies
    var updateDate: Long = 0L,

    // code of the stock, such as 000001 300015 600327 788234 880123
    var code: String = "",

    //name of the stock  such as 上海在线
    var name: String = "",


    /*
    @ColumnInfo(name = "sub_reasons")
    //var targetReasonList: List<TargetReasonMeta> = emptyList(),
    var targetReasonList: List<TabEntity> = emptyList(),


    @ColumnInfo(name = "sub_actions")
    //var targetActionList: List<TargetActionMeta> = emptyList(),
    var targetActionList: List<TabEntity> = emptyList(),

    @ColumnInfo(name = "sub_watchlist")
    var targetWatchListList: List<TabEntity> = emptyList(),
     */

    var tabs: List<TabEntity> = emptyList(),

    /**
     * 市场给予了操作机会，操作的理由 targetReasonList 之一
     */
    var targetReasonActed: String = "",

    /**
     * 市场给予了操作机会，实施的操作 targetActionList 之一
     */
    var targetActionActed: String = "",

    //var tabsPinned:List<TabEntity> = emptyList(),
    //var tabsTopped:List<TabEntity> = emptyList(),

    //var targetWatchListList: List<TargetWatchList> = emptyList(),

    var hasPosition: Boolean = false,

    // 持有股票的仓位
    var stockPosition: Double = 0.0,

    // 持有股票的仓位
    var stockPositionPercentage: Double = 0.0,

    // 持有股票的仓位
    var stockPositionValue: Double = 0.0,

    // 持有股票的仓位
    var stockPositionValuePercentage: Double = 0.0,

    // 持有股票的仓位
    var stockPositionValueChange: Double = 0.0,


    // 后续给机会了么， 如果没给机会的话, 这个targe就结束了?
    // @see isCompleted 设置为true ，
    // ？？后续还是标的的话 重新开始
    var isOpportunityGiven: Boolean = false,

    // 给机会 就应该买入/操作 真的操作了么？
    // 有可能排队没排上, 没操作的话 也结束了
    // 排队没排上和没操作一个效果
    var isPlanActed: Boolean = false,

    //FIXME
    //给了机会 操作了 是否成交了
    //var isOrderPlaced:Boolean = false,

    //FIXME 进行了操作的标的，
    //var reasonActed
    //var actionActed

    /**
     * 操作最终,获利情况 盈利true 亏损 false
     */
    var isProfitable: Boolean = false,

    /**
     * 获利亏损的比例
     */
    var profitOrLossPercentage: Double = 0.0,

    /**
     *
     * 反思  备注
     */
    var actionReview: String = "",

    /**
     * 是否已经完结
     */
    var isCompleted: Boolean = false,

    /**
     * 注释
     *
     */
    var comment: String = "",

    /**
     * 特别关注
     */
    var isFavorite: Boolean = false,

    @Ignore
    var isTopPinned: Boolean = false,

    @Ignore
    var isMoveTop: Boolean = false,

    )



