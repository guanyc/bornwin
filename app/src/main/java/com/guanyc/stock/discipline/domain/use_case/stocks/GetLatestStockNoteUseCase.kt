package com.guanyc.stock.discipline.domain.use_case.stocks

import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLatestStockNoteUseCase  @Inject constructor(
    private val stockNoteRepository: StockNoteRepository
) {
    operator fun invoke(): Flow<StockNoteWithTargetLists> {
        return stockNoteRepository.getLatestStockNote()


    }
}