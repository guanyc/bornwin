package com.guanyc.stock.discipline.presentation.main.components

import android.content.Context
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.repository.StockTargetRepository
import javax.inject.Inject

class UpdateStockTargetsUseCase @Inject constructor(
    private val stockTargetRepository: StockTargetRepository, private val context: Context
) {
    suspend operator fun invoke(stockTargetList: List<StockTarget>) {
        stockTargetRepository.updateStockTargetList(stockTargetList)
    }

}
