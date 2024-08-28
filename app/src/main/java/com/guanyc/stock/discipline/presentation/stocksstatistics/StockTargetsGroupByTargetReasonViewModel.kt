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
import com.guanyc.stock.discipline.domain.model.targetReasonList
import com.guanyc.stock.discipline.domain.use_case.settings.GetSettingsUseCase
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.settings.Order
import com.guanyc.stock.discipline.util.settings.OrderType
import com.guanyc.stock.discipline.util.settings.toInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.annotation.Nullable
import javax.inject.Inject


@HiltViewModel
class StockTargetsGroupByTargetReasonViewModel @Inject constructor(
    private val getSettings: GetSettingsUseCase,
    private val getStockTargetsInDateRangeUseCase: GetStockTargetsInDateRangeUseCase
) : ViewModel() {

    data class UiState(
        val stockCodeList: List<StockTarget> = emptyList(),
        val targetGroupByReasonTitle:Map<String, List<TabEntity>> = emptyMap(),

        //key 是 stocktarget的code
        val dateFrom: String = SimpleDateFormat("yyyyMMdd").format(Date()),
        val dateTo: String = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis() - 7 * 86400 * 1000)),
        //val dateTo: String = SimpleDateFormat("yyyyMMdd").format(Date()),
        val orderType: OrderType = OrderType.DESC(),
        @Nullable val error: String? = null,

        val includingCompleted: Boolean = true,
        val includingUnCompleted: Boolean = true,
    )


    var uiState by mutableStateOf(StockTargetsGroupByTargetReasonViewModel.UiState())//

    init {
        val cal = Calendar.getInstance()

        cal.add(Calendar.DAY_OF_YEAR, -365)

        uiState = uiState.copy(
            dateFrom = SimpleDateFormat("yyyyMMdd").format(cal.time),
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

            }.collect {}


        }

    }//init


    fun onEvent(event: StockTargetsGroupByTargetReasonEvent) {
        when (event) {
            StockTargetsGroupByTargetReasonEvent.ErrorDisplayed -> uiState =
                uiState.copy(error = null)

            StockTargetsGroupByTargetReasonEvent.GetStockTargetsInDateRange -> viewModelScope.launch {
                Log.d(TAG, uiState.toString())

                getStockTargetsInDateRangeUseCase.invoke(
                    uiState.dateFrom,
                    uiState.dateTo,
                    uiState.orderType,
                    uiState.includingCompleted,
                    uiState.includingUnCompleted
                ).onEach { stockCodes ->
                    Log.d(TAG, " getting result from onEach ...............")


                    onCollectingCodes(stockCodes)

                }.collect()
            }
        }
    }

    private fun onCollectingCodes(stockCodes: List<StockTarget>) {
        val reasonList: List<List<TabEntity>> = stockCodes.map { it -> it.targetReasonList }
        val targetReasonMetas: List<TabEntity> = reasonList.flatten()

        //如果适用groupingby可以直接算出来each size count
        val targetGroupByReasonTitle: Map<String, List<TabEntity>> =
            targetReasonMetas.groupBy { it.title }

        uiState = uiState.copy(
            stockCodeList = stockCodes,
            targetGroupByReasonTitle = targetGroupByReasonTitle
        )
    }
}

sealed class StockTargetsGroupByTargetReasonEvent {
    object ErrorDisplayed : StockTargetsGroupByTargetReasonEvent()

    companion object {}

    object GetStockTargetsInDateRange : StockTargetsGroupByTargetReasonEvent()

}