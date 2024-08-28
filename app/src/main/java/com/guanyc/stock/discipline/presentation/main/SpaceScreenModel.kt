package com.guanyc.stock.discipline.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guanyc.stock.discipline.domain.use_case.settings.GetSettingsUseCase
import com.guanyc.stock.discipline.domain.use_case.settings.SaveSettingsUseCase
import com.guanyc.stock.discipline.presentation.targetconsts.GetTargetMetaUseCase
import com.guanyc.stock.discipline.domain.model.TargetConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SpaceScreenModel @Inject constructor(
    private val getSettings: GetSettingsUseCase,
    private val saveSettings: SaveSettingsUseCase,
    private val getTargetMetaUseCase: GetTargetMetaUseCase
) : ViewModel() {

    data class UiState(
        val targetConstants: TargetConstants = TargetConstants(),
        val isTargetConstantsInitialized: Boolean = false
    )

    var uiState: UiState by mutableStateOf(UiState())

    private var getTasksJob: Job? = null

    init {
        viewModelScope.launch {
            getTasks()
        }
    }


    private fun getTasks() {
        getTasksJob?.cancel()

        getTasksJob = getTargetMetaUseCase().onEach { tc ->
            uiState = uiState.copy(isTargetConstantsInitialized = true, targetConstants = tc)

        }.launchIn(viewModelScope)

    }


}
