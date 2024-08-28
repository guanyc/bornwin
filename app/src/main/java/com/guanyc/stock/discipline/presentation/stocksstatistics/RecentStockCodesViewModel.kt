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
import com.guanyc.stock.discipline.util.BackupUtil.toJson
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
import java.util.Calendar.DAY_OF_YEAR
import java.util.Date
import javax.annotation.Nullable
import javax.inject.Inject

@HiltViewModel
open class RecentStockCodesViewModel @Inject constructor(
    private val getSettings: GetSettingsUseCase,
    private val getStockTargetsInDateRangeUseCase: GetStockTargetsInDateRangeUseCase
) : ViewModel() {

    //val tempDate = Calendar.getInstance()
    //val date = Date(tempDate.timeInMillis)

    //val createDate = df.format(date)


    data class UiState(
        val stockCodeList: List<StockTarget> = emptyList(),

        //key 是 stocktarget的code
        val stockCodeMap: Map<String, List<StockTarget>> = emptyMap(),


        val dateFrom: String = SimpleDateFormat("yyyyMMdd").format(Date()),
        val dateTo: String = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis() - 7 * 86400 * 1000)),
        val orderType: OrderType = OrderType.DESC(),
        @Nullable
        val error: String? = null,

        val includingCompleted: Boolean = true,
        val includingUnCompleted: Boolean = true,
        val queryStringForActionReview:String = ""
    )


    var uiState by mutableStateOf(RecentStockCodesViewModel.UiState())//

    init {

        val cal = Calendar.getInstance()
        cal.add(DAY_OF_YEAR, -7)

        uiState = uiState.copy(
            dateFrom = SimpleDateFormat("yyyyMMdd").format(cal.time),
            dateTo = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis() - 7 * 86400 * 1000)),

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

                uiState = uiState.copy(
                    stockCodeList = stockCodes,
                    stockCodeMap = stockCodes.groupBy { it -> it.code },
                )


            }.collect {

            }

            Log.d("init", uiState.stockCodeList.toJson())
        }
    }

    fun onEvent(event: RecentStockCodesEvent) {
        when (event) {
            is RecentStockCodesEvent.ErrorDisplayed -> uiState = uiState.copy(error = null)

            is RecentStockCodesEvent.GetStockCodeInDateRange -> viewModelScope.launch {

                if(uiState.queryStringForActionReview.isNotEmpty()){
                    getStockTargetsInDateRangeUseCase.invoke(
                        uiState.dateFrom,
                        uiState.dateTo,
                        uiState.orderType,
                        uiState.includingCompleted,
                        uiState.includingUnCompleted,
                        uiState.queryStringForActionReview,
                    ).onEach {
                        Log.d(TAG, " getting result from onEach ...............")
                        uiState = uiState.copy(stockCodeList = it,
                            stockCodeMap = it.groupBy { it -> it.code })
                    }.collect()
                }else{
                    getStockTargetsInDateRangeUseCase.invoke(
                        uiState.dateFrom,
                        uiState.dateTo,
                        uiState.orderType,
                        uiState.includingCompleted,
                        uiState.includingUnCompleted,
                    ).onEach {
                        Log.d(TAG, " getting result from onEach ...............")
                        uiState = uiState.copy(stockCodeList = it,
                            stockCodeMap = it.groupBy { it -> it.code })
                    }.collect()
                }

            }

            is RecentStockCodesEvent.QueryChanged ->viewModelScope.launch{
                if(uiState.queryStringForActionReview.isNotEmpty()){
                    getStockTargetsInDateRangeUseCase.invoke(
                        uiState.dateFrom,
                        uiState.dateTo,
                        uiState.orderType,
                        uiState.includingCompleted,
                        uiState.includingUnCompleted,
                        uiState.queryStringForActionReview,
                    ).onEach {
                        Log.d(TAG, " getting result from onEach ...............")
                        uiState = uiState.copy(stockCodeList = it,
                            stockCodeMap = it.groupBy { it -> it.code })
                    }.collect()
                }else{
                    getStockTargetsInDateRangeUseCase.invoke(
                        uiState.dateFrom,
                        uiState.dateTo,
                        uiState.orderType,
                        uiState.includingCompleted,
                        uiState.includingUnCompleted,
                    ).onEach {
                        Log.d(TAG, " getting result from onEach ...............")
                        uiState = uiState.copy(stockCodeList = it,
                            stockCodeMap = it.groupBy { it -> it.code })
                    }.collect()
                }

            }
        }
    }


}

sealed class RecentStockCodesEvent {
    object QueryChanged : RecentStockCodesEvent()

    object ErrorDisplayed : RecentStockCodesEvent()

    companion object {}

    object  GetStockCodeInDateRange : RecentStockCodesEvent()

}
