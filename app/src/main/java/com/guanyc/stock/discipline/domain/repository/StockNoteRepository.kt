package com.guanyc.stock.discipline.domain.repository

import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import kotlinx.coroutines.flow.Flow


interface StockNoteRepository {



    fun getLatestStockNote(): Flow<StockNoteWithTargetLists>
    //FIXME datefrom dateto
    fun getAllStockNoteWithTargetLists(): Flow<List<StockNoteWithTargetLists>>

    fun getStockNoteWithTargetLists(stockId: Long): Flow<StockNoteWithTargetLists>

    suspend fun getStockNoteById(noteId: Long): StockNote

    suspend fun insertStockNote(stockNote: StockNote): Long

    suspend fun updateStockNote(stockNote: StockNote)

    suspend fun deleteStockNote(stockNote: StockNote)

    suspend fun complete(stockDailyNoteId: Long, complete: Boolean)

    suspend fun getStockNoteForDate(createDate: String): List<StockNote>


    suspend fun insertStockTarget(stockTarget: StockTarget): Long


    //    @Insert(onConflict = OnConflictStrategy.REPLACE) insertStockTarget
    suspend fun insertOrUpdateStockTargets(stockTargets: List<StockTarget>)


    suspend fun deleteStockTarget(stockTarget: StockTarget)

    suspend fun getStockTargets(stockNoteId: Long): List<StockTarget>

    suspend fun getStockTargets(stockNoteIds: List<Long>): Map<StockNote, List<StockTarget>>

    suspend fun completeStockTarget(stockTarget: StockTarget, complete: Boolean = true)
    suspend fun updateStockTarget(stockTarget: StockTarget)
    fun getAllUncompletedStockNotes(): Flow<List<StockNote>>
    fun getCompletedStockNotesInAWeek(): Flow<List<StockNote>>

    fun getStockTargets(
        dateFrom: String,
        dateTo: String,
    ): Flow<List<StockTarget>>

    fun getStockTargetsForActionReview(
        dateFrom: String,
        dateTo: String,
        queryStringForActionReview:String
    ): Flow<List<StockTarget>>


}