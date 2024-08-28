package com.guanyc.stock.discipline.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.app.dataStore
import com.guanyc.stock.discipline.app.getString
import com.guanyc.stock.discipline.data.local.MyBrainDatabase
import com.guanyc.stock.discipline.data.local.dao.StockNoteDao
import com.guanyc.stock.discipline.data.local.dao.StockTargetDao
import com.guanyc.stock.discipline.data.repository.SettingsRepositoryImpl
import com.guanyc.stock.discipline.data.repository.StockNoteRepositoryImpl
import com.guanyc.stock.discipline.data.repository.StockTargetRepositoryImpl
import com.guanyc.stock.discipline.domain.model.PinnedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.TargetConstants
import com.guanyc.stock.discipline.domain.repository.SettingsRepository
import com.guanyc.stock.discipline.domain.repository.StockNoteRepository
import com.guanyc.stock.discipline.domain.repository.StockTargetRepository
import com.guanyc.stock.discipline.presentation.main.components.TAB_TYPE
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import com.guanyc.stock.discipline.presentation.targetconsts.TargetMetaRepository
import com.guanyc.stock.discipline.presentation.targetconsts.TargetMetaRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton


class StockNoteCallBack @Inject constructor(
    private val provider: Provider<StockNoteDao>,
    private val providerTarget: Provider<StockTargetDao>

) : RoomDatabase.Callback() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        //applicationScope.launch {}

        CoroutineScope(Dispatchers.IO).launch {

            var tas = listOf(
                TabEntity(title = "打板", tabType = TAB_TYPE.TAB_ACTION),
                TabEntity(title = "低吸", tabType = TAB_TYPE.TAB_ACTION),
                TabEntity(title = "接力", tabType = TAB_TYPE.TAB_ACTION),

            )

            var trs = listOf<TabEntity>(
                TabEntity(title = "高标", tabType = TAB_TYPE.TAB_REASON),
                TabEntity(title = "强度量能板", tabType = TAB_TYPE.TAB_REASON),
                TabEntity(title = "新高放量", tabType = TAB_TYPE.TAB_REASON),
                TabEntity(title = "超跌", tabType = TAB_TYPE.TAB_REASON),
            )

            var twls = listOf<TabEntity>(
                TabEntity(
                    title = getString(R.string.string_position),
                    tabType = TAB_TYPE.TAB_SPECIAL
                ),
                TabEntity(title = getString(R.string.string_all), tabType = TAB_TYPE.TAB_SPECIAL),

                TabEntity(
                    title = getString(R.string.string_indices),
                    tabType = TAB_TYPE.TAB_SPECIAL
                ),
                TabEntity(
                    title = getString(R.string.string_special),
                    tabType = TAB_TYPE.TAB_SPECIAL
                ),
            )

            val targetConstants = TargetConstants(
                tabs = trs.plus(tas).plus(twls)
            )
            provider.get().insertTargetConstants(targetConstants)


            var tabs = listOf<TabEntity>() + twls[0] + trs + twls[1] + twls[3] + tas + twls[2]

            for (tab in tabs) {
                var pinnedTabEntityStockTargetList = PinnedTabEntityStockTargetList(
                    tab = tab,
                    pinnedList = listOf()
                )
                providerTarget.get().insertTabPinnedStockTargetList(tab, listOf())
                providerTarget.get().insertTabToppedStockTargetList(tab, listOf())

            }


            var stocknotes = listOf(

                StockNote(
                    createDate = ""
                ),

                StockNote(
                    createDate = "20240807"
                ),
                StockNote(
                    createDate = "20240802"
                ),

            )

            var dao = provider.get()

            var noteId = -1L
            var note: StockNote? = null
            var noteWithEmptyCreateDate: StockNote? = null
            var noteWithEmptyCreateDateId = -1L

            stocknotes.forEach {
                    note = it
                    noteId = dao.insertStockNote(it)
            }

            val stockTargets = listOf(
                StockTarget(
                    stockNoteId = noteId,
                    createDate = note?.createDate!!,
                    code = "600015",
                    name = "中国船舶",
                    tabs = listOf(tas[0], tas[1], trs[0], trs[1]),
                ),
                StockTarget(
                    stockNoteId = noteId,
                    createDate = note?.createDate!!,
                    code = "600611",
                    name = "大众交通",
                    tabs = listOf(tas[0],  trs[1], trs[2]),

                    ),
                StockTarget(
                    stockNoteId = noteId,
                    createDate = note?.createDate!!,
                    code = "002607",
                    name = "中公教育",
                    tabs = listOf(tas[2],  trs[2], trs[3]),
                ),

                StockTarget(
                    stockNoteId = noteId,
                    createDate = note?.createDate!!,
                    code = "600000",
                    name = "浦发银行",
                    tabs = listOf(tas[0],  trs[2], trs[3]),
                ),

                StockTarget(
                    stockNoteId = noteId,
                    createDate = note?.createDate!!,
                    code = "000001",
                    name = "上证指数",
                    tabs = listOf(twls[0], twls[1], twls[3])
                )


            )

            provider.get().insertStockTargets(stockTargets)

            Log.d("StockNoteCallBack", "pre inserting data ok")


            //TabPinnedStockTargetList
            //TabToppedStockTargetList

        }
    }


    @Module
    @InstallIn(SingletonComponent::class)
    object AppModule {

        @Singleton
        @Provides
        fun provideMyBrainDataBase(
            @ApplicationContext applicationContext: Context,
            provider: Provider<StockNoteDao>,
            providerTarget: Provider<StockTargetDao>
        ) = Room.databaseBuilder(
            applicationContext, MyBrainDatabase::class.java, MyBrainDatabase.DATABASE_NAME
        ).addCallback(
            StockNoteCallBack(provider, providerTarget)
        )/*.addMigrations(MIGRATION_1_2)*/.build()


        @Singleton
        @Provides
        fun provideStockDailyNoteDao(myBrainDatabase: MyBrainDatabase): StockNoteDao =
            myBrainDatabase.stockNoteDao()


        @Singleton
        @Provides
        fun provideStockTargetDao(myBrainDatabase: MyBrainDatabase): StockTargetDao =
            myBrainDatabase.stockTargetDao()

        @Singleton
        @Provides
        fun provideStockTargetRepository(stockTargetDao: StockTargetDao): StockTargetRepository =
            StockTargetRepositoryImpl(stockTargetDao)

        @Singleton
        @Provides
        fun provideStockDailyNoteRepository(stockNoteDao: StockNoteDao): StockNoteRepository =
            StockNoteRepositoryImpl(stockNoteDao)


        @Singleton
        @Provides
        fun provideTargetActionHolderRepository(stockNoteDao: StockNoteDao): TargetMetaRepository =
            TargetMetaRepositoryImpl(stockNoteDao)


        @Singleton
        @Provides
        fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository =
            SettingsRepositoryImpl(context.dataStore)

        @Singleton
        @Provides
        fun provideAppContext(@ApplicationContext context: Context) = context

    }
}

