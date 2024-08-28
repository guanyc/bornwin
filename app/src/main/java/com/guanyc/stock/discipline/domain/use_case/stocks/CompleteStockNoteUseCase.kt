package com.guanyc.stock.discipline.domain.use_case.stocks

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import javax.inject.Inject

open class CompleteStockNoteUseCase @Inject constructor(
    private val stockNoteRepository: StockNoteRepository, private val context: Context
){
    suspend operator fun invoke(stockDailyNoteId: Long, isComplete:Boolean= false) {
        stockNoteRepository.complete(stockDailyNoteId, isComplete)
        //context.refreshTasksWidget()
    }
}
