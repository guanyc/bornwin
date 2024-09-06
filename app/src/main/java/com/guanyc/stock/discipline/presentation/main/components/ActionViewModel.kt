package com.guanyc.stock.discipline.presentation.main.components


import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import com.guanyc.stock.discipline.domain.model.PinnedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.TargetConstants
import com.guanyc.stock.discipline.domain.model.ToppedTabEntityStockTargetList
import com.guanyc.stock.discipline.domain.use_case.settings.GetSettingsUseCase
import com.guanyc.stock.discipline.domain.use_case.settings.SaveSettingsUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.AddStockTargetUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.GetLatestStockNoteUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.InsertStockNoteUseCase
import com.guanyc.stock.discipline.presentation.stocks.JsonLoader
import com.guanyc.stock.discipline.presentation.stocks.marketWords
import com.guanyc.stock.discipline.presentation.targetconsts.GetTargetMetaUseCase
import com.guanyc.stock.discipline.presentation.targetconsts.TargetConstantsUpdateUseCase
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.containsStockCode
import com.guanyc.stock.discipline.util.getStockCode
import com.guanyc.stock.discipline.util.settings.NewTargetCreateDateChoice
import com.guanyc.stock.discipline.util.settings.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

//金溢科技

@HiltViewModel
class ActionViewModel @Inject constructor(

    private val jsonLoader: JsonLoader,

    private val getSettings: GetSettingsUseCase,
    private val saveSetting: SaveSettingsUseCase,
    private val getLastStockNote: GetLatestStockNoteUseCase,
    private val getTargetConstants: GetTargetMetaUseCase,
    private val updateTargetConstants: TargetConstantsUpdateUseCase,
    private val getStockTargetsUnCompleted: GetAllStockTargetsUnCompletedUseCase,
    private val updateStockTargetFavorite: UpdateStockTargetFavoriteUseCase,
    private val updateStockTargetCompleted: UpdateStockTargetCompletedUseCase,
    private val updateStockTargetComment: UpdateStockTargetCommentUseCase,
    private val updateStockTargets: UpdateStockTargetsUseCase,

    private val updateTabPinnedStockTargetList: UpdateTabPinnedStockTargetListUseCase,
    //private val insertTabPinnedStockTargetList: InsertTabPinnedStockTargetListUseCase,

    private val updateTabToppedStockTargetList: UpdateTabToppedStockTargetListUseCase,
    //private val insertTabToppedStockTargetList: InsertTabToppedStockTargetListUseCase,
    private val getTabPinnedStockTargetList: GetTabPinnedStockTargetListUseCase,
    private val getTabToppedStockTargetList: GetTabToppedStockTargetListUseCase,

    private val updateStockTargetTabEntities: UpdateStockTargetTabEntitiesUseCase,
    private val addStockTarget: AddStockTargetUseCase,
    private val insertStockNote: InsertStockNoteUseCase,
    @ApplicationContext val context: Context
    //    private val applicationContext: Context,

) : ViewModel() {


    data class UiState(

        //FIXME

        val dataChoice: Int = 0,

        val latestStockNote: StockNoteWithTargetLists? = null,

        val targetConstants: TargetConstants? = null,
        val isTargetConstantsInitialized: Boolean = false,

        val dateFrom: String = SimpleDateFormat("yyyyMMdd").format(Date()),
        val dateTo: String = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis() - 7 * 86400 * 1000)),


        //val showCompletedNotes: Boolean = false,

        val includingCompleted: Boolean = true,
        val includingUnCompleted: Boolean = true,

        val orderType: OrderType = OrderType.DESC(),


        //val actionToCodeListMap: MutableMap<TabEntity, MutableList<StockTarget>> = emptyMap<TabEntity, MutableList<StockTarget>>().toMutableMap(),

        //val reasonToCodeListMap: MutableMap<TabEntity, MutableList<StockTarget>> = emptyMap<TabEntity, MutableList<StockTarget>>().toMutableMap(),

        //val watchListToCodeListMap: MutableMap<TabEntity, MutableList<StockTarget>> = emptyMap<TabEntity, MutableList<StockTarget>>().toMutableMap(),


        val tabs: List<TabEntity> = emptyList<TabEntity>(),

        val stockTargets: List<StockTarget> = emptyList(),

        val currentTab: TabEntity = TabEntity(),
        val currentTabIndex: Int = 0,

        //val tabEntities: MutableList<TabEntity> = emptyList<TabEntity>().toMutableList(),

        val tabPinList: List<PinnedTabEntityStockTargetList> = emptyList<PinnedTabEntityStockTargetList>(),

        val tabTopList: List<ToppedTabEntityStockTargetList> = emptyList<ToppedTabEntityStockTargetList>(),

        )

    var uiState: UiState by mutableStateOf(UiState())

    val jsonContent = mutableStateOf<String?>(null)
    val codeNameMap = mutableMapOf<String, String>()
    private fun loadJsonContent() {
        jsonContent.value = jsonLoader.loadJson("stockcodes.json")
        codeNameMap.clear()

        val jsonArray: JSONArray = JSONArray(jsonContent.value)

        for (i in 0 until jsonArray.length()) {
            val stock = jsonArray.getJSONObject(i)
            val code = stock.getString("code")
            val name = stock.getString("name")
            if (!codeNameMap.containsKey(code)) {
                codeNameMap.put(code, name)
            }
            //println("Code: $code, Name: $name")
        }

    }

    init {

        loadJsonContent()

        initializeAndSequentiallyLoadData()
    }

    private fun initializeAndSequentiallyLoadData() {
        // 初始化设置和获取目标常量

        viewModelScope.launch {


            kotlinx.coroutines.flow.combine(

                getSettings(
                    intPreferencesKey(Constants.NEW_TARGET_CREATEDATE_CHOICE_KEY),
                    NewTargetCreateDateChoice.LATEST.value
                ),

                getTargetConstants(),

                getStockTargetsUnCompleted(),

                getTabPinnedStockTargetList(),

                getTabToppedStockTargetList(),

                getLastStockNote()

            ) {
                //targetConstants: TargetConstants,
                // stockTargets: List<StockTarget>,
                //pinnedTabEntityStockTargetList: List<PinnedTabEntityStockTargetList>,
                //toppedTabEntityStockTargetList: List<ToppedTabEntityStockTargetList>,
                //latestStockNote: StockNoteWithTargetLists
                    values ->
                uiState = uiState.copy(dataChoice = values[0] as Int)
                //targetConstants
                uiState = uiState.copy(targetConstants = values[1] as TargetConstants)
                uiState.targetConstants?.let {
                    uiState = uiState.copy(tabs = it.tabs)
                }
                //loadStockTargets
                uiState = uiState.copy(
                    //stockTargets
                    stockTargets = values[2] as List<StockTarget>,

                    //pinnedTabEntityStockTargetList
                    tabPinList = values[3] as List<PinnedTabEntityStockTargetList>,
                    //toppedTabEntityStockTargetList
                    tabTopList = values[4] as List<ToppedTabEntityStockTargetList>,
                    //latestStockNote
                    latestStockNote = values[5] as StockNoteWithTargetLists
                )


            }.collect {}

        }
    }


    fun onEvent(event: ActionViewEvent) {

        Log.d("onEvent", event.toString())

        when (event) {

            is ActionViewEvent.InitAll -> viewModelScope.launch {
                initializeAndSequentiallyLoadData()
            }

            is ActionViewEvent.addNewTabEntity -> viewModelScope.launch {

                event.tabEntity.let {

                    //update preference datastore
                    uiState.targetConstants?.let {
                        val tc = it.copy(tabs = it.tabs.toMutableList().apply {
                            add(0, event.tabEntity)
                        })
                        uiState.copy(targetConstants = tc)
                    }

                    if (uiState.targetConstants != null) {
                        updateTargetConstants(uiState.targetConstants!!)
                    }

                }
            }


            is ActionViewEvent.onItemEdited -> viewModelScope.launch {

                Log.d("onItemEdited", " $event")

                var old = event.tabEntityOld
                var edited = event.tabEntityEdited
                var index = event.index


                // 1 uiState.targetConstants
                // 2 uistate.tabs
                // 3 stockTargets (tabs) 更新


                event.tabEntityEdited.let {
                    uiState = uiState.copy(tabs = uiState.tabs.toMutableList().apply {
                        removeAt(index)
                        add(index, it)
                    })

                    uiState = uiState.copy(
                        targetConstants = uiState.targetConstants?.copy(tabs = uiState.tabs.toMutableList()
                            .apply {
                                removeAt(index)
                                add(index, it)
                            })
                    )

                    if (uiState.targetConstants != null) {
                        updateTargetConstants(uiState.targetConstants!!)
                    }
                }

                uiState.stockTargets.forEach {
                    if (it.tabs.any { it.title == old.title }) {
                        it.tabs =
                            it.tabs.map { if (it.title == old.title) it.copy(title = edited.title) else it }
                    }
                }

                updateStockTargets(uiState.stockTargets)
            }

            is ActionViewEvent.onItemDeleted -> viewModelScope.launch {
                var deleted = event.tabEntity
                var title = deleted.title
                var index = event.index

                event.tabEntity.let {
                    uiState = uiState.copy(tabs = uiState.tabs.toMutableList().apply {
                        remove(it)
                    })

                    uiState = uiState.copy(
                        targetConstants = uiState.targetConstants?.copy(tabs = uiState.tabs.toMutableList()
                            .apply {
                                removeAt(index)
                            })
                    )

                    if (uiState.targetConstants != null) {
                        updateTargetConstants(uiState.targetConstants!!)
                    }
                }

                uiState.stockTargets.forEach {
                    if (it.tabs.any { it.title == deleted.title }) {
                        it.tabs = it.tabs.filter { it.title != deleted.title }
                    }
                }
                updateStockTargets(uiState.stockTargets)

            }

            is ActionViewEvent.onItemsReordered -> viewModelScope.launch {


                var items = event.newItems
                var draggingAction = event.draggingAction
                var draggingItemIndex = event.draggingIndex

                if (items.size > 0) {
                    uiState = uiState.copy(tabs = items.toMutableList())

                    uiState = uiState.copy(
                        targetConstants = uiState.targetConstants?.copy(
                            tabs = items
                        )
                    )

                    if (uiState.targetConstants != null) {
                        updateTargetConstants(uiState.targetConstants!!)
                    }
                }

                //stockTargets的tabs 并未改变,仅仅顺序改了,可以不用改

            }

            is ActionViewEvent.onStockTargetFavoriteUpdated -> viewModelScope.launch {
                var stockTarget = event.stockTarget
                var favorite = event.favorite

                uiState.stockTargets.apply {
                    this.find { it.stockTargetId == stockTarget.stockTargetId }?.isFavorite =
                        favorite
                }

                updateStockTargetFavorite(stockTarget, favorite)
            }


            is ActionViewEvent.onStockTargetDialogDeleteClicked -> viewModelScope.launch {

                //删除的话 也就是 做completed 标记

                var stockTarget = event.stockTarget

                if (stockTarget.isCompleted == false) {
                    stockTarget.isCompleted = true
                    updateStockTargetCompleted(stockTarget)
                }

                uiState.stockTargets.apply {
                    this.find { it.stockTargetId == stockTarget.stockTargetId }?.isCompleted =
                        stockTarget.isCompleted
                }

            }

            is ActionViewEvent.onStockTargetCommentUpdated -> viewModelScope.launch {
                var stockTarget = event.stockTarget
                if (stockTarget.comment != event.newComment) {
                    stockTarget.comment = event.newComment
                    updateStockTargetComment(stockTarget)
                }
            }

            is ActionViewEvent.onSelectedTabIndexChanged -> viewModelScope.launch {
                var selectedTabIndex = event.selectedTabIndex
                var tabEntity = event.tabEntity
            }

            //力源信息

            is ActionViewEvent.onStockTargetTopPinned -> viewModelScope.launch {

                Log.d("onStockTargetTopPinned", event.toString())

                var stockTarget = event.stockTarget
                var pinTop = event.pinTop

                var tabEntity = event.tabEntity

                //var tab = uiState.tabEntities.indexOf(tabEntity)
                //var tabPinnedStockTargetList: MutableList<StockTarget>? = uiState.tabPinListMap.get(tabEntity)
                // uiState.reasonToCodeListMap.getOrPut(targetReason) { mutableListOf() }
                //                            .add(stockTarget)

                if (pinTop) {
                    Log.d("onStockTargetTopPinned event", event.toString())

                    var xx: PinnedTabEntityStockTargetList =
                        uiState.tabPinList.find { it.tab.title == tabEntity.title }!!

                    var yy: PinnedTabEntityStockTargetList =
                        xx.copy(pinnedList = xx.pinnedList.toMutableList().apply {
                            removeIf { it.stockTargetId == stockTarget.stockTargetId }
                            add(0, stockTarget)
                        })

                    updateTabPinnedStockTargetList(yy)

                    uiState = uiState.copy(tabPinList = uiState.tabPinList.toMutableList().apply {
                        remove(xx)
                        add(0, yy)
                    })
                    //yy b保存

                } else {//remove pin top

                    var xx: PinnedTabEntityStockTargetList =
                        uiState.tabPinList.find { it.tab.title == tabEntity.title }!!

                    xx = xx.copy(pinnedList = xx.pinnedList.toMutableList().apply {
                        removeIf({ it.stockTargetId == stockTarget.stockTargetId })
                    })

                    updateTabPinnedStockTargetList(xx)
                }
            }

            is ActionViewEvent.onStockTargetTopMoved -> viewModelScope.launch {
                var stockTarget = event.stockTarget
                var moveUp = event.moveTop
                var tabEntity = event.tabEntity

                var existedPinnedList =
                    uiState.tabPinList.find { it.tab.title == tabEntity.title }!!

                if (existedPinnedList.pinnedList.contains(stockTarget) || stockTarget.isTopPinned) {
                    return@launch
                }


                if (moveUp) {
                    var existedTopList: ToppedTabEntityStockTargetList =
                        uiState.tabTopList.find { it.tab.title == tabEntity.title }!!

                    existedTopList = existedTopList.copy(
                        toppedList = existedTopList.toppedList.toMutableList().apply {
                            removeIf({ it.stockTargetId == stockTarget.stockTargetId })
                            add(0, stockTarget)
                        })


                    updateTabToppedStockTargetList(existedTopList)

                    uiState = uiState.copy(tabTopList = uiState.tabTopList.toMutableList().apply {
                        add(0, existedTopList)
                    })

                } else {
                    var existedTopList: ToppedTabEntityStockTargetList =
                        uiState.tabTopList.find { it.tab.title == tabEntity.title }!!

                    existedTopList = existedTopList.copy(
                        toppedList = existedTopList.toppedList.toMutableList().apply {
                            removeIf({ it.stockTargetId == stockTarget.stockTargetId })
                        })

                    updateTabToppedStockTargetList(existedTopList)

                    uiState = uiState.copy(tabTopList = uiState.tabTopList.toMutableList().apply {
                        add(0, existedTopList)
                    })
                }
            }

            is ActionViewEvent.onItemTabEntitiesChanged -> viewModelScope.launch {
                var tabOld = event.tabOld
                var tabNew = event.tabNew

                var stockTarget = event.stockTarget

                stockTarget.tabs = tabNew

                updateStockTargetTabEntities(stockTarget, tabOld, tabNew, event.selectedIndex)

            }

            is ActionViewEvent.addNewFreshStockTarget -> viewModelScope.launch {

                if (uiState.dataChoice == NewTargetCreateDateChoice.LATEST.value) {
                    if (uiState.latestStockNote != null) {
                        val tempDate = Calendar.getInstance()

                        event.stockTarget.apply {
                            this.stockNoteId = uiState.latestStockNote!!.note.stockNoteId
                            this.createDate = uiState.latestStockNote!!.note.createDate
                        }

                        var stockTarget = event.stockTarget

                        if (!uiState.stockTargets.any { it.code.equals(stockTarget.code) }) {
                            Log.d("addNewFreshStockTarget Latest", stockTarget.toString())
                            addStockTarget(stockTarget)

                            uiState = getStockTargetsUnCompleted().firstOrNull()
                                ?.let { uiState.copy(stockTargets = it) }!!

                        }


                    } else {

                        val tempDate = Calendar.getInstance()
                        val date = Date(tempDate.timeInMillis)
                        val df = SimpleDateFormat("yyyyMMdd")
                        var locale = Locale.getDefault()
                        val createDate = df.format(date)
                        val stockNote = StockNote(
                            createDate = createDate,
                        )

                        var id = insertStockNote(stockNote)


                        var stockTarget = event.stockTarget
                        stockTarget.stockNoteId = id
                        stockTarget.createDate = createDate

                        if (!uiState.stockTargets.any { it.code.equals(stockTarget.code) }) {
                            Log.d("addNewFreshStockTarget Today", stockTarget.toString())

                            addStockTarget(stockTarget)

                            uiState =
                                uiState.copy(latestStockNote = getLastStockNote().firstOrNull())
                            uiState = getStockTargetsUnCompleted().firstOrNull()
                                ?.let { uiState.copy(stockTargets = it) }!!
                        }


                    }
                } else {
                    //  if(uiState.dataChoice == NewTargetCreateDateChoice.TODAY.value){
                    val todayStr = SimpleDateFormat("yyyyMMdd").format(Date())
                    val latestStr: String = uiState.latestStockNote!!.note.createDate

                    var stockTarget = event.stockTarget


                    if (latestStr != todayStr) {
                        val stockNote = StockNote(
                            createDate = todayStr,
                        )
                        val id = insertStockNote(stockNote)
                        stockTarget.createDate = todayStr
                        stockTarget.stockNoteId = id

                        uiState = uiState.copy(latestStockNote = getLastStockNote().firstOrNull())
                    } else {
                        val stockNote = uiState.latestStockNote!!.note
                        stockTarget.stockNoteId = stockNote.stockNoteId
                        stockTarget.createDate = stockNote.createDate
                    }

                    stockTarget.createDate = todayStr

                    if (!uiState.stockTargets.any { it.code.equals(stockTarget.code) }) {
                        addStockTarget(stockTarget)

                        uiState = uiState.copy(latestStockNote = getLastStockNote().firstOrNull())
                        uiState = getStockTargetsUnCompleted().firstOrNull()
                            ?.let { uiState.copy(stockTargets = it) }!!
                    }


                }

                initializeAndSequentiallyLoadData()

            }

            is ActionViewEvent.OnScanPhoto -> viewModelScope.launch {

                val currentLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.resources.configuration.locales[0] // For Android N and above
                } else {
                    context.resources.configuration.locale // For pre-Nougat Android versions
                }

                val language = currentLocale.language // e.g., "en"
                val country = currentLocale.country // e.g., "US"
                //val locale = "$language-$country"


                var resultTexts = emptyList<String>().toMutableList()
                var lines = emptyList<String>().toMutableList()

                for (block: Text.TextBlock in event.visionText.textBlocks) {
                    //val blockText = block.text
                    //val blockCornerPoints = block.cornerPoints
                    //val blockFrame = block.boundingBox

                    for (line in block.lines) {
                        val lineText = line.text
                        if (!resultTexts.contains(lineText)) {
                            Log.d("lineText", lineText)
                            resultTexts.add(lineText)
                        }

                        //val lineCornerPoints = line.cornerPoints
                        //val lineFrame = line.boundingBox
                        /*for (element in line.elements) {
                            val elementText = element.text
                            val elementCornerPoints = element.cornerPoints
                            val elementFrame = element.boundingBox
                        }*/
                    }
                }

                for (item in resultTexts) {
                    if ((item.isBlank() || item.isEmpty() || item.trim().isBlank() || item.trim()
                            .isEmpty())
                    ) {
                        continue
                    }
                    if (marketWords.any { it ->
                            it.contains(item.trim()) || item.trim().contains(it.trim())
                        }) {
                        continue
                    }

                    if (item.matches("\\b\\d+\\.\\d+\\b".toRegex())) {
                        //Log.d("containsStockCode?", "end element is :" + item)
                        continue
                    }

                    if (containsStockCode(item)) {
                        var stockcode = getStockCode(item.trim())
                        //Log.d("containsStockCode?", "stockcode is :" + stockcode)
                        if (!lines.contains(stockcode)) {
                            lines.add(stockcode)
                        }
                        continue
                    }
                }// for resultlines

                var filteredStockCodes =
                    lines.filter { stockCode -> codeNameMap.containsKey(stockCode) }


                if (uiState.dataChoice == NewTargetCreateDateChoice.LATEST.value) {
                    val latestStockNote = uiState.latestStockNote!!.note
                    if (filteredStockCodes.isNotEmpty()) {
                        try {
                            for (stockCode in filteredStockCodes) {
                                val stockTarget = StockTarget(
                                    stockNoteId = latestStockNote.stockNoteId,
                                    createDate = latestStockNote.createDate,
                                    code = stockCode,
                                    name = codeNameMap[stockCode]!!
                                )


                                if (!uiState.stockTargets.any { it.code.equals(stockTarget.code) }) {
                                    addStockTarget(stockTarget)

                                    uiState =
                                        uiState.copy(latestStockNote = getLastStockNote().firstOrNull())
                                    uiState = getStockTargetsUnCompleted().firstOrNull()
                                        ?.let { uiState.copy(stockTargets = it) }!!
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                } else if (uiState.dataChoice == NewTargetCreateDateChoice.TODAY.value) {
                    if (filteredStockCodes.isNotEmpty()) {

                        val tempDate = Calendar.getInstance()
                        val date = Date(tempDate.timeInMillis)
                        val df = SimpleDateFormat("yyyyMMdd")
                        val createDate = df.format(date)

                        val stockNote = StockNote(createDate = createDate)

                        //TODO 最后的stocknote或者今天

                        try {
                            val stockNoteId = insertStockNote(stockNote = stockNote)

                            for (stockCode in filteredStockCodes) {
                                val stockTarget = StockTarget(
                                    stockNoteId = stockNoteId,
                                    createDate = createDate,
                                    code = stockCode,
                                    name = codeNameMap[stockCode]!!
                                )

                                if (!uiState.stockTargets.any { it.code.equals(stockTarget.code) }) {
                                    addStockTarget(stockTarget)

                                    uiState =
                                        uiState.copy(latestStockNote = getLastStockNote().firstOrNull())
                                    uiState = getStockTargetsUnCompleted().firstOrNull()
                                        ?.let { uiState.copy(stockTargets = it) }!!
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    }
}

sealed class ActionViewEvent {

    data class OnScanPhoto(val visionText: Text) : ActionViewEvent()


    data class addNewTabEntity(val tabEntity: TabEntity) : ActionViewEvent()

    data class onItemDeleted(val tabEntity: TabEntity, val index: Int) : ActionViewEvent()
    data class onItemEdited(
        val tabEntityOld: TabEntity, val tabEntityEdited: TabEntity, val index: Int
    ) : ActionViewEvent()

    data class onItemsReordered(
        val draggingAction: Int, val draggingIndex: Int, val newItems: List<TabEntity>
    ) : ActionViewEvent()

    data class onStockTargetFavoriteUpdated(val stockTarget: StockTarget, val favorite: Boolean) :
        ActionViewEvent()

    data class onStockTargetDialogDeleteClicked(val stockTarget: StockTarget) : ActionViewEvent()

    data class onStockTargetCommentUpdated(val stockTarget: StockTarget, val newComment: String) :
        ActionViewEvent()

    data class onStockTargetTopPinned(
        val stockTarget: StockTarget, val tabEntity: TabEntity, val pinTop: Boolean
    ) : ActionViewEvent()


    data class onSelectedTabIndexChanged(val tabEntity: TabEntity, val selectedTabIndex: Int) :
        ActionViewEvent()

    data class onStockTargetTopMoved(
        val stockTarget: StockTarget, val tabEntity: TabEntity, val moveTop: Boolean
    ) : ActionViewEvent()


    data class addNewFreshStockTarget(val stockTarget: StockTarget) : ActionViewEvent()

    data class onItemTabEntitiesChanged(
        val stockTarget: StockTarget,
        val tabOld: List<TabEntity>,
        val tabNew: List<TabEntity>,
        val selectedIndex: Int
    ) : ActionViewEvent()

    object InitAll : ActionViewEvent()
    companion object

}
