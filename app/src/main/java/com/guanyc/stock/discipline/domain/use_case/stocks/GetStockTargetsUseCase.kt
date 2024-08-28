package com.guanyc.stock.discipline.domain.use_case.stocks

import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import javax.inject.Inject


class GetStockTargetsUseCase @Inject constructor(
    private val stockNoteRepository: StockNoteRepository
){
    suspend operator fun invoke(stockIds: List<Long>)  : Map<StockNote, List<StockTarget>> {
        return  stockNoteRepository.getStockTargets(stockIds)
    }
}