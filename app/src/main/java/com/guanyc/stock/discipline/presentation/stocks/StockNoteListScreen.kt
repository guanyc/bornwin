package com.guanyc.stock.discipline.presentation.stocks


//import com.google.accompanist.flowlayout.FlowRow  is deprecated

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.tabActions
import com.guanyc.stock.discipline.domain.model.tabReasons
import com.guanyc.stock.discipline.domain.model.targetActionList
import com.guanyc.stock.discipline.domain.model.targetReasonList
import com.guanyc.stock.discipline.presentation.main.components.TAB_TYPE
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import com.guanyc.stock.discipline.presentation.stocks.StockNoteListEvent.GetStockNoteForDate
import com.guanyc.stock.discipline.presentation.util.Screen
import com.guanyc.stock.discipline.theme.Golden
import com.guanyc.stock.discipline.util.BackupUtil.toJson
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.settings.StockNoteColor
import com.guanyc.stock.discipline.util.settings.toStockNoteColor
import isScanPhotoEnabled
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


// #1 添加stocknote



@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun StockNoteListScreen(
    navController: NavHostController,
    viewModel: StockNoteListViewModel = hiltViewModel(),
) {

    val context = LocalContext.current

    var uiState = viewModel.uiState

    val focusRequester = remember { FocusRequester() }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                uri?.let {
                    //Log.d("imageUri", imageUri.toString())
                    imageUri = it
                }
            })

    //val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)


    var allExpanded by remember { mutableStateOf(true) }
    var expandedItems by remember { mutableStateOf(listOf<Boolean>()) }


    BackHandler {
        if (sheetState.isVisible) scope.launch {
            sheetState.hide()
        }
        else navController.popBackStack()
    }


    LaunchedEffect(uiState) {
        Log.d("LaunchedEffect", "notesUiState")
        if (uiState.error != null) {
            scaffoldState.snackbarHostState.showSnackbar(uiState.error!!)
            viewModel.onEvent(StockNoteListEvent.ErrorDisplayed)
        }
    }



    Scaffold(
        scaffoldState = scaffoldState,

        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(R.string.stock_daily_note_list),
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                )
            }, backgroundColor = MaterialTheme.colors.background, elevation = 0.dp, actions = {
                IconButton(onClick = {
                    scope.launch {
                        //sheetState.show()
                        allExpanded = !allExpanded
                        expandedItems = expandedItems.map { allExpanded }
                    }
                }) {
                    Icon(
                        imageVector = if (allExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (allExpanded) "Collapse All" else "Expand All"
                    )
                }

                //替换成 IconButton
                if (isScanPhotoEnabled()) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_photo_library_24),
                        contentDescription = stringResource(R.string.select_image_to_scan),
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                galleryLauncher.launch("image/*")
                            },
                        tint = MaterialTheme.colors.primary,
                    )
                }

                //替换成 IconButton
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.target_constants_meta_setting),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            navController.navigate(Screen.TargetMetaScreen.route)
                        },
                    tint = MaterialTheme.colors.primary,
                )


            })
        },

        /*
        bottomBar = {
            BottomAppBar(
            ) {
                BottomNavigationItem(selected = false, icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_expand_more_24),
                        contentDescription = "Expand More",
                        modifier = Modifier
                            .size(30.dp)
                            .background(color = Color.LightGray)
                            .clickable { },
                        tint = Color.Green,
                    )
                }, onClick = {})
            }
        },
        */


        //#1 添加stocknote
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = { //浮动按钮 add
            FloatingActionButton(
                onClick = {
                    scope.launch {

                        val tempDate = Calendar.getInstance()
                        val date = Date(tempDate.timeInMillis)
                        val df = SimpleDateFormat("yyyyMMdd")

                        var locale = Locale.getDefault()

                        val createDate = df.format(date)

                        viewModel.onEvent(GetStockNoteForDate(createDate))

                        if (uiState.hasSameStockNoteOnTheDay) {
                            scaffoldState.snackbarHostState.showSnackbar(uiState.error!!)
                            viewModel.onEvent(StockNoteListEvent.ErrorDisplayed)

                        } else {
                            val stockNote = StockNote(createDate = createDate)
                            viewModel.onEvent(StockNoteListEvent.InsertStockNote(stockNote))

                        }

                    }

                },
                backgroundColor = MaterialTheme.colors.primary,

                ) {
                Icon(
                    modifier = Modifier.size(25.dp),
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.add_stock_daily_note),
                    tint = Color.White
                )
            }
        },

        ) {

        LaunchedEffect(uiState.error) {
            Log.d("LaunchedEffect", "notesUiState.error")
            uiState.error?.let {
                scaffoldState.snackbarHostState.showSnackbar(uiState.error!!)
                viewModel.onEvent(StockNoteListEvent.ErrorDisplayed)
            }
        }

        if (uiState.stockNoteWithTargetListsList.isEmpty()) {
            Log.d("notes.isNotEmpty", "empty")
            NoItemMessage()
        }

        LaunchedEffect(imageUri) {
            if (imageUri != null) {

                val image: InputImage = InputImage.fromFilePath(context, imageUri!!)

                val recognizer =
                    TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())

                val result = recognizer.process(image).addOnSuccessListener { visionText ->
                    // Task completed successfully
                    // ...
                    //Log.d("visionText", visionText.toString())

                    viewModel.onEvent(StockNoteListEvent.OnScanPhoto(visionText))

                }.addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                    e.printStackTrace()
                }

            }
        }//扫描图片   LaunchedEffect(imageUri)

        Divider(thickness = 2.dp, color = MaterialTheme.colors.primary)

        StockNoteList(navController,
            uiState,
            viewModel,
            expandedItems = expandedItems,
            onExpandChange = { index, expanded ->
                expandedItems = expandedItems.toMutableList().apply {
                    this[index] = expanded
                }
            },
            allExpanded = allExpanded,
            setAllExpanded = { expanded ->
                expandedItems = List(uiState.stockNoteWithTargetListsList.size) { expanded }
            })

    }
}


