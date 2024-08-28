package com.guanyc.stock.discipline.presentation.main.components

import android.content.Context
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.TargetConstants
import com.guanyc.stock.discipline.domain.repository.StockTargetRepository
import com.guanyc.stock.discipline.presentation.targetconsts.TargetMetaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateStockTargetFavoriteUseCase @Inject constructor(
    private val stockTargetRepository: StockTargetRepository, private val context: Context
) {
    suspend operator fun invoke(stockTarget: StockTarget,favorite:Boolean) {
        stockTargetRepository.updateStockTargetFavorite(stockTarget,favorite)
    }

}
