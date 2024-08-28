package com.guanyc.stock.discipline.domain.use_case.stocks

import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import javax.inject.Inject

class GetStockNoteWithStockTargetsUseCase  @Inject constructor(
    private val stockNoteRepository: StockNoteRepository
) {
    suspend operator fun invoke(stockNoteId: Long)  : List<StockTarget> {
        return  stockNoteRepository.getStockTargets(stockNoteId)
    }
}