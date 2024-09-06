package com.guanyc.stock.discipline.presentation.stocks

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.TargetConstants
import com.guanyc.stock.discipline.domain.use_case.settings.GetSettingsUseCase
import com.guanyc.stock.discipline.domain.use_case.settings.SaveSettingsUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.AddStockTargetUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.CompleteStockNoteUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.CompleteStockTargetUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.DeleteStockNoteUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.DeleteStockTargetUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.GetAllStockNoteWithTargetListsUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.GetStockNoteForDateUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.InsertStockNoteUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.UpdateStockTargetUseCase
import com.guanyc.stock.discipline.presentation.targetconsts.GetTargetMetaUseCase
import com.guanyc.stock.discipline.theme.Rubik
import com.guanyc.stock.discipline.util.BackupUtil.toJson
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.containsStockCode
import com.guanyc.stock.discipline.util.getStockCode
import com.guanyc.stock.discipline.util.settings.OrderType
import com.guanyc.stock.discipline.util.settings.StartUpScreenSettings
import com.guanyc.stock.discipline.util.settings.ThemeSettings
import com.guanyc.stock.discipline.util.settings.toInt
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.annotation.Nullable
import javax.inject.Inject


class JsonLoader @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun loadJson(fileName: String): String {
        context.assets.open(fileName).bufferedReader().use {
            return it.readText()
        }
    }
}


