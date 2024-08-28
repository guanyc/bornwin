package com.guanyc.stock.discipline.presentation.targetconsts

import android.content.Context
import com.guanyc.stock.discipline.domain.model.TargetConstants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TargetConstantsUpdateUseCase @Inject constructor(
    private val targetMetaRepository: TargetMetaRepository, private val context: Context

) {
    suspend operator  fun invoke(targetConstants: TargetConstants)=
        targetMetaRepository.update(targetConstants)

}
