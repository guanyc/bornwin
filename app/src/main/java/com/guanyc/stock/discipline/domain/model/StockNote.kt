package com.guanyc.stock.discipline.domain.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey


//var x: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")


/**
 * 交易日志
 * 参考  onDelete = CASCADE, ref StockTarget 删除级联的关联对象
 */
@Entity(
    tableName = "stocknotes",
    indices = [Index(value=["createDate"], unique = true)]
)
data class StockNote(

    @Ignore
    var targetListVisible:Boolean = true,

    @PrimaryKey(autoGenerate = true)
    var stockNoteId: Long = 0L,

    /**
     * 日期 创建日期 仅取yyyyMMdd
     */
    var createDate: String = "",
    //仅仅有一个cerateDate为空的特殊的stocknote，用于创建特殊的tabentity

    var color: Int = 0,

    //所属的target 全部completed， isCompleted为true?
    var isCompleted: Boolean = false,

    var hasUnplannedAction: Boolean =false,

    /**
     * 盘后心得
     */
    var reviewMarket:String= "",




    /**
     * 大盘指数
     */
    //var indexShanghai: Double= 3000.0,



    //var indexShenzhen:Double = 10000.0,
    /**
     * 创业板指数
     */
    //var indexCreateTech: Double = 2000.0,



    /**
     *
     */
    //var hotSectionSurging: String = "信息板块",

    //var hotSectionDeSurging: String ="农业",

    /**
     * 仓位百分比，由仓位 当日操作计算得出
     */
    //var positionPercent: Float = 0f,

    /**
     * 盈亏比例
     */
    //var profitPercentage:Float = 0f,


    //timemmiles
    // var updatedDate: Long = 0L,


    /**
     * 明日计划
     */
    //var plan: String = "",

    /**
     * 对比昨日计划和昨日选出标的, 核对今日操作的复盘
     */
    //var review: String = "",




    /**
     * 题目?
     */
    //var title: String = "",


    /**
     * 盘中分析 -- 大盘分析
     */
    //var indexAnalysis: String = "",

    /**
     * 持仓个股分析
     */
    //var stockAnalysis1: String= "",
    //var stockAnalysis2: String= "",
    //var stockAnalysis3: String= "",


    //var stockTargetsJson: String = "",



    /**
     * 当日购买的操作
     */
    //var buyActions: List<StockOperation> = emptyList(),

    /**
     * 当日卖出的操作
     */
    //var sellActions: List<StockOperation> = emptyList(),

    /**
     * 当前仓位
     */
    //var holdingPositions: List<StockPosition> = emptyList(),

    /**
     * 复盘选出的自选标的
     */
    //var stockTargets: List<StockTarget> = emptyList(),

)