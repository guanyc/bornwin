package com.guanyc.stock.discipline.domain.use_case.stocks

import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import javax.inject.Inject

class GetStockNoteByIdUseCase @Inject constructor(
    private val stockNoteRepository: StockNoteRepository
) {
    suspend operator fun invoke(noteId: Long): StockNote {
        return stockNoteRepository.getStockNoteById(noteId)
    }

}