package com.guanyc.stock.discipline.presentation.main

import android.util.Log
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
import com.guanyc.stock.discipline.domain.use_case.stocks.InsertStockNoteUseCase

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
import javax.annotation.Nullable
import javax.inject.Inject

sealed class DashboardEvent {
    object InitAll : DashboardEvent()
    object ErrorDisplayed : DashboardEvent()

    data class  insertStockNote(val stockNote: StockNote): DashboardEvent()
    companion object {}

}


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSettings: GetSettingsUseCase,
    private val getAllUncompletedStockNotes: GetAllUncompletedStockNotesUseCase,
    private val  getCompletedStockNotesInAWeek: GetCompletedStockNotesInAWeekUseCase,
    private val insertStockNoteUseCase: InsertStockNoteUseCase,
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

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.InitAll -> collectDashboardData()

            DashboardEvent.ErrorDisplayed -> {
                uiState = uiState.copy(error = null)
            }

            is DashboardEvent.insertStockNote -> viewModelScope.launch {
                try {
                    val id = insertStockNoteUseCase(stockNote = event.stockNote)

                    Log.d("MainViewModel", "insertStockNote: $id")
                    onEvent(DashboardEvent.InitAll)
                }catch (e: Exception){
                    Log.e("MainViewModel", "insertStockNote: ${e.message}")
                    uiState = uiState.copy(error = e.message)
                }
            }
        }
    }

    data class UiState(
        @Nullable val error: String? = null,
        val unCompleted: List<StockNote> = emptyList(),
        val completed: List<StockNote> = emptyList(),
    )



    private fun collectDashboardData() = viewModelScope.launch {
        combine(
            //getAllTasks
            getAllUncompletedStockNotes(),

            //week?
            getCompletedStockNotesInAWeek(),


        ) {   unCompleted, completed->

            uiState = uiState.copy(

                unCompleted = unCompleted,
                completed = completed
            )

            //refreshTasks(order.toOrder(), showCompleted)

        }.collect()
    }

}