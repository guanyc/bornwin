package com.guanyc.stock.discipline.domain.use_case.stocks

import android.content.Context
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import javax.inject.Inject

class AddStockTargetUseCase @Inject constructor(
    private val stockNoteRepository: StockNoteRepository,
    private val context: Context
) {

    suspend operator fun invoke(targets:List<StockTarget>){
        stockNoteRepository.insertOrUpdateStockTargets(targets)
        //context.refreshTasksWidget()
    }

    suspend operator fun invoke(stockTarget: StockTarget):Long{
        val id = stockNoteRepository.insertStockTarget(stockTarget)
        //context.refreshTasksWidget()
        return id
    }
}