package com.guanyc.stock.discipline.domain.repository
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.PinnedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.model.ToppedTabEntityStockTargetList
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import kotlinx.coroutines.flow.Flow

interface StockTargetRepository {
    abstract fun getAllUncompletedStockTargets(): Flow<List<StockTarget>>
    suspend fun updateStockTargetFavorite(stockTarget: StockTarget, favorite: Boolean)
    abstract suspend fun updateStockTargetCompleted(stockTarget: StockTarget)
    abstract suspend fun updateStockTargetComment(stockTarget: StockTarget)

    suspend fun updateStockTargetList(stockTargetList: List<StockTarget>)



    fun getAllPinnedTabEntityStockTargetList(): Flow<List<PinnedTabEntityStockTargetList>>
    suspend fun updateTabPinnedStockTargetList(pinned: PinnedTabEntityStockTargetList)
    suspend fun insertTabPinnedStockTargetList(pinned: PinnedTabEntityStockTargetList)


    fun getAllToppedTabEntityStockTargetList(): Flow<List<ToppedTabEntityStockTargetList>>
    suspend fun updateTabToppedStockTargetList(topped: ToppedTabEntityStockTargetList)
    suspend fun insertTabToppedStockTargetList(topped: ToppedTabEntityStockTargetList)
    suspend fun updateStockTargetTabEntities(
        stockTarget: StockTarget,
        tabOld: List<TabEntity>,
        tabNew: List<TabEntity>,
        selectedIndex: Int
    )

}
