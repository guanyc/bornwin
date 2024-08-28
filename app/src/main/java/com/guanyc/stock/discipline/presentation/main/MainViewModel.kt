package com.guanyc.stock.discipline.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guanyc.stock.discipline.domain.model.StockNote

import com.guanyc.stock.discipline.domain.use_case.settings.GetSettingsUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.GetAllUncompletedStockNotesUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.GetCompletedStockNotesInAWeekUseCase

import com.guanyc.stock.discipline.theme.Rubik
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.settings.Order
import com.guanyc.stock.discipline.util.settings.OrderType
import com.guanyc.stock.discipline.util.settings.StartUpScreenSettings
import com.guanyc.stock.discipline.util.settings.ThemeSettings
import com.guanyc.stock.discipline.util.settings.toInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSettings: GetSettingsUseCase,
    private val getAllUncompletedStockNotes: GetAllUncompletedStockNotesUseCase,
    private val  getCompletedStockNotesInAWeek: GetCompletedStockNotesInAWeekUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    private var refreshTasksJob: Job? = null

    val themeMode: Flow<Int> =
        getSettings(intPreferencesKey(Constants.SETTINGS_THEME_KEY), ThemeSettings.AUTO.value)


    val defaultStartUpScreen = getSettings(
        intPreferencesKey(Constants.DEFAULT_START_UP_SCREEN_KEY),
        StartUpScreenSettings.SPACES.value
    )
    val font = getSettings(intPreferencesKey(Constants.APP_FONT_KEY), Rubik.toInt())
    val blockScreenshots =
        getSettings(booleanPreferencesKey(Constants.BLOCK_SCREENSHOTS_KEY), false)

    fun onDashboardEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.InitAll -> collectDashboardData()

        }
    }

    data class UiState(
        val unCompleted: List<StockNote> = emptyList(),
        val completed: List<StockNote> = emptyList(),
    )



    private fun collectDashboardData() = viewModelScope.launch {
        combine(
            getSettings(
                intPreferencesKey(Constants.ORDER_KEY),
                Order.DateModified(OrderType.ASC()).toInt()
            ),
            getSettings(
                booleanPreferencesKey(Constants.SHOW_COMPLETED_TASKS_KEY),
                false
            ),


            //getAllTasks
            getAllUncompletedStockNotes(),

            //week?
            getCompletedStockNotesInAWeek(),


        ) { order, showCompleted, unCompleted, completed->

            uiState = uiState.copy(

                unCompleted = unCompleted,
                completed = completed
            )

            //refreshTasks(order.toOrder(), showCompleted)

        }.collect()
    }

}