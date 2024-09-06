package com.guanyc.stock.discipline.presentation.stocks

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guanyc.stock.discipline.domain.use_case.stocks.UpdateStockTargetUseCase
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.use_case.stocks.CompleteStockNoteUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.DeleteStockTargetUseCase
import com.guanyc.stock.discipline.domain.use_case.settings.GetSettingsUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.DeleteStockNoteUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.GetStockNoteWithTargetListsUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.InsertOrUpdateStockTargetListUseCase
import com.guanyc.stock.discipline.domain.use_case.stocks.UpdateStockNoteUseCase
import com.guanyc.stock.discipline.presentation.targetconsts.GetTargetMetaUseCase
import com.guanyc.stock.discipline.domain.model.TargetConstants
import com.guanyc.stock.discipline.util.BackupUtil.toJson
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.settings.Order
import com.guanyc.stock.discipline.util.settings.OrderType
import com.guanyc.stock.discipline.util.settings.toInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StockNoteDetailEvent {

    object ErrorDisplayed : StockNoteDetailEvent()


    data class GetStockNoteWithTargetLists(val stockNoteId:Long): StockNoteDetailEvent()

    data class DeleteStockNoteEvent(val note: StockNote) : StockNoteDetailEvent()

    data class UpdateStockNoteEvent(val note: StockNote) : StockNoteDetailEvent()
    data class CompleteStockNoteEvent(val stockNote: StockNote, val isCompleted: Boolean) :
        StockNoteDetailEvent()


    data class DeleteStockTargetEvent(val stockTarget: StockTarget) : StockNoteDetailEvent()

    data class InsertOrUpdateStockTargetListEvent(val targets: List<StockTarget>) :
        StockNoteDetailEvent()

    data class UpdateStockTargetEvent(val update: StockTarget) : StockNoteDetailEvent()
    data class UpdateStockNoteAndStockTargetEvent(val stockNote: StockNote, val stockTarget: StockTarget) : StockNoteDetailEvent()

}

data class NoteDetailUiState(

    val targetConstants: TargetConstants? = null,

    val item: StockNoteWithTargetLists? = null,

    val navigateUp: Boolean = false, val error: String? = null
)



/*

如何给hiltviewmodel传递参数  这里的hilt compose版本比较低 不会应用此文最后的最新的版本

How to Pass Arguments to a HiltViewModel from Compose
https://medium.com/@cgaisl/how-to-pass-arguments-to-a-hiltviewmodel-from-compose-97c74a75f772

With the release of version 1.2.0-alpha01 of the Hilt Navigation Compose library comes a new composable hiltViewModel() function, which takes a callback with our AssistedFactory with which we can initialize our HiltViewModel with runtime parameters.

@HiltViewModel(assistedFactory = DetailViewModel.DetailViewModelFactory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted val id: String,
    val exampleRepository: ExampleRepository,
) : ViewModel() {

    @AssistedFactory
    interface DetailViewModelFactory {
        fun create(id: String): DetailViewModel
    }
}

@Composable
fun DetailScreen(
    id: String,
) {
    val viewModel = hiltViewModel<DetailViewModel, DetailViewModel.DetailViewModelFactory> { factory ->
        factory.create(id)
    }
}
 */


