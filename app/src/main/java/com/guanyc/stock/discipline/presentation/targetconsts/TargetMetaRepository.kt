package com.guanyc.stock.discipline.presentation.targetconsts

import com.guanyc.stock.discipline.domain.model.TargetConstants
import kotlinx.coroutines.flow.Flow

interface TargetMetaRepository {

    //fun getTargetConstants(targetConstantsId: Int): Flow<TargetConstants>

    fun getTheFirstElement():Flow<TargetConstants>

    suspend fun update(targetConstants: TargetConstants)

    companion object {

    }

}
