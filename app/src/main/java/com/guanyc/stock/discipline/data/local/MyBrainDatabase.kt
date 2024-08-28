package com.guanyc.stock.discipline.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.guanyc.stock.discipline.data.local.dao.StockNoteDao
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.data.local.converters.DBConverters
import com.guanyc.stock.discipline.data.local.dao.StockTargetDao
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.PinnedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.model.ToppedTabEntityStockTargetList

import com.guanyc.stock.discipline.domain.model.TargetConstants

@Database(
    entities = [
        TargetConstants::class,
        StockNote::class,
        StockTarget::class,
        PinnedTabEntityStockTargetList::class,
        ToppedTabEntityStockTargetList::class,
    ], version = 5, exportSchema = true,
    autoMigrations = [
        //AutoMigration(from = 1, to = 2,)
    ]
)

@TypeConverters(DBConverters::class)
abstract class MyBrainDatabase : RoomDatabase() {

    abstract fun stockTargetDao(): StockTargetDao
    abstract fun stockNoteDao(): StockNoteDao


    companion object {
        const val DATABASE_NAME = "by_brain_db"
    }
}