@Composable
private fun StockNoteList(
    navController: NavHostController,
    uiState: StockNoteListViewModel.UiState,
    viewModel: StockNoteListViewModel,
    expandedItems: List<Boolean>,
    onExpandChange: (Int, Boolean) -> Unit,
    allExpanded: Boolean,
    setAllExpanded: (Boolean) -> Unit
) {

    val orderSettingsVisible = remember { mutableStateOf(false) }
    val showCompletedNotes = remember { mutableStateOf(viewModel.uiState.showCompletedNotes) }

    val expandListByTargetReasons =
        remember { mutableStateOf(viewModel.uiState.expandListByTargetReasons) }

    val queryStr = remember { mutableStateOf("") }

    LaunchedEffect(uiState.stockNoteWithTargetListsList) {
        setAllExpanded(allExpanded)
    }

    LazyColumn(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(top = 12.dp, start = 0.dp, end = 0.dp, bottom = 12.dp)
    ) {

        //header
        item {
            Column(
                Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    IconButton(onClick = {
                        orderSettingsVisible.value = !orderSettingsVisible.value
                    }) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.ic_settings_sliders),
                            contentDescription = stringResource(id = R.string.order_by)
                        )
                    }

                    OutlinedTextField(value = queryStr.value, onValueChange = {
                        queryStr.value = it

                        viewModel.uiState = if (it.isEmpty()) {
                            viewModel.uiState.copy(queryStringForActionReview = "")
                        } else {
                            viewModel.uiState.copy(queryStringForActionReview = it)
                        }

                        viewModel.onEvent(StockNoteListEvent.QueryChanged)

                    })

                }//row for buttons

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AnimatedVisibility(visible = orderSettingsVisible.value) {
                        Column(
                            Modifier.background(color = MaterialTheme.colors.background)
                        ) {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = showCompletedNotes.value, onCheckedChange = {
                                    showCompletedNotes.value = it
                                    viewModel.onEvent(
                                        StockNoteListEvent.ShowCompletedTasks(it)
                                    )
                                })
                                Text(
                                    text = stringResource(R.string.including_completed_stocknotes),
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier.padding(start = 8.dp)
                                )

                                Checkbox(checked = expandListByTargetReasons.value,
                                    onCheckedChange = {

                                        expandListByTargetReasons.value = it

                                        viewModel.onEvent(
                                            StockNoteListEvent.ExpandListByTargetReasons(it)
                                        )
                                    })
                                Text(
                                    text = stringResource(R.string.expand_list_by_target_reasons),
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }// row

                    } //AnimatedVisibility for order settings
                } //row for animatedvisibility

            }
        } //item

        this.items(uiState.stockNoteWithTargetListsList.size) { index ->

            val notewithtargets: StockNoteWithTargetLists =
                uiState.stockNoteWithTargetListsList.get(index)

            val replace = Screen.StockDailyNoteDetailScreen.route.replace(
                oldValue = "{${Constants.STOCK_DAILY_NOTE_ID_ARG}}",
                newValue = "${notewithtargets.note.stockNoteId}"
            )

            ExpandableListItem(uiState = viewModel.uiState,
                stockNote = notewithtargets.note,
                targets = notewithtargets.stockTargets,
                index = index,
                expanded = expandedItems.getOrNull(index) ?: false,
                onExpandChange = { onExpandChange(index, it) },

                onClickToEditPage = {
                    navController.navigate(replace)
                },
                onClickDelete = {
                    viewModel.onEvent(
                        StockNoteListEvent.DeleteStockNotesWithTargets(stockNote = notewithtargets.note)
                    )
                },
                onClickAddTarget = { stockTarget ->
                    viewModel.onEvent(StockNoteListEvent.AddStockTarget(stockTarget))
                },
                onComplete = {
                    viewModel.onEvent(
                        StockNoteListEvent.CompleteStockNote(
                            notewithtargets.note, !notewithtargets.note.isCompleted
                        )
                    )
                })

        }


    }
}