@HiltViewModel
open class StockNoteListViewModel @Inject constructor(

    private val jsonLoader: JsonLoader,
    private val getSettings: GetSettingsUseCase,
    private val saveSettings: SaveSettingsUseCase,

    private val completeStockDailyNote: CompleteStockNoteUseCase,
    private val completeStockTarget: CompleteStockTargetUseCase,


    private val deleteStockNote: DeleteStockNoteUseCase,
    private val insertStockNote: InsertStockNoteUseCase,
    private val addStockTarget: AddStockTargetUseCase,

    private val deleteStockTarget: DeleteStockTargetUseCase,
    private val updateStockTarget: UpdateStockTargetUseCase,
    private val getTargetConstants: GetTargetMetaUseCase,

    private val getAllStockNoteWithTargetListsUseCase: GetAllStockNoteWithTargetListsUseCase,
    private val getStockNoteForDate: GetStockNoteForDateUseCase,

    @ApplicationContext val context: Context

) : ViewModel() {


    data class UiState(

        val hasSameStockNoteOnTheDay: Boolean = false,

        val targetConstants: TargetConstants? = null,

        val stockNoteId: Long = -1,

        // val noteView: ItemView = ItemView.LIST,

        val navigateUp: Boolean = false,

        val readingMode: Boolean = true,

        val stockNoteWithTargetListsList: List<StockNoteWithTargetLists> = emptyList(),

        val showCompletedNotes: Boolean = false,

        val lines: List<String> = emptyList<String>(),

        val expandListByTargetReasons: Boolean = true,

        val includingCompleted: Boolean = true,
        val includingUnCompleted: Boolean = true,

        val dateFrom: String = SimpleDateFormat("yyyyMMdd").format(Date()),
        val dateTo: String = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis() - 7 * 86400 * 1000)),
        val orderType: OrderType = OrderType.DESC(),

        @Nullable val error: String? = null,
        val queryStringForActionReview: String = "",

        val targetListVisible: Boolean = true
    )


    var uiState by mutableStateOf(UiState())

    private var getNotesJob: Job? = null

    val jsonContent = mutableStateOf<String?>(null)
    val codeNameMap = mutableMapOf<String, String>();


    //private var getNotesJob2: Job? = null

    private fun loadJsonContent() {
        jsonContent.value = jsonLoader.loadJson("stockcodes.json")
        codeNameMap.clear();

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
        initFun()
    }

    fun initFun() {
        //val date = Date()
        //val dateFormat = SimpleDateFormat("yyyyMMdd")

        Log.d("GetAllStockNotes", "init")

        viewModelScope.launch {

            kotlinx.coroutines.flow.combine(

                getTargetConstants(),

                ) { targetConstants: Array<TargetConstants> ->

                GetAllStockNotes(
                    orderType = uiState.orderType,
                    includingCompleted = uiState.includingCompleted,
                    includingUnCompleted = uiState.includingUnCompleted,
                    dateFrom = uiState.dateFrom,
                    dateTo = uiState.dateTo
                )

                uiState = uiState.copy(
                    targetConstants = targetConstants.firstOrNull(),
                )


                Log.d("StockNoteListViewModel", uiState.targetConstants.toJson())


            }.collect()

            Log.d("init", uiState.stockNoteWithTargetListsList.toJson())
        }
    }


    private fun GetAllStockNotes(
        orderType: OrderType = OrderType.ASC(),
        includingCompleted: Boolean = false,
        includingUnCompleted: Boolean = true,
        dateFrom: String = uiState.dateFrom,
        dateTo: String = uiState.dateTo
    ) {

        Log.d("GetAllStockNotes", "getNotes")

        getNotesJob?.cancel()

        getNotesJob = getAllStockNoteWithTargetListsUseCase(orderType, includingCompleted).onEach {

                item: List<StockNoteWithTargetLists> ->

            Log.d("onEach", item.toJson())

            uiState = uiState.copy(
                stockNoteWithTargetListsList = if (orderType == OrderType.ASC()) item.sortedBy { it -> it.note.createDate }
                else item.sortedByDescending { it -> it.note.createDate },
                showCompletedNotes = includingCompleted,
            )
        }.launchIn(viewModelScope)
    }


    val themeMode = getSettings(
        intPreferencesKey(Constants.SETTINGS_THEME_KEY), ThemeSettings.AUTO.value
    )

    val defaultStartUpScreen = getSettings(
        intPreferencesKey(Constants.DEFAULT_START_UP_SCREEN_KEY), StartUpScreenSettings.SPACES.value
    )


    val font = getSettings(intPreferencesKey(Constants.APP_FONT_KEY), Rubik.toInt())

    val blockScreenshots =
        getSettings(booleanPreferencesKey(Constants.BLOCK_SCREENSHOTS_KEY), false)


    /**
     *
     */
    fun onEvent(event: StockNoteListEvent) {

        when (event) {

            is StockNoteListEvent.GetStockNoteForDate -> viewModelScope.launch {
                val createDate = event.createDate

                val stockNotesOnTheSameDay: List<StockNote> = getStockNoteForDate(createDate)

                if (stockNotesOnTheSameDay.isNotEmpty()) {

                    uiState = uiState.copy(hasSameStockNoteOnTheDay = true)
                    uiState =
                        uiState.copy(error = context.getString(R.string.error_message_create_date_unique))

                } else {
                    uiState = uiState.copy(hasSameStockNoteOnTheDay = false)
                    uiState = uiState.copy(error = null)
                }

            }

            is StockNoteListEvent.UpdateStockTarget -> viewModelScope.launch {

                var stocknote = event.stockNote
                var stockTarget = event.stockTarget

                updateStockTarget(stockTarget)

                var ml = uiState.stockNoteWithTargetListsList.toMutableList()

                var index =
                    uiState.stockNoteWithTargetListsList.indexOfFirst { x -> x.note == stocknote }

                var element = ml.elementAt(index)
                var elementTargets = element.stockTargets;

                var targetIndex =
                    elementTargets.indexOfFirst { item -> item.stockTargetId == event.stockTarget.stockTargetId }
                var target = elementTargets.get(targetIndex)

                var tml = elementTargets.toMutableList()
                tml.removeAt(targetIndex)
                tml.add(targetIndex, event.stockTarget)


                var newElement: StockNoteWithTargetLists =
                    element.copy(note = stocknote, stockTargets = tml)

                ml.removeAt(index)
                ml.add(index, newElement)

                uiState = uiState.copy(stockNoteWithTargetListsList = ml)
            }

            is StockNoteListEvent.DeleteStockTarget -> viewModelScope.launch {
                var note = event.stockNoteWithTargetLists
                var targets = event.stockNoteWithTargetLists.stockTargets
                var target = event.stockTarget


                var ml = uiState.stockNoteWithTargetListsList.toMutableList()
                var index = uiState.stockNoteWithTargetListsList.indexOf(note)
                var element = ml.elementAt(index) //和上面的note 一样的
                var itemnote = element.note
                var newtargets = element.stockTargets.toMutableList()
                newtargets.remove(target)

                ml.removeAt(index);
                ml.add(index, element.copy(note = itemnote, stockTargets = newtargets))


                uiState = uiState.copy(
                    stockNoteWithTargetListsList = ml
                )

                deleteStockTarget(target)

            }

            is StockNoteListEvent.AddStockTarget -> viewModelScope.launch {

                Log.d("stockTarget", event.stockTarget.toJson())
                var stockTargetId = addStockTarget(event.stockTarget)

            }

            is StockNoteListEvent.InsertStockNote -> viewModelScope.launch {
                try {
                    val id = insertStockNote(event.stockNote)

                    uiState = uiState.copy(error = null)
                    uiState = uiState.copy(stockNoteId = id)

                    Log.d("InsertStockNote", event.stockNote.toJson())

                } catch (e: IllegalArgumentException) {
                    //e.printStackTrace()
                    Log.d("InsertStockNote", e.toString())
                    uiState =
                        uiState.copy(error = "日记的createDate日期字段是唯一的, 不能创建具有相同日期的两个条目")
                }
            }

            is StockNoteListEvent.DeleteStockNotesWithTargets -> viewModelScope.launch {

                var note = event.stockNote
                //entity class 定义了 外键 ondelete cascade
                deleteStockNote(note)

                //var x: StockNoteWithTargetLists? =
                //  notesUiState.notes.find { item -> item.note.stockNoteId == note.stockNoteId }
            }

            is StockNoteListEvent.ErrorDisplayed -> {
                uiState = uiState.copy(error = null, hasSameStockNoteOnTheDay = false)

            }

            is StockNoteListEvent.ShowCompletedTasks -> viewModelScope.launch {
                saveSettings(
                    booleanPreferencesKey(Constants.SHOW_COMPLETED_ITEMS_VIEW_LIST_KEY),
                    event.showCompleted
                )
                uiState = uiState.copy(showCompletedNotes = event.showCompleted)

            }

            is StockNoteListEvent.CompleteStockNote -> viewModelScope.launch {

                completeStockDailyNote(event.stockNote.stockNoteId, !event.isCompleted)

                if (event.isCompleted) {
                    //TODO deleteStockDailyNoteAlarm (event.stockNote.stockDailyNoteId)
                }
            }

            is StockNoteListEvent.SearchTasks -> TODO()

            is StockNoteListEvent.CompleteStockTarget -> viewModelScope.launch {
                completeStockTarget(event.stockTarget, event.isCompleted)
            }

            is StockNoteListEvent.OnScanPhoto -> viewModelScope.launch {


                var resultTexts = emptyList<String>().toMutableList()
                var lines = emptyList<String>().toMutableList()

                for (block: Text.TextBlock in event.visionText.textBlocks) {
                    val blockText = block.text
                    val blockCornerPoints = block.cornerPoints
                    val blockFrame = block.boundingBox

                    for (line in block.lines) {
                        val lineText = line.text
                        if (!resultTexts.contains(lineText)) {
                            Log.d("lineText", lineText)
                            resultTexts.add(lineText)
                        }

                        val lineCornerPoints = line.cornerPoints
                        val lineFrame = line.boundingBox
                        for (element in line.elements) {
                            val elementText = element.text
                            val elementCornerPoints = element.cornerPoints
                            val elementFrame = element.boundingBox
                        }
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

                if (filteredStockCodes.isNotEmpty()) {

                    val tempDate = Calendar.getInstance()
                    val date = Date(tempDate.timeInMillis)
                    val df = SimpleDateFormat("yyyyMMdd")
                    val createDate = df.format(date)

                    val stockNote = StockNote(createDate = createDate)

                    try {
                        val id = insertStockNote(stockNote = stockNote)

                        uiState = uiState.copy(stockNoteId = id)

                        for (stockCode in filteredStockCodes) {
                            val stockTarget = StockTarget(
                                stockNoteId = id,
                                code = stockCode,
                                name = codeNameMap[stockCode]!!,
                                createDate = createDate,
                            )
                            addStockTarget(stockTarget)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            is StockNoteListEvent.ExpandListByTargetReasons -> viewModelScope.launch {

                saveSettings(
                    booleanPreferencesKey(Constants.EXPAND_LIST_BY_TARGET_REASONS),
                    event.expandListByTargetReasons
                )
                uiState = uiState.copy(expandListByTargetReasons = event.expandListByTargetReasons)

            }

            StockNoteListEvent.QueryChanged -> viewModelScope.launch {
                if (uiState.queryStringForActionReview.isNotBlank()) {
                    Log.d("queryStringForActionReview", uiState.queryStringForActionReview)
                    //TODO
                } else {
                    Log.d("queryStringForActionReview", "blank")
                    //TODO
                }
            }

            StockNoteListEvent.CollapseWholeListScreen -> {
                uiState.stockNoteWithTargetListsList.forEach { it ->
                    it.note.targetListVisible = false
                }
            }

            StockNoteListEvent.ExpandWholeListScreen -> {
                uiState.stockNoteWithTargetListsList.forEach { it ->
                    it.note.targetListVisible = true
                }
            }
        }
    }


}


sealed class StockNoteListEvent {

    object QueryChanged : StockNoteListEvent()

    data class DeleteStockNotesWithTargets(val stockNote: StockNote) : StockNoteListEvent()

    //data class UpdateOrder(val order: Order) : StockNoteListEvent()


    data class ExpandListByTargetReasons(val expandListByTargetReasons: Boolean = false) :
        StockNoteListEvent()

    data class ShowCompletedTasks(val showCompleted: Boolean) : StockNoteListEvent()

    data class CompleteStockNote(val stockNote: StockNote, val isCompleted: Boolean) :
        StockNoteListEvent()

    data class CompleteStockTarget(val stockTarget: StockTarget, val isCompleted: Boolean) :
        StockNoteListEvent()

    data class SearchTasks(val query: String) : StockNoteListEvent()

    data class InsertStockNote(val stockNote: StockNote) : StockNoteListEvent()
    data class AddStockTarget(val stockTarget: StockTarget) : StockNoteListEvent()
    data class DeleteStockTarget(
        val stockTarget: StockTarget, val stockNoteWithTargetLists: StockNoteWithTargetLists
    ) : StockNoteListEvent()

    data class UpdateStockTarget(
        val stockNote: StockNote, val stockTarget: StockTarget
    ) : StockNoteListEvent()

    data class GetStockNoteForDate(val createDate: String) : StockNoteListEvent()
    data class OnScanPhoto(val visionText: Text) : StockNoteListEvent()

    object ErrorDisplayed : StockNoteListEvent()

    companion object {

    }

    object ExpandWholeListScreen : StockNoteListEvent() {}

    object CollapseWholeListScreen : StockNoteListEvent() {}

}


/*
is StockNoteListEvent.ScanPhotoFromURIEvent -> {

                val image: InputImage = InputImage.fromFilePath( LocalContext.current, event.imageUri!!)

                val recognizer =
                    TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())

                recognizer.process(image).addOnSuccessListener { visionText ->
                    // Task completed successfully
                    // ...
                    Log.d("visionText", visionText.toString())

                    var resultTexts = mutableListOf<String>()

                    var lines = mutableListOf<String>()

                    for (block: Text.TextBlock in visionText.textBlocks) {
                        val blockText = block.text
                        val blockCornerPoints = block.cornerPoints
                        val blockFrame = block.boundingBox

                        if (!resultTexts.contains(blockText.toString())) {
                            resultTexts.add(blockText.toString())
                        }

                        for (line in block.lines) {

                            val lineText = line.text
                            val lineCornerPoints = line.cornerPoints
                            val lineFrame = line.boundingBox
                            for (element in line.elements) {
                                val elementText = element.text

                                val elementCornerPoints = element.cornerPoints
                                val elementFrame = element.boundingBox
                            }
                        }
                    }

                    var started = false;

                    for ((index, item) in resultTexts.withIndex()) {
                        //futu, 同花顺, tdx
                        if (item.contains("自选")
                            || item.contains("全部")
                            || item.contains("涨幅榜")) {
                            started = true;
                            continue
                        }

                        //[\u4e00-\u9fa5]{6}
                        if (isStockName(item)
                        ) {
                            lines.add(item)
                        } else if (containsStockCode(item)) {
                            if(lines.isEmpty()){
                                //bypass the first code there is no corresponding code name
                                continue;
                            }
                            lines.add(item)
                        }


                        if (item.matches("\\b\\d+\\.\\d+\\b".toRegex())
                            || item.contains("首页")
                            || item.contains(
                                "同花顺自选"
                            )
                        ) {
                            break;
                        }
                    }

                    while(containsStockCode(lines.first())){
                        lines.removeAt(0)
                    }

                    lines.forEach{
                        Log.d("lines", it)
                    }

                    uiState = uiState.copy(lines = lines)



                }.addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                    e.printStackTrace()
                    //uiState = uiState.copy(lines = emptyList())
                    uiState = uiState.copy(error = "扫描图片异常, 识别出错!")


                }

            }
 */


//futu, 同花顺, tdx
val marketWords = listOf(
    "自选",
    "全部",
    "涨幅榜",
    "资产",
    "市值",
    "持仓",
    "沪深",
    "名称",
    "代码",
    "创业板",
    "展开分析",
    "成分股",
    "价格",
    "涨跌",
    "行业",
    "板块",
    "首页",
    "行情",
    "自选",
    "同花顺自选",
    "资金",
    "最新服",
    "港股服",
    "美股",
    "涨幅",
    "板块",
    "简况",
    "F10",
    "最新价",
    "简况",
    "买入",
    "隐藏",
    "卖出",
    "浮动",
    "盈亏",
    "撤单",
    "基金",
    "买板块",
)