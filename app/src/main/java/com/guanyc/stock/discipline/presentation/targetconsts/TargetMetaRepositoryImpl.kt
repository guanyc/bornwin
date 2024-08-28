package com.guanyc.stock.discipline.presentation.targetconsts

import com.guanyc.stock.discipline.data.local.dao.StockNoteDao
import com.guanyc.stock.discipline.domain.model.TargetConstants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TargetMetaRepositoryImpl(

    private val stockNoteDao: StockNoteDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TargetMetaRepository {



    override suspend fun update(targetConstants: TargetConstants) {
        withContext(ioDispatcher){
            stockNoteDao.updateTargetConstants(targetConstants)
        }
    }

    override  fun getTheFirstElement(): Flow<TargetConstants> {
        return stockNoteDao.getTheFirstTargetConstants()

    }


}