@Composable
fun NoItemMessage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_stock_daily_notes_message),
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            modifier = Modifier.size(125.dp),
            painter = painterResource(id = R.drawable.tasks_img),
            contentDescription = stringResource(R.string.no_stock_daily_notes_message),
            alpha = 0.7f
        )
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ExpandableListItem(
    uiState: StockNoteListViewModel.UiState,
    stockNote: StockNote,
    targets: List<StockTarget>?,
    modifier: Modifier = Modifier,
    onClickToEditPage: () -> Unit,
    onClickDelete: () -> Unit,
    onClickAddTarget: (StockTarget) -> Unit,
    onComplete: () -> Unit,
    index: Int,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit
) {

    var openDialogDeleteValue by rememberSaveable { mutableStateOf(false) }

    var openDialogAddTargetValue = rememberSaveable { mutableStateOf(false) }

    var isCompleted by rememberSaveable { mutableStateOf(stockNote.isCompleted) }

    var targetListVisible = rememberSaveable { mutableStateOf(stockNote.targetListVisible) }


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        //.border(BorderStroke(2.dp, Color.Blue)),
        shape = RoundedCornerShape(12.dp),
        elevation = 8.dp,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.primary,
                ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StockNoteCheckBox(
                    isComplete = stockNote.isCompleted,
                    borderColor = stockNote.color.toStockNoteColor().color
                ) {
                    isCompleted = !isCompleted

                    onComplete()
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(weight = 4f, fill = true)
                ) {
                    Text(
                        text = stockNote.createDate,
                        style = MaterialTheme.typography.h4.copy(color = Color.White),
                    )

                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(weight = 1f, fill = false)

                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        stringResource(id = R.string.detail),
                        modifier = Modifier
                            .size(48.dp)
                            .padding(horizontal = 12.dp)
                            .clickable {
                                onClickToEditPage()
                            },
                        tint = Golden
                    )
                }


                Icon(
                    imageVector = Icons.Default.Delete,
                    stringResource(id = R.string.delete),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { openDialogDeleteValue = true },
                    tint = Color.Red,
                )

                if (targets?.isNotEmpty() == true) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                onExpandChange(!expanded)
                            },
                        tint = Color.Red,
                    )
                }
            }           //header row


            //add 对话框
            if (openDialogAddTargetValue.value) {
                TargetCardDialogAdd(
                    uiState,
                    stockNote,
                    openDialogAddTargetValue = openDialogAddTargetValue,
                    onClickAddTarget = onClickAddTarget
                )
            }

            //delete 对话框
            if (openDialogDeleteValue) {
                AlertDialog(shape = RoundedCornerShape(25.dp),
                    onDismissRequest = { openDialogDeleteValue = false },
                    title = { Text(stockNote.createDate) },
                    text = {
                        Text(
                            stringResource(
                                R.string.delete_stock_note_confirmation_message,
                                stockNote.createDate
                            )
                        )
                    },
                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                            shape = RoundedCornerShape(25.dp),
                            onClick = {
                                openDialogDeleteValue = false
                                onClickDelete()
                            },
                        ) {
                            Text(stringResource(R.string.delete), color = Color.White)
                        }
                    },
                    dismissButton = {
                        Button(shape = RoundedCornerShape(25.dp), onClick = {
                            openDialogDeleteValue = false
                        }) {
                            Text(stringResource(R.string.cancel), color = Color.White)
                        }
                    })
            }//header row dialog

            //listItemExpanded
            //target details
            if (expanded) {
                if (targets != null && targets.isNotEmpty()) {
                    TargetRows(
                        uiState = uiState,
                        targets,
                        targetListVisible = targetListVisible,
                        // stockNote.targetListVisible
                        stockNote = stockNote,
                        //topExpandButtonClicked = topExpandButtonClicked,

                    )
                }
            }

        } //header card
    }

}


