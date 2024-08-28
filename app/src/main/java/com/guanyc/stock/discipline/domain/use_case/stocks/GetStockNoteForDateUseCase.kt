package com.guanyc.stock.discipline.domain.use_case.stocks

import android.content.Context
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import javax.inject.Inject

class GetStockNoteForDateUseCase  @Inject constructor(
    private val stockNoteRepository: StockNoteRepository,
    private val context: Context
) {
    suspend operator fun invoke(createDate: String): List<StockNote> {
        //context.refreshTasksWidget()
        return stockNoteRepository.getStockNoteForDate(createDate)
    }
}