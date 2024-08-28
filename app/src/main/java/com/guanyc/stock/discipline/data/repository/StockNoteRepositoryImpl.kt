package com.guanyc.stock.discipline.data.repository

import com.guanyc.stock.discipline.data.local.dao.StockNoteDao
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class StockNoteRepositoryImpl(
    private val stockNoteDao: StockNoteDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StockNoteRepository {




    override fun getAllUncompletedStockNotes(): Flow<List<StockNote>> {
        return stockNoteDao.getAllUncompletedStockNotes()
    }

    override fun getCompletedStockNotesInAWeek(): Flow<List<StockNote>> {
        return stockNoteDao.getCompletedStockNotesInAWeek()
    }

    override fun getLatestStockNote(): Flow<StockNoteWithTargetLists> {
        return stockNoteDao.getLatestStockNote()    }


    override fun getAllStockNoteWithTargetLists(): Flow<List<StockNoteWithTargetLists>> {

        return stockNoteDao.getAllStockNoteWithTargetLists()

    }

    override fun getStockNoteWithTargetLists(stockId: Long): Flow<StockNoteWithTargetLists> {
        return stockNoteDao.getStockNoteWithTargetLists(stockId)
    }


    override suspend fun getStockNoteById(noteId: Long): StockNote {
        return withContext(ioDispatcher) {
            if (noteId > -1) {
                stockNoteDao.getStockNote(noteId)
            } else {
                val tempDate = Calendar.getInstance()
                val date = Date(tempDate.timeInMillis)
                val df = SimpleDateFormat("yyyyMMdd")

                //, updatedDate = tempDate.timeInMillis
                StockNote(createDate = df.format(date))
            }
        }
    }

    override suspend fun insertStockNote(stockNote: StockNote): Long {

        var id =-1L

        withContext(ioDispatcher) {

            try {
                 id = stockNoteDao.insertStockNote(stockNote)

            } catch (e: Exception) {

                //, "unique constraints on createDate column is violated!")
                throw IllegalArgumentException(stockNote.toString()+e.toString())
            }

        }

        return id;
    }

    override suspend fun updateStockNote(stockNote: StockNote) {
        return withContext(ioDispatcher) {
            stockNoteDao.updateStockNote(stockNote)
        }
    }

    override suspend fun deleteStockNote(stockNote: StockNote) {
        return withContext(ioDispatcher) {
            stockNoteDao.deleteStockNote(stockNote)
        }
    }


    override suspend fun complete(stockDailyNoteId: Long, complete: Boolean) {
        withContext(ioDispatcher) {
            stockNoteDao.updateStockNoteIsCompleted(stockDailyNoteId, complete)
        }
    }

    override suspend fun getStockNoteForDate(createDate: String): List<StockNote> {
        return withContext(ioDispatcher) {
            stockNoteDao.getStockNoteForDate(createDate)
        }
    }

    override suspend fun insertStockTarget(stockTarget: StockTarget): Long {
        return withContext(ioDispatcher) {
            stockNoteDao.insertStockTarget(stockTarget)
        }
    }

    override suspend fun insertOrUpdateStockTargets(stockTargets: List<StockTarget>) {

        withContext(ioDispatcher) {

            stockTargets.forEachIndexed { index, item ->

                if (item.stockTargetId > 0) {
                    stockNoteDao.updateStockTarget(item)
                } else {
                    var id = stockNoteDao.insertStockTarget(item)
                }

            }
        }
    }


    override suspend fun deleteStockTarget(stockTarget: StockTarget) {
        withContext(ioDispatcher) {
            stockNoteDao.deleteStockTarget(stockTarget)
        }
    }


    override suspend fun getStockTargets(stockNoteId: Long): List<StockTarget> {
        return withContext(ioDispatcher) {
            stockNoteDao.getStockTargets(stockNoteId)
        }
    }

    override suspend fun getStockTargets(stockNoteIds: List<Long>): Map<StockNote, List<StockTarget>> {
        return withContext(ioDispatcher) {
            stockNoteDao.getStockTargets(stockNoteIds)
        }
    }



    override fun getStockTargets(
        dateFrom: String,
        dateTo: String,
    ): Flow<List<StockTarget>> {
        return stockNoteDao.getStockTargetsInDateRange(dateFrom,dateTo)
    }

    override fun getStockTargetsForActionReview(
        dateFrom: String,
        dateTo: String,
        queryStringForActionReview: String
    ): Flow<List<StockTarget>> {
        return stockNoteDao.getStockTargetsInDateRangeForActionReview(dateFrom,dateTo,queryStringForActionReview)
    }


    override suspend fun completeStockTarget(stockTarget: StockTarget, complete: Boolean) {
        withContext(ioDispatcher) {
            stockNoteDao.completeStockTarget(stockTarget.stockTargetId, complete)
        }
    }

    override suspend fun updateStockTarget(stockTarget: StockTarget) {
        withContext(ioDispatcher) {
            stockNoteDao.updateStockTarget(stockTarget)
        }
    }


}