@Composable
private fun TargetRows(
    uiState: StockNoteListViewModel.UiState,
    targets: List<StockTarget>,
    targetListVisible: MutableState<Boolean>,
    stockNote: StockNote,
    //topExpandButtonClicked: MutableState<Boolean>,
) {
    if (stockNote.targetListVisible) {
        if (uiState.expandListByTargetReasons) {
            showTargetReasonList(targets, targetListVisible.value)
        } else {
            showTargets(targets, targetListVisible.value)
        }
    }
}

fun showTargetsTitle(targets: List<StockTarget>) {
    TODO("Not yet implemented")
}

@Composable
private fun showTargets(
    targets: List<StockTarget>, targetListVisible: Boolean
) {
    targets.forEachIndexed { index, stockTarget ->
        Log.d("forEachIndexed", stockTarget.toJson())

        AnimatedVisibility(visible = targetListVisible) {
            TargetRow(onTapOnRow = {}, stockTarget)
        }

        if (stockTarget != targets.last()) {
            Divider(thickness = 1.dp, color = MaterialTheme.colors.primary)
        }


    }
}

@Composable
private fun showTargetReasonList(
    targets: List<StockTarget>, targetListVisible: Boolean
) {
    val actionToCodeListMap = emptyMap<TabEntity, MutableList<StockTarget>>().toMutableMap()

    //这个要不要在init中做？
    targets.forEach { stockTarget: StockTarget ->
        stockTarget.targetReasonList.forEach { targetReason ->
            actionToCodeListMap.getOrPut(targetReason) { mutableListOf() }.add(stockTarget)
        }
    }

    val TAG = "actionToCodeListMap"

    actionToCodeListMap.entries.sortedBy { it.key.title }
        .forEachIndexed { index, entry: MutableMap.MutableEntry<TabEntity, MutableList<StockTarget>> ->

            AnimatedVisibility(visible = targetListVisible) {
                TargetActionEntryRow(onTapOnRow = {}, entry)
            }

            if (index != actionToCodeListMap.entries.size) {
                Divider(thickness = 1.dp, color = MaterialTheme.colors.primary)
            }
        }
}


