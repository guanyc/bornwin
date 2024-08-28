package com.guanyc.stock.discipline.presentation.main.components

import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.repository.StockTargetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllStockTargetsUnCompletedUseCase @Inject constructor(
    private val StockTargetRepository: StockTargetRepository
) {
    operator fun invoke(): Flow<List<StockTarget>> {
        return StockTargetRepository.getAllUncompletedStockTargets()
    }
}
