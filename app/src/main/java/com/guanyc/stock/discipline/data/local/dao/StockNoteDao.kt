package com.guanyc.stock.discipline.data.local.dao

import android.annotation.TargetApi
import androidx.room.*
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import com.guanyc.stock.discipline.domain.model.TargetConstants
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

@Dao
interface StockNoteDao {

    @Query("SELECT * FROM stocknotes")
    fun getAllStockDailyNotes(): Flow<List<StockNote>>

    @Query("SELECT * FROM stocknotes WHERE stockNoteId = :stockNoteId")
    suspend fun getStockNote(stockNoteId: Long): StockNote

    //@Query("SELECT * FROM stocknotes WHERE name LIKE '%' || :name || '%'")
    //fun getStockDailyNotesByTitle(name: String): Flow<List<StockNote>>

    @Query("SELECT * FROM stocknotes WHERE createDate = :createDate")
    suspend fun getStockNoteForDate(createDate: String): List<StockNote>
    

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStockNote(stockNote: StockNote): Long

    //@Insert(onConflict = OnConflictStrategy.IGNORE)
    //suspend fun insertStockNotes(stockNote: List<StockNote>)

    @Update
    suspend fun updateStockNote(stockNote: StockNote)

    @Delete
    suspend fun deleteStockNote(stockNote: StockNote)


    //这个排序 在 repository里面实现
    //@Query("SELECT * FROM stocknotes where isCompleted = :isCompleted  ")
    //fun getNotesByIsCompleted(isCompleted: Boolean): Flow<List<StockNote>>


    @Query("update stocknotes set isCompleted = :complete where stockNoteId = :stockNoteId ")
    fun updateStockNoteIsCompleted(stockNoteId: Long, complete: Boolean)

    @Query("update targets set isCompleted = :complete where  stockTargetId = :stockTargetId")
    fun completeStockTarget(stockTargetId: Long, complete: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStockTarget(stockTarget: StockTarget): Long

    @Insert
    fun insertStockTargets(targets: List<StockTarget>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateStockTarget(stockTarget: StockTarget)

    @Delete
    fun deleteStockTarget(stockTarget: StockTarget)


    @Transaction
    @Insert
    fun addStockNoteWithStockTargets(stockNote: StockNote, targets: List<StockTarget>)


    //SQLite 没有单独的存储布尔值得存储类型。而是把false存储为0，true存储为1。也就是用INTEGER来存储，表达布尔类型。

    @Transaction
    @Query("SELECT * FROM targets where stockNoteId = :stockNoteId ")
    fun getStockTargets(stockNoteId: Long): List<StockTarget>


    @Transaction
    @Query("SELECT * FROM stocknotes JOIN targets ON stocknotes.stockNoteId = targets.stockNoteId where stocknotes.stockNoteId IN (:stockNoteIds) ")
    fun getStockTargets(stockNoteIds: List<Long>): Map<StockNote, List<StockTarget>>



    //////////////getNotes/////////////////

    @Transaction
    @Query("SELECT * FROM stocknotes")
    fun getAllStockNoteWithTargetLists(): Flow<List<StockNoteWithTargetLists>>

    @Transaction
    @Query("SELECT * FROM stocknotes where stockNoteId = :stockNoteId ")
    fun getStockNoteWithTargetLists(stockNoteId: Long): Flow<StockNoteWithTargetLists>


    @Query("SELECT * FROM stocknotes WHERE isCompleted = :isCompleted")
    fun getAllStockNotesOnIsCompleted(isCompleted: Boolean): Flow<List<StockNote>>

    fun getAllUncompletedStockNotes(): Flow<List<StockNote>> {
        return getAllStockNotesOnIsCompleted(isCompleted = false)
    }

    //val tempDate = Calendar.getInstance()
    //val date = Date(tempDate.timeInMillis)
    //val df = SimpleDateFormat("yyyyMMdd")
    //, updatedDate = tempDate.timeInMillis
    //StockNote(createDate = df.format(date))

    @Query("SELECT * FROM targets WHERE createDate > :dateFrom and createDate <= :dateTo ")
    fun getStockTargetsInDateRange(dateFrom: String, dateTo: String): Flow<List<StockTarget>>

    @Query("SELECT * FROM stocknotes WHERE createDate >= :dateStart and createDate < :dateEnd")
    fun getStockNotesInDateRange(dateStart: String, dateEnd: String): Flow<List<StockNote>>



    @Query("SELECT * FROM targets WHERE createDate > :dateFrom and createDate <= :dateTo and actionReview LIKE '%' || :queryStringForActionReview || '%' ")
    fun getStockTargetsInDateRangeForActionReview(
        dateFrom: String,
        dateTo: String,
        queryStringForActionReview: String
    ): Flow<List<StockTarget>>

    fun getStockNotesInDateRange(dateStart: Date, dateEnd: Date): Flow<List<StockNote>> {
        val df = SimpleDateFormat("yyyyMMdd")
        return getStockNotesInDateRange(df.format(dateStart), df.format(dateEnd))
    }

    @TargetApi(26)
    fun getOneWeekDateRange(): Pair<LocalDate, LocalDate> {
        val endDate = LocalDate.now()
        val startDate = endDate.minus(6, ChronoUnit.DAYS)
        return startDate to endDate
    }

    fun getCompletedStockNotesInAWeek(): Flow<List<StockNote>> {
        val rightnow = Calendar.getInstance()
        val startDate = rightnow.time

        rightnow.add(Calendar.DATE, -7)
        val dateEnd = rightnow.time

        return getStockNotesInDateRange(startDate, dateEnd)

    }




    ////////// //tableName = "targetholder",

    @Query("SELECT * FROM targetholder where id = :id")
    fun getTargetConstants(id: Int): Flow<TargetConstants>


    @Transaction
    @Query("SELECT * FROM stocknotes ORDER BY stockNoteId DESC LIMIT 1")
    fun getLatestStockNote(): Flow<StockNoteWithTargetLists>

    @Query("SELECT * FROM targetholder limit 1")
    fun getTheFirstTargetConstants(): Flow<TargetConstants>

    @Update
    suspend fun updateTargetConstants(targetConstants: TargetConstants)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTargetConstants(targetConstants: TargetConstants)



}