package com.guanyc.stock.discipline.presentation.targetconsts

import android.content.Context
import com.guanyc.stock.discipline.domain.model.TargetConstants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTargetMetaUseCase @Inject constructor(
    private val targetMetaRepository: TargetMetaRepository, private val context: Context

) {
    operator fun invoke(anything: String = ""): Flow<TargetConstants> {

        return targetMetaRepository.getTheFirstElement()
    }

}
