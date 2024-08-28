package com.guanyc.stock.discipline.domain.use_case.stocks

import android.content.Context
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStockNoteWithTargetListsUseCase  @Inject constructor(
    private val stockNoteRepository: StockNoteRepository,
    private val context: Context
) {
    operator fun invoke(stockId: Long): Flow<StockNoteWithTargetLists> {
        //context.refreshTasksWidget()
        return stockNoteRepository.getStockNoteWithTargetLists(stockId)
    }
}