package com.guanyc.stock.discipline.presentation.main.components

import android.content.Context
import com.guanyc.stock.discipline.domain.model.TargetConstants
import com.guanyc.stock.discipline.presentation.targetconsts.TargetMetaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class getAllTabsUseCase @Inject constructor(
    private val targetMetaRepository: TargetMetaRepository,
    private val context: Context
) {
    operator  fun invoke(anything: String = ""): Flow<TargetConstants> {
       return  targetMetaRepository.getTheFirstElement()

    }

}