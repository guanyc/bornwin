package com.guanyc.stock.discipline.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.PinnedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.model.ToppedTabEntityStockTargetList
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockTargetDao {
    @Query("SELECT * FROM targets WHERE  isCompleted = 0 ")
    fun getAllUncompletedStockTargets(): Flow<List<StockTarget>>


    @Query("update targets set isFavorite = :favorite where stockTargetId = :stockTargetId")
    fun updateStockTargetFavorite(stockTargetId: Long, favorite: Boolean)

    @Query("update targets set isCompleted = :isCompleted where stockTargetId = :stockTargetId")
    fun updateStockTargetCompleted(stockTargetId: Long, isCompleted: Boolean)

    @Query("update targets set comment = :comment where stockTargetId = :stockTargetId")
    fun updateStockTargetComment(stockTargetId: Long, comment: String)

    @Update
    fun updateStockTargetList(stockTargetList: List<StockTarget>)



    @Update
    fun UpdateTabPinnedStockTargetList(pinnedTabEntityStockTargetList: PinnedTabEntityStockTargetList)

    @Insert
    fun insertTabPinnedStockTargetList(pinnedTabEntityStockTargetList: PinnedTabEntityStockTargetList)

    fun insertTabPinnedStockTargetList(tabEntity: TabEntity, stockTargetList: List<StockTarget>){
        val pinnedTabEntityStockTargetList = PinnedTabEntityStockTargetList(
            tab = tabEntity,
            pinnedList = stockTargetList
        )
        insertTabPinnedStockTargetList(pinnedTabEntityStockTargetList)
    }

    @Update
    fun UpdateTabToppedStockTargetList(toppedTabEntityStockTargetList: ToppedTabEntityStockTargetList)

    @Insert
    fun insertTabToppedStockTargetList(toppedTabEntityStockTargetList: ToppedTabEntityStockTargetList)

    fun insertTabToppedStockTargetList(tabEntity: TabEntity, stockTargetList: List<StockTarget>){
        val toppedTabEntityStockTargetList = ToppedTabEntityStockTargetList(
            tab = tabEntity,
            toppedList = stockTargetList
        )
        insertTabToppedStockTargetList(toppedTabEntityStockTargetList)
    }


    @Query("SELECT * FROM tab_pinned_list ")
    fun getAllPinnedTabEntityStockTargetList(): Flow<List<PinnedTabEntityStockTargetList>>


    @Query("SELECT * FROM tab_topped_list ")
    fun getAllToppedTabEntityStockTargetList(): Flow<List<ToppedTabEntityStockTargetList>>

    @Update
    fun updateStockTargetTabEntities(stockTarget: StockTarget)
}