@HiltViewModel
class StockNoteListItemDetailViewModel @Inject constructor(
    private val getSettings: GetSettingsUseCase,

    private val updateStockNote: UpdateStockNoteUseCase,

    private val completeStockDailyNote: CompleteStockNoteUseCase,
    private val deleteStockTarget: DeleteStockTargetUseCase,
    private val insertOrUpdateStockTargetList: InsertOrUpdateStockTargetListUseCase,
    private val deleteStockNote: DeleteStockNoteUseCase,

    private val getTargetConstants: GetTargetMetaUseCase,

    private val updateStockTarget: UpdateStockTargetUseCase,
    private val getStockNoteWithTargetLists: GetStockNoteWithTargetListsUseCase

) : ViewModel() {

    var uiState by mutableStateOf(NoteDetailUiState())
        private set


    private var getNotesJob: Job? = null

    init {


        viewModelScope.launch {

            kotlinx.coroutines.flow.combine(
                getSettings(
                    intPreferencesKey(Constants.STOCK_DAILY_NOTE_ORDER_KEY),
                    Order.DateCreated(OrderType.DESC()).toInt()
                ),

                getTargetConstants(),

            ) { order: Int, targetConstants ->



                uiState = uiState.copy(targetConstants = targetConstants)

                Log.d("targetConstants", targetConstants.toJson())

                //
                //StockNoteDetailEvent.GetStockNoteWithTargetLists(stockNoteId = stockNoteId)


            }.collect()

        }
    }




    fun onEvent(event: StockNoteDetailEvent) {
        when (event) {

            is StockNoteDetailEvent.UpdateStockTargetEvent -> viewModelScope.launch {
                updateStockTarget(event.update)
            }
            is StockNoteDetailEvent.UpdateStockNoteAndStockTargetEvent -> viewModelScope.launch {

                var stocknote = event.stockNote
                var stockTarget = event.stockTarget

                updateStockTarget(event.stockTarget)
                updateStockNote(event.stockNote)

                if(event.stockNote.isCompleted){
                    var tml = uiState.item?.stockTargets?.toMutableList()
                    if(tml!=null) {
                        var completedTargets = tml.map { it.copy(isCompleted = true) }
                        insertOrUpdateStockTargetList(completedTargets)
                    }
                }

                getStockNoteWithTargetLists(event.stockNote.stockNoteId).onEach {
                    uiState = uiState.copy(item = it)
                }.collect()

                /*
                var ml = uiState.item

                var index = uiState.notes.indexOfFirst { x -> x.note == stocknote }

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

                uiState = uiState.copy(notes = ml)
                */
            }


            is StockNoteDetailEvent.GetStockNoteWithTargetLists -> viewModelScope.launch {
                getStockNoteWithTargetLists(event.stockNoteId).onEach {
                    uiState = uiState.copy(item = it)
                }.collect()
            }

            is StockNoteDetailEvent.DeleteStockTargetEvent -> viewModelScope.launch {
                deleteStockTarget.invoke(event.stockTarget)
            }

            is StockNoteDetailEvent.DeleteStockNoteEvent -> viewModelScope.launch {
                deleteStockNote.invoke(event.note)
                //event.note.dueDate!=0L deleteAlarm(event.note.id)
                uiState = uiState.copy(navigateUp = true)
            }

            is StockNoteDetailEvent.ErrorDisplayed -> {
                uiState = uiState.copy(error = null)
            }


            is StockNoteDetailEvent.UpdateStockNoteEvent -> viewModelScope.launch {

                try {
                    //android.database.sqlite.SQLiteConstraintException: UNIQUE constraint failed: stocknotes.createDate
                    updateStockNote(event.note)
                    uiState = uiState.copy(navigateUp = true)
                } catch (e: Exception) {
                    uiState = uiState.copy(error = e.message)
                }
            }

            is StockNoteDetailEvent.CompleteStockNoteEvent -> viewModelScope.launch {



                completeStockDailyNote(event.stockNote.stockNoteId, event.isCompleted)
                //TODO 重新实现这个

                //true //TODO check this set isCompleted is all truye
                if(event.isCompleted){
                    var tml = uiState.item?.stockTargets?.toMutableList()
                    if(tml!=null) {
                        var completedTargets = tml.map { it.copy(isCompleted = true) }
                        insertOrUpdateStockTargetList(completedTargets)
                    }
                }

            }


            is StockNoteDetailEvent.InsertOrUpdateStockTargetListEvent -> viewModelScope.launch {

                insertOrUpdateStockTargetList(event.targets)

            }
        }
    }


}