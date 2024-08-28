package com.guanyc.stock.discipline.presentation.stocksstatistics

import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import com.guanyc.stock.discipline.util.settings.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetStockTargetsInDateRangeUseCase @Inject constructor(
    private val stockNoteRepository: StockNoteRepository
) {
    operator fun invoke(
        dateFrom: kotlin.String,
        dateTo: kotlin.String,
        orderType: com.guanyc.stock.discipline.util.settings.OrderType,
        includingCompleted: kotlin.Boolean = false,
        includingUnCompleted: kotlin.Boolean = true,
        queryStringForActionReview: String = ""
    ): Flow<List<StockTarget>> {

        // map actions to order, to consider whether to show/hide completedness

        if(queryStringForActionReview.isNotEmpty()){
            val listFlow = stockNoteRepository.getStockTargetsForActionReview(dateFrom, dateTo,queryStringForActionReview)
            return mapFlow(listFlow, orderType, includingCompleted, includingUnCompleted)
        }else{
            val listFlow = stockNoteRepository.getStockTargets(dateFrom, dateTo)
            return mapFlow(listFlow, orderType, includingCompleted, includingUnCompleted)
        }


    }

    private fun mapFlow(
        listFlow: Flow<List<StockTarget>>,
        orderType: OrderType,
        includingCompleted: Boolean,
        includingUnCompleted: Boolean
    ) = listFlow.map { targets ->
        when (orderType) {
            is OrderType.DESC -> {

                return@map when {
                    includingCompleted && includingUnCompleted -> {
                        targets.sortedByDescending { it.createDate }
                    }

                    includingCompleted && !includingUnCompleted -> {
                        targets.filter { it.isCompleted }.sortedByDescending { it.createDate }
                    }

                    !includingCompleted && includingUnCompleted -> {
                        targets.filter { !it.isCompleted }.sortedByDescending { it.createDate }
                    }

                    else -> targets.filter { false }

                }
            }

            is OrderType.ASC -> {
                return@map when {
                    includingCompleted && includingUnCompleted -> {
                        targets.sortedBy { it.createDate }
                    }

                    includingCompleted && !includingUnCompleted -> {
                        targets.filter { it.isCompleted }.sortedBy { it.createDate }
                    }

                    !includingCompleted && includingUnCompleted -> {
                        targets.filter { !it.isCompleted }.sortedBy { it.createDate }
                    }

                    else -> {
                        //never execute here?
                        targets.filter { false }
                    }
                }

            }
        }

    }
}
