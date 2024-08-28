package com.guanyc.stock.discipline.data.repository

import com.guanyc.stock.discipline.data.local.dao.StockTargetDao
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.PinnedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.model.ToppedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.repository.StockTargetRepository
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StockTargetRepositoryImpl (
    private val stockTargetDao: StockTargetDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StockTargetRepository {
    override fun getAllUncompletedStockTargets(): Flow<List<StockTarget>> {
       return  stockTargetDao.getAllUncompletedStockTargets()
    }

    override suspend fun updateStockTargetFavorite(stockTarget: StockTarget, favorite: Boolean) {
        return withContext(ioDispatcher) {
            stockTargetDao.updateStockTargetFavorite(stockTarget.stockTargetId, favorite)
        }
    }

    override suspend fun updateStockTargetCompleted(stockTarget: StockTarget) {
        return withContext(ioDispatcher) {
            stockTargetDao.updateStockTargetCompleted(stockTarget.stockTargetId, true)
        }
    }

    override suspend fun updateStockTargetComment(stockTarget: StockTarget) {
        return withContext(ioDispatcher) {
            stockTargetDao.updateStockTargetComment(stockTarget.stockTargetId, stockTarget.comment)
        }
    }

    override suspend fun updateStockTargetList(stockTargetList: List<StockTarget>) {
        return withContext(ioDispatcher) {
            stockTargetDao.updateStockTargetList(stockTargetList)
        }
    }

    override fun getAllPinnedTabEntityStockTargetList(): Flow<List<PinnedTabEntityStockTargetList>> {
        return  stockTargetDao.getAllPinnedTabEntityStockTargetList()
    }

    override suspend fun updateTabPinnedStockTargetList(pinnedTabEntityStockTargetList: PinnedTabEntityStockTargetList) {
        return withContext(ioDispatcher) {
            stockTargetDao.UpdateTabPinnedStockTargetList(pinnedTabEntityStockTargetList)
        }
    }

    override suspend fun insertTabPinnedStockTargetList(pinnedTabEntityStockTargetList: PinnedTabEntityStockTargetList) {
        return withContext(ioDispatcher) {
            stockTargetDao.insertTabPinnedStockTargetList(pinnedTabEntityStockTargetList)
        }
    }

    override fun getAllToppedTabEntityStockTargetList(): Flow<List<ToppedTabEntityStockTargetList>> {
        return  stockTargetDao.getAllToppedTabEntityStockTargetList()
    }

    override suspend fun updateTabToppedStockTargetList(topped: ToppedTabEntityStockTargetList) {
        return withContext(ioDispatcher) {
            stockTargetDao.UpdateTabToppedStockTargetList(topped)
        }
    }

    override suspend fun insertTabToppedStockTargetList(topped: ToppedTabEntityStockTargetList) {
        return withContext(ioDispatcher) {
            stockTargetDao.insertTabToppedStockTargetList(topped)
        }
    }

    override suspend fun updateStockTargetTabEntities(
        stockTarget: StockTarget,
        tabOld: List<TabEntity>,
        tabNew: List<TabEntity>,
        selectedIndex: Int
    ) {
        return withContext(ioDispatcher) {
            stockTarget.tabs = tabNew

            stockTargetDao.updateStockTargetTabEntities(
                stockTarget,
            )
        }
    }
}