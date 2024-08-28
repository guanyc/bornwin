package com.guanyc.stock.discipline.presentation.main.components

import android.content.Context
import com.guanyc.stock.discipline.domain.model.ToppedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.repository.StockTargetRepository
import javax.inject.Inject

class UpdateTabToppedStockTargetListUseCase @Inject constructor(
    private val stockTargetRepository: StockTargetRepository, private val context: Context
) {
    suspend operator fun invoke(topped: ToppedTabEntityStockTargetList) {
        stockTargetRepository.updateTabToppedStockTargetList(topped)
    }

}