@Composable
fun StockNoteCheckBox(
    isComplete: Boolean = false,
    borderColor: Color = MaterialTheme.colors.secondary,
    onComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .padding(5.dp)
            .clip(CircleShape)
            .border(2.dp, color = borderColor, shape = CircleShape)
            .clickable {
                onComplete()
            }, contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = isComplete) {
            Icon(
                modifier = Modifier.size(25.dp),
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TargetCardDialogAdd(
    //viewModel: StockNoteListViewModel,
    uiState: StockNoteListViewModel.UiState,
    stockNote: StockNote,
    openDialogAddTargetValue: MutableState<Boolean>,
    onClickAddTarget: (StockTarget) -> Unit
) {
    var stockTarget =
        StockTarget(createDate = stockNote.createDate, stockNoteId = stockNote.stockNoteId)

    var code = rememberSaveable { mutableStateOf(stockTarget.code) }
    var name = rememberSaveable { mutableStateOf(stockTarget.name) }


    var targetReasonList = remember { mutableStateListOf<TabEntity>() }
    var targetActionList = remember { mutableStateListOf<TabEntity>() }

    AlertDialog(
        //shape = RoundedCornerShape(25.dp),
        onDismissRequest = { openDialogAddTargetValue.value = false },
        title = { Text(stringResource(R.string.title_add_target)) },

        text = {
            Column {
                Row() {
                    Text(
                        "代码", modifier = Modifier.weight(2f)
                    )
                    OutlinedTextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = code.value,
                        onValueChange = { code.value = it },
                        modifier = Modifier.weight(4f),
                    )
                    Text(
                        "名称", modifier = Modifier.weight(2f)
                    )

                    OutlinedTextField(
                        value = name.value,
                        onValueChange = {
                            name.value = it
                        },
                        modifier = Modifier.weight(4f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                }

                FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top
                ) {

                    Text(
                        //"备选理由",
                        text = stringResource(id = R.string.target_reason_list)
                        //modifier = Modifier.padding(2.dp),
                        //style = MaterialTheme.typography.body1
                    )

                    var tc = uiState.targetConstants
                    if (tc != null) {
                        tc.tabReasons.forEachIndexed { index, meta ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = targetReasonList.contains(meta),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            targetReasonList.add(meta)
                                        } else {
                                            targetReasonList.remove(meta)
                                        }
                                    })//checkbox
                                Text(meta.title)
                            }
                        }
                    }

                }//flowrow


                FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        stringResource(id = R.string.target_actions),
                        //modifier = Modifier.padding(2.dp),
                        //style = MaterialTheme.typography.body1
                    )

                    var tc = uiState.targetConstants
                    if (tc != null) {
                        tc.tabActions.forEachIndexed { index, meta ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = targetActionList.contains(meta),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            targetActionList.add(
                                                meta
                                            )
                                        } else {
                                            targetActionList.remove(meta)
                                        }
                                    })//checkbox

                                Text(meta.title)
                            }
                        }
                    }


                }//flowrow

                Divider(thickness = 1.dp)

            }
        },

        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(),
                shape = RoundedCornerShape(6.dp),
                onClick = {

                    var reduce: String = ""
                    var actionReduce: String = ""


                    var toAdd = StockTarget(
                        stockNoteId = stockTarget.stockNoteId,
                        code = code.value,
                        name = name.value,
                        //targetReasonList = targetReasonList,
                        //targetActionList = targetActionList,
                        //FIXME 暂时先zheyang
                        tabs = targetReasonList.plus(targetActionList),
                        //stockTargetId = item.stockTargetId,
                        createDate = stockNote.createDate,
                    )

                    //TODO update the stocktarget
                    Log.d("stocktarget", toAdd.toString())

                    openDialogAddTargetValue.value = false


                    onClickAddTarget(toAdd)

                }) {
                Text(stringResource(R.string.save), color = Color.White)
            }

        },
        dismissButton = {
            Button(shape = RoundedCornerShape(6.dp), onClick = {
                openDialogAddTargetValue.value = false
            }) {
                Text(stringResource(R.string.cancel), color = Color.White)
            }

        })


}

