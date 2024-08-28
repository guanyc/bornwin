package com.guanyc.stock.discipline.presentation.main.components

import android.content.Context
import com.guanyc.stock.discipline.domain.model.PinnedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.repository.StockTargetRepository
import javax.inject.Inject

class UpdateTabPinnedStockTargetListUseCase @Inject constructor(
    private val stockTargetRepository: StockTargetRepository, private val context: Context
) {
    suspend operator fun invoke(pinnedTabEntityStockTargetList: PinnedTabEntityStockTargetList) {
        stockTargetRepository.updateTabPinnedStockTargetList(pinnedTabEntityStockTargetList)
    }

}