package com.guanyc.stock.discipline.presentation.main.components

import android.content.Context
import com.guanyc.stock.discipline.domain.model.PinnedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.model.ToppedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.repository.StockTargetRepository
import javax.inject.Inject

class InsertTabPinnedStockTargetListUseCase @Inject constructor(
    private val stockTargetRepository: StockTargetRepository, private val context: Context
) {
    suspend operator fun invoke(topped: ToppedTabEntityStockTargetList) {
        stockTargetRepository.insertTabToppedStockTargetList(topped)
    }

}