@Composable
fun TargetActionEntryRow(
    onTapOnRow: () -> Unit, entry: MutableMap.MutableEntry<TabEntity, MutableList<StockTarget>>
) {
    var rowcolor = StockNoteColor.ORANGE.color
    Row(
        modifier = Modifier
            .background(rowcolor)
            //.shadow(2.dp)
            .padding(6.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        val fontLarge = MaterialTheme.typography.h6
        val fontSmall = MaterialTheme.typography.body1

        //代码
        Column(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .width(120.dp),
            //.weight(6f)
            //horizontalAlignment = Alignment.CenterHorizontally,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,

            ) {
            Text(text = entry.key.title, style = fontLarge)
        }

        //Spacer(modifier = Modifier.width(1.dp).background(color=MaterialTheme.colors.primary))

        //备选理由
        Column(
            modifier = Modifier.padding(horizontal = 2.dp),
            //.weight(8f)
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {


            val ev: MutableList<StockTarget> = entry.value

            if (ev.isNotEmpty()) {
                Text(
                    text = ev.map { it.code }.joinToString(";"), style = fontSmall
                )
            } else {
                Text(text = "", style = fontSmall)
            }

        }

        //Spacer(modifier = Modifier.width(1.dp).background(color=MaterialTheme.colors.primary))


    }

}


@Composable
fun TargetRow(
    onTapOnRow: () -> Unit,
    stockTarget: StockTarget,
) {

    var rowcolor = StockNoteColor.ORANGE.color

    Row(
        modifier = Modifier
            .background(rowcolor)
            //.shadow(2.dp)
            .padding(6.dp)
            .fillMaxSize()
            //.border(BorderStroke(2.dp, Color.White), RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    //onPress = { x ->
                    //Toast.makeText(context, "press"+x.toString(), Toast.LENGTH_SHORT)
                    //Log.i("pointerInput", "onPress")
                    //},
                    //onDoubleTap = ,
                    onTap = { offset -> onTapOnRow() },

                    //onLongPress = { offset -> },
                )

            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        val fontLarge = MaterialTheme.typography.h6
        val fontSmall = MaterialTheme.typography.body1

        //代码
        Column(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .weight(6f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

            ) {
            Text(text = stockTarget.code, style = fontLarge)
            Text(text = stockTarget.name, style = fontLarge)
        }

        //Spacer(modifier = Modifier.width(1.dp).background(color=MaterialTheme.colors.primary))

        //备选理由
        Column(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .weight(8f)
        ) {

            if (stockTarget.targetReasonList.isNotEmpty()) {
                Text(
                    text = stockTarget.targetReasonList.map { it.title }.toList().joinToString(";"),
                    style = fontSmall
                )
            } else {
                Text(text = "", style = fontSmall)
            }


        }

        //Spacer(modifier = Modifier.width(1.dp).background(color=MaterialTheme.colors.primary))

        //操作计划
        Column(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .weight(8f)
        ) {

            Log.d("targetActionList", stockTarget.targetActionList.toJson())

            if (stockTarget.targetActionList.isNotEmpty()) {
                Text(
                    text = stockTarget.targetActionList.map { it.title }.toList().joinToString(";"),
                    style = fontSmall
                )

            } else {
                Text(text = "", style = fontSmall)
            }
        }


        /*
        //操作计划
        Column(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .weight(4f)
        ) {

            Icon(
                imageVector = Icons.Default.MoreVert, contentDescription = stringResource(
                    id = string.more
                )
            )
        }*/
    }
}