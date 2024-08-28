package com.guanyc.stock.discipline.presentation.targetconsts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guanyc.stock.discipline.domain.model.TargetConstants
import com.guanyc.stock.discipline.domain.use_case.settings.GetSettingsUseCase
import com.guanyc.stock.discipline.domain.use_case.settings.SaveSettingsUseCase
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.settings.Order
import com.guanyc.stock.discipline.util.settings.OrderType
import com.guanyc.stock.discipline.util.settings.toInt
import com.guanyc.stock.discipline.util.settings.toOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject



sealed class TargetActionEvent{
    object ErrorDisplayed: TargetActionEvent()
    data class Update(val targetConstants: TargetConstants): TargetActionEvent()

}

@HiltViewModel
class TargetMetaScreenModel @Inject constructor(
    private val getSettings: GetSettingsUseCase,
    private val saveSettings: SaveSettingsUseCase,


    private val updateTargetConstants: UpdateTargetMetaUseCase,
    private val getTargetConstants: GetTargetMetaUseCase


    ): ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    private var getTasksJob: Job? = null

    init {
        viewModelScope.launch {

            combine(
                getSettings(
                    intPreferencesKey(Constants.ORDER_KEY),
                    Order.DateModified(OrderType.ASC()).toInt()
                ),

                getSettings(
                    booleanPreferencesKey(Constants.SHOW_COMPLETED_TASKS_KEY), false
                ),

                ){
                    order, showCompleted ->
                getTasks(order.toOrder())

            }.collect()
        }

    }

    private fun getTasks(order: Order, showCompleted: Boolean = true) {
        getTasksJob?.cancel()
        getTasksJob = getTargetConstants()
            //.map { list ->
            //if (showCompleted)
            //  list
            //else
            //  list.filter { !it.isCompleted }
            //}
            .onEach { tc ->
                uiState = uiState.copy(
                    targetConstants = tc
                )
            }.launchIn(viewModelScope)
    }


    data class UiState(
        val targetConstants: TargetConstants =  TargetConstants(),
        val error: String? = null,
        val navigateUp: Boolean = false,
    )

    fun onEvent(event: TargetActionEvent) {

        when(event){
            TargetActionEvent.ErrorDisplayed -> {
                uiState = uiState.copy(error = null)
            }

            is TargetActionEvent.Update -> viewModelScope.launch {
                updateTargetConstants(event.targetConstants)
            }
        }

    }


}
