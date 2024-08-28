package com.guanyc.stock.discipline.domain.use_case.stocks

import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import com.guanyc.stock.discipline.util.settings.Order
import com.guanyc.stock.discipline.util.settings.OrderType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author guanyc on 2023/2/28
 */
class GetAllStockNoteWithTargetListsUseCase @Inject constructor(
    private val stockNoteRepository: StockNoteRepository
) {
    operator fun invoke(orderType: OrderType = OrderType.DESC(), showCompleted: Boolean = false): Flow<List<StockNoteWithTargetLists>> {

        //FIXME map actions to order, to consider whether to show/hide completedness

        return stockNoteRepository.getAllStockNoteWithTargetLists()


    }
}