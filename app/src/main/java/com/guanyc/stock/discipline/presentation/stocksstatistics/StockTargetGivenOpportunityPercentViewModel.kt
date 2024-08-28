package com.guanyc.stock.discipline.presentation.stocksstatistics

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.use_case.settings.GetSettingsUseCase
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.settings.Order
import com.guanyc.stock.discipline.util.settings.OrderType
import com.guanyc.stock.discipline.util.settings.toInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.annotation.Nullable
import javax.inject.Inject


@HiltViewModel
class StockTargetGivenOpportunityPercentViewModel
@Inject constructor(
    private val getSettings: GetSettingsUseCase,
    private val getStockTargetsInDateRangeUseCase: GetStockTargetsInDateRangeUseCase
) : ViewModel() {

    data class UiState(
        val stockCodeList: List<StockTarget> = emptyList(),
        val groupByIsGivenOpportunity: Map<Boolean, List<StockTarget>> = emptyMap(),

        //key 是 stocktarget的code
        val dateFrom: String = SimpleDateFormat("yyyyMMdd").format(Date()),
        val dateTo: String = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis() - 7 * 86400 * 1000)),
        //val dateTo: String = SimpleDateFormat("yyyyMMdd").format(Date()),
        val orderType: OrderType = OrderType.DESC(),
        @Nullable val error: String? = null,

        val includingCompleted: Boolean = true,
        val includingUnCompleted: Boolean = true,
    )


    var uiState by mutableStateOf(UiState())//


    private fun onCollectingCodes(stockTargets: List<StockTarget>) {

        var groupByIsGivenOpportunity: Map<Boolean, List<StockTarget>> =
            stockTargets.groupBy { it.isOpportunityGiven }

        uiState = uiState.copy(
            stockCodeList = stockTargets,
            groupByIsGivenOpportunity = groupByIsGivenOpportunity
        )
    }

    init {
        uiState = uiState.copy(
            //one week ago
            dateFrom = SimpleDateFormat("yyyyMMdd").format(Date().time - 7 * 86400 * 1000),
            //date now
            dateTo = SimpleDateFormat("yyyyMMdd").format(Date()),
        )

        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                getSettings(
                    intPreferencesKey(Constants.STOCK_DAILY_NOTE_ORDER_KEY),
                    Order.DateCreated(OrderType.DESC()).toInt()
                ),

                getSettings(
                    booleanPreferencesKey(Constants.SHOW_COMPLETED_ITEMS_VIEW_LIST_KEY), false
                ),

                getStockTargetsInDateRangeUseCase.invoke(
                    uiState.dateFrom,
                    uiState.dateTo,
                    orderType = uiState.orderType,
                    includingCompleted = uiState.includingCompleted,
                    includingUnCompleted = uiState.includingUnCompleted
                )
            ) { order, completed, stockCodes ->
                Log.d(TAG, " getting result from combined ...............")

                onCollectingCodes(stockCodes)
            }.collect()

        }
    }//init


    fun onEvent(event: StockTargetGivenOpportunityPercentEvent) {
        when (event) {
            StockTargetGivenOpportunityPercentEvent.ErrorDisplayed -> uiState =
                uiState.copy(error = null)

            StockTargetGivenOpportunityPercentEvent.GetStockTargetsInDateRange -> viewModelScope.launch {
                getStockTargetsInDateRangeUseCase.invoke(
                    uiState.dateFrom,
                    uiState.dateTo,
                    uiState.orderType,
                    uiState.includingCompleted,
                    uiState.includingUnCompleted
                ).onEach { stockTargets: List<StockTarget> ->
                    Log.d(TAG, " getting result from onEach ...............")
                    onCollectingCodes(stockTargets)
                }.collect()
            }
        }
    }


}

sealed class StockTargetGivenOpportunityPercentEvent {
    object ErrorDisplayed : StockTargetGivenOpportunityPercentEvent()

    companion object {}

    object GetStockTargetsInDateRange : StockTargetGivenOpportunityPercentEvent()

}