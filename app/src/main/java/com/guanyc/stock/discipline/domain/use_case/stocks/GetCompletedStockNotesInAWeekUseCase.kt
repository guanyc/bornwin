package com.guanyc.stock.discipline.domain.use_case.stocks

import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompletedStockNotesInAWeekUseCase  @Inject constructor(
    private val stockNoteRepository: StockNoteRepository
) {
    operator fun invoke(): Flow<List<StockNote>> {
        return stockNoteRepository.getCompletedStockNotesInAWeek()
    }
}