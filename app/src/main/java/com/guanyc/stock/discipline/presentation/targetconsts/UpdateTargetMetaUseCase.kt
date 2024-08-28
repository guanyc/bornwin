package com.guanyc.stock.discipline.presentation.targetconsts

import android.content.Context
import com.guanyc.stock.discipline.domain.model.TargetConstants
import javax.inject.Inject

class UpdateTargetMetaUseCase @Inject constructor(
    private val targetMetaRepository: TargetMetaRepository,
    private val context: Context
){
    suspend operator fun invoke(targetConstants: TargetConstants) {
        targetMetaRepository.update(targetConstants)
    }
}
