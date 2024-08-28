@file:OptIn(ExperimentalMaterialApi::class)

package com.guanyc.stock.discipline.presentation.stocks


//import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
//import com.google.accompanist.flowlayout.FlowMainAxisAlignment
//import com.google.accompanist.flowlayout.FlowRow

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.R.drawable
import com.guanyc.stock.discipline.R.string
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockNoteWithTargetLists
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.tabActions
import com.guanyc.stock.discipline.domain.model.tabReasons
import com.guanyc.stock.discipline.presentation.main.components.TAB_TYPE
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import com.guanyc.stock.discipline.presentation.stocks.StockNoteDetailEvent.UpdateStockTargetEvent
import com.guanyc.stock.discipline.presentation.util.Screen
import com.guanyc.stock.discipline.presentation.util.showDatePicker
import com.guanyc.stock.discipline.theme.Orange
import com.guanyc.stock.discipline.util.BackupUtil.toJson
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.date.formatDateformat
import com.guanyc.stock.discipline.util.settings.StockNoteColor
import com.guanyc.stock.discipline.util.settings.toInt
import com.guanyc.stock.discipline.util.settings.toStockNoteColor
import java.util.Calendar


//https://stackoverflow.com/questions/2928902/how-do-i-detect-a-cancel-click-of-the-datepicker-dialog


data class User(
    val name: String,
    val age: Int,
    val emails: List<Email>
)

data class Email(
    val address: String,
    val type: String // e.g., "personal", "work"
)

fun userToString(item: StockNoteWithTargetLists): String {
    return buildString {
        append("StockNote:\n")
        append("stockNoteId: ${item.note.stockNoteId}\n")
        append("createDate: ${item.note.createDate}\n")
        append("color: ${item.note.color.toStockNoteColor()}\n")
        append("isCompleted: ${item.note.isCompleted}\n")
        append("hasUnplannedAction: ${item.note.hasUnplannedAction}\n")
        append("reviewMarket: ${item.note.reviewMarket}\n")

        item.stockTargets.forEach {
            append("StockTarget:\n")
            append("stockTargetId: ${it.stockTargetId}\n")
            append("name: ${it.name}\n")
            append("code: ${it.code}\n")
            append("isFavorite: ${it.isFavorite}\n")
            append("comment: ${it.comment}\n")
            append("hasPosition: ${it.hasPosition}\n")
            append("isCompleted: ${it.isCompleted}\n")
        }
    }
}

fun userToMarkdown(item: StockNoteWithTargetLists): String {
    return buildString {

        append("# StockNote Information\n")

        append("stockNoteId: ${item.note.stockNoteId}\n")
        append("createDate: ${item.note.createDate}\n")
        append("color: ${item.note.color.toStockNoteColor()}\n")
        append("isCompleted: ${item.note.isCompleted}\n")
        append("hasUnplannedAction: ${item.note.hasUnplannedAction}\n")
        append("reviewMarket: ${item.note.reviewMarket}\n")

        item.stockTargets.forEach {
            append("## StockTarget \n")
            append("name-code: ${it.name}-${it.code}\n")
            append("isFavorite: ${it.isFavorite}\n")
            append("comment: ${it.comment}\n")
            append("hasPosition: ${it.hasPosition}\n")
            append("isCompleted: ${it.isCompleted}\n")

            //it.targetActionList.forEach()
            //it.targetReasonList.forEach()
            //it.targetWatchListList.forEach()

        }
    }
}

fun userToEmailContent(item: StockNoteWithTargetLists): String {
    return buildString {
        append("Hello,\n")

        append("Here is the StockNote information:\n")

        append("stockNoteId: ${item.note.stockNoteId}\n")
        append("createDate: ${item.note.createDate}\n")
        append("color: ${item.note.color.toStockNoteColor()}\n")
        append("isCompleted: ${item.note.isCompleted}\n")
        append("hasUnplannedAction: ${item.note.hasUnplannedAction}\n")
        append("reviewMarket: ${item.note.reviewMarket}\n\n")

        item.stockTargets.forEach {
            append("StockTarget \n")
            append("name-code: ${it.name}-${it.code}\n")
            append("isFavorite: ${it.isFavorite}\n")
            append("comment: ${it.comment}\n")
            append("hasPosition: ${it.hasPosition}\n")
            append("isCompleted: ${it.isCompleted}\n\n")

            //it.targetActionList.forEach()
            //it.targetReasonList.forEach()
            //it.targetWatchListList.forEach()

        }

        append("\nBest regards,\n")
    }
}


@Suppress("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StockNoteListItemDetailScreen(
    navController: NavHostController,
    stockNoteId: Long,
    viewModel: StockNoteListItemDetailViewModel = hiltViewModel(),
) {


    val uiState = viewModel.uiState

    Log.d("stockNoteId", "$stockNoteId")

    LaunchedEffect(key1 = true) {

        //StockNoteWithTargetLists

        viewModel.onEvent(
            StockNoteDetailEvent.GetStockNoteWithTargetLists(stockNoteId = stockNoteId)
        )

    }


    val focusRequester = remember { FocusRequester() }
    //LaunchedEffect(true) { focusRequester.requestFocus() }


    //val context = LocalContext.current.applicationContext
    val context = LocalContext.current


    val scaffoldState = rememberScaffoldState()

    var openDeleteStockNoteDialog by rememberSaveable { mutableStateOf(false) }
    var openShareStockNoteDialog by rememberSaveable { mutableStateOf(false) }


    var toShowDatePicker by remember { mutableStateOf(false) }

    var hasUnplannedAction by remember { mutableStateOf(false) }

    val createTargetDialogValue = rememberSaveable {
        mutableStateOf(false)
    }


    var editTargetDialogValue = rememberSaveable { mutableStateOf(false) }


    var createDate by rememberSaveable { mutableStateOf("") }

    val colors = StockNoteColor.values().toList()


    var selectedColor by rememberSaveable { mutableStateOf(StockNoteColor.RED) }

    var isCompleted by rememberSaveable { mutableStateOf(false) }

    var reviewMarket by rememberSaveable { mutableStateOf("") }

    val targets = remember { mutableStateListOf<StockTarget>() }

    var targetListIndex = rememberSaveable { mutableStateOf(-1L) }


    //初始化赋值
    LaunchedEffect(uiState.item) {
        Log.d("uiState", uiState.toJson())
        if (uiState.item != null) {
            createDate = uiState.item.note.createDate
            reviewMarket = uiState.item.note.reviewMarket
            isCompleted = uiState.item.note.isCompleted
            selectedColor = uiState.item.note.color.toStockNoteColor()
            hasUnplannedAction = uiState.item.note.hasUnplannedAction

            targets.clear()
            targets.addAll(uiState.item.stockTargets)
        }

    }

    LaunchedEffect(uiState) {

        if (uiState.navigateUp) {
            openDeleteStockNoteDialog = false
            navController.popBackStack(
                Screen.StockNoteListScreen.route, inclusive = false
            )
        }

        if (uiState.error != null) {
            scaffoldState.snackbarHostState.showSnackbar(uiState.error)
            viewModel.onEvent(StockNoteDetailEvent.ErrorDisplayed)
        }
    }

    //并没有监测后退键
    val backHandlerEnabled = false
    if (backHandlerEnabled) {
        androidx.activity.compose.BackHandler(enabled = backHandlerEnabled) {
            //updateStockNoteIfChanged(note = uiState.note, copy = uiState.note.copy(
            //参考其他screen
        }
    }

    Scaffold(scaffoldState = scaffoldState, topBar = {
        TopAppBar(
            title = {
                Text(text = "日志细节", modifier = Modifier.clickable {

                    val replace = Screen.StockNoteDetailPage.route.replace(
                        oldValue = "{${Constants.STOCK_DAILY_NOTE_ID_ARG}}",
                        newValue = "${stockNoteId}"
                    )
                    //navController.navigate(replace)
                })
            },

            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack();
                }) {
                    //Icons.Default.MoreVert
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },

            actions = {
                IconButton(onClick = { openDeleteStockNoteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        //painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = stringResource(string.delete),
                        tint = MaterialTheme.colors.primary

                    )
                }

                IconButton(onClick = { openShareStockNoteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share),
                        tint = MaterialTheme.colors.primary
                    )
                }


            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
        )
    }) {


        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(2.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            // complete checkbox and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //width 30dp
                StockNoteCheckBox(
                    isComplete = isCompleted, borderColor = selectedColor.color
                ) {
                    isCompleted = !isCompleted

                    if (uiState.item != null) {
                        viewModel.onEvent(
                            StockNoteDetailEvent.CompleteStockNoteEvent(
                                uiState.item.note, !uiState.item.note.isCompleted
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                OutlinedTextField(
                    value = reviewMarket,
                    onValueChange = { reviewMarket = it },
                    label = { Text(stringResource(R.string.comment_on_note)) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }


            //日期
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        toShowDatePicker = true
                    }
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {

                if (toShowDatePicker) {
                    val date = Calendar.getInstance()
                    showDatePicker(date, LocalContext.current, onDateSelected = {
                        toShowDatePicker = false
                        createDate = it.formatDateformat("yyyyMMdd")
                    }, onCancel = {
                        toShowDatePicker = false
                    },

                        onDismiss = {
                            toShowDatePicker = false
                        })
                }

                Icon(
                    imageVector = Icons.Default.DateRange,
                    stringResource(id = string.date_created),
                    modifier = Modifier.size(45.dp),
                )

                Spacer(modifier = Modifier.width(4.dp))

                OutlinedTextField(
                    value = createDate,
                    onValueChange = { },
                    label = { Text(text = stringResource(id = string.date_created)) },
                    shape = RoundedCornerShape(5.dp),
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                Column() {
                    Icon(
                        painter = painterResource(id = drawable.baseline_question_mark_24),
                        stringResource(id = R.string.question_mark),
                        tint = Color.Red,
                        modifier = Modifier
                            .size(45.dp)
                            .background(color = Color.White),
                    )
                }


                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .background(color = if (hasUnplannedAction) Color.Red else MaterialTheme.colors.primary)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(string.has_unplanned_action),
                            style = MaterialTheme.typography.h6.copy(color = Color.Black)
                        )

                        Switch(
                            checked = hasUnplannedAction, onCheckedChange = {
                                hasUnplannedAction = !hasUnplannedAction
                            }, enabled = true, modifier = Modifier.weight(2f)
                            //.background(Color.LightGray),
                        )
                        Text(
                            text = if (hasUnplannedAction) stringResource(string.title_yes) else stringResource(
                                string.title_no
                            ), style = MaterialTheme.typography.h6.copy(color = Color.Black)
                        )
                    }
                }
            }


            // color part 颜色
            Divider()
            Text(
                text = stringResource(string.note_color),
                //style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(12.dp))
            ColorTabRow(
                colors = colors, selectedColor = selectedColor
            ) { selectedColor = it }


            //targets rows
            Spacer(Modifier.height(12.dp))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                Modifier
                    .padding(horizontal = 24.dp, vertical = 6.dp)
                    .fillMaxWidth()
                    .clickable {
                        createTargetDialogValue.value = true
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    //text = stringResource(string.choices),
                    text = "添加标的", style = MaterialTheme.typography.h6
                    //modifier = Modifier.padding(vertical = 8.dp)
                )

                Icon(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    //painter = painterResource(id = R.drawable.ic_add),
                    imageVector = Icons.Default.Add, contentDescription = stringResource(
                        id = string.add_stock_target
                    )
                )

            }


            if (createTargetDialogValue.value) {
                DialogAddNewTarget(
                    //新创建的target肯定确定一定未完成? 如果是补作业呢？
                    uiState = uiState,
                    targets = targets,
                    stockNoteId = stockNoteId,
                    createTargetDialogValue = createTargetDialogValue,
                    createDate = createDate
                )
            }
            //
            //TargetRowsTableHead()

            Spacer(Modifier.height(12.dp))
            if (targets != null && targets.isNotEmpty()) {

                TargetRows(
                    targetListIndex, targets, viewModel
                )

                if (editTargetDialogValue.value) {
                    DialogEditTargetDetail(
                        targets,
                        stockNoteId,
                        editTargetDialogValue,
                        targetListIndex,
                        uiState,
                        viewModel
                    )
                }
            }


            val timeMillis = System.currentTimeMillis()
            //submit button
            Button(
                onClick = {

                    val stockNote = StockNote(
                        stockNoteId = stockNoteId,
                        //updatedDate = System.currentTimeMillis(),
                        createDate = createDate,
                        isCompleted = isCompleted,
                        color = selectedColor.toInt(),
                        reviewMarket = reviewMarket,
                    )

                    viewModel.onEvent(
                        StockNoteDetailEvent.UpdateStockNoteEvent(stockNote)
                    )

                    val targetsWithNoteId = targets.map { item ->
                        item.copy(
                            stockNoteId = stockNoteId,
                            //updateDate = stockNote.updatedDate,
                            createDate = stockNote.createDate,
                        )
                    }

                    viewModel.onEvent(
                        StockNoteDetailEvent.InsertOrUpdateStockTargetListEvent(
                            targetsWithNoteId
                        )
                    )


                    //other processing
                    //title = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                //shape = RoundedCornerShape(25.dp),
            ) {

                val stringRes = string.update

                Text(
                    text = stringResource(stringRes),
                    //这个style 定义了字体背景
                    style = MaterialTheme.typography.h5,
                )
            }
        }
    }

    if (openShareStockNoteDialog) {
        //share dialog
        AlertDialog(//shape = RoundedCornerShape(25.dp),
            onDismissRequest = { openShareStockNoteDialog = false },
            title = { Text(stringResource(string.share)) },
            text = {
                Column {
                    Text("Choose how you'd like to share the information:")
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, userToString(uiState.item!!))
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(intent, "Share as Text"))
                    }) {
                        Text("Share as String")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, userToMarkdown(uiState.item!!))
                            type = "text/markdown"
                        }
                        context.startActivity(Intent.createChooser(intent, "Share as Markdown"))
                    }) {
                        Text("Share as Markdown")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("recipient@example.com"))
                            putExtra(Intent.EXTRA_SUBJECT, "User Information")
                            putExtra(Intent.EXTRA_TEXT, userToEmailContent(uiState.item!!))
                            type = "message/rfc822"
                        }
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    }) {
                        Text("Share as Email")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        val clipboardManager =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("UserData", userToString(uiState.item!!))
                        clipboardManager.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Copy to Clipboard")
                    }
                }


            },
            confirmButton = {
                Button(onClick = { openShareStockNoteDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (openDeleteStockNoteDialog) {
        AlertDialog(//shape = RoundedCornerShape(25.dp),
            onDismissRequest = { openDeleteStockNoteDialog = false },
            title = { Text(stringResource(string.delete)) },
            text = {
                Text(
                    stringResource(
                        string.delete_task_confirmation_message, createDate
                    )
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    //shape = RoundedCornerShape(25.dp),
                    onClick = {
                        if (uiState.item != null) {
                            viewModel.onEvent(StockNoteDetailEvent.DeleteStockNoteEvent(uiState.item.note))

                            openDeleteStockNoteDialog = false

                            navController.popBackStack();

                        }
                    },
                ) {
                    Text(
                        stringResource(
                            string.delete
                        ), color = Color.White
                    )
                }
            },
            dismissButton = {
                Button(//shape = RoundedCornerShape(25.dp),
                    onClick = {
                        openDeleteStockNoteDialog = false
                    }) {
                    Text(stringResource(string.cancel), color = Color.White)
                }
            })
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DialogEditTargetDetail(
    targets: SnapshotStateList<StockTarget>,
    stockNoteId: Long,
    editTargetDialogValue: MutableState<Boolean>,
    targetListIndex: MutableState<Long>,
    uiState: NoteDetailUiState,
    viewModel: StockNoteListItemDetailViewModel,
) {

    var stockTarget: StockTarget = targets[targetListIndex.value.toInt()];

    var code = rememberSaveable { mutableStateOf(stockTarget.code) }
    var name = rememberSaveable { mutableStateOf(stockTarget.name) }

    var isOpportunityGiven by remember { mutableStateOf(stockTarget.isOpportunityGiven) }
    var isPlanActed by remember { mutableStateOf(stockTarget.isPlanActed) }
    var isProfitable by remember { mutableStateOf(stockTarget.isProfitable) }
    var actionReview by remember { mutableStateOf(stockTarget.actionReview) }
    var profitOrLossPercentage by remember { mutableStateOf(stockTarget.profitOrLossPercentage) }


    var targetReasonList = remember { mutableStateListOf<TabEntity>() }
    var targetActionList = remember { mutableStateListOf<TabEntity>() }



    if (stockTarget.tabs.isNotEmpty()) {
        targetReasonList.clear()
        targetReasonList.addAll(stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON })
    }

    if (stockTarget.tabs.isNotEmpty()) {
        targetActionList.clear()
        targetActionList.addAll(stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_ACTION })
    }


    AlertDialog(
        //shape = RoundedCornerShape(25.dp),
        onDismissRequest = { editTargetDialogValue.value = false },
        title = { Text(stringResource(string.update)) },
        text = {
            Column() {
                Row() {
                    OutlinedTextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = code.value,
                        label = { Text(stringResource(string.target_code)) },
                        onValueChange = { code.value = it },
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(4f),
                    )

                    OutlinedTextField(
                        value = name.value,
                        onValueChange = {
                            name.value = it
                        },
                        label = { Text(stringResource(string.target_name)) },
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(4f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                }

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(string.target_reason_options),
                        modifier = Modifier.padding(2.dp),
                        style = MaterialTheme.typography.body1
                    )
                }

                FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = 3,
                ) {
                    if (uiState.targetConstants != null) {

                        uiState.targetConstants.tabReasons.forEachIndexed { index, meta ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = targetReasonList.contains(meta),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            targetReasonList.add(
                                                meta
                                            )
                                        } else {
                                            targetReasonList.remove(meta)
                                        }
                                    })//checkbox
                                Text(meta.title)
                            }
                        }
                    }
                }//flowrow


                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(string.target_actions),
                        modifier = Modifier.padding(2.dp),
                        style = MaterialTheme.typography.body1
                    )
                }
                FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top
                ) {

                    if (uiState.targetConstants != null) {
                        uiState.targetConstants.tabActions.forEachIndexed { index, meta ->
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
                                    })
                                Text(meta.title)
                            }
                        }
                    }

                }//flowrow


                Divider(thickness = 1.dp)
                FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isOpportunityGiven, onCheckedChange = { checked ->
                            isOpportunityGiven = checked
                        })
                        Text(
                            text = stringResource(string.string_given_opportunity),
                            //style = MaterialTheme.typography.body2.copy(color = Color.White),
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isPlanActed, onCheckedChange = {
                            isPlanActed = !isPlanActed
                        })
                        Text(
                            text = stringResource(string.act_according_to_plan),
                            //style = MaterialTheme.typography.body2.copy(color = Color.White),
                        )
                    }

                    AnimatedVisibility(visible = isPlanActed) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = if (isProfitable) stringResource(string.make_profit) else stringResource(
                                    string.make_loss
                                ),
                                modifier = Modifier.weight(2f),
                            )

                            Switch(
                                checked = isProfitable,
                                onCheckedChange = {
                                    isProfitable = !isProfitable
                                },
                                enabled = true,
                                modifier = Modifier
                                    .weight(2f)
                                    .background(Color.LightGray),

                                )


                            Text(
                                stringResource(string.profit_loss_ratio),
                                //modifier = Modifier.padding(2.dp)
                                modifier = Modifier.weight(2f),
                            )

                            val pattern = remember { Regex("^-?\\d*\\.?\\d*\$") }

                            OutlinedTextField(
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                value = profitOrLossPercentage.toString(),
                                onValueChange = {
                                    if (it.matches(pattern)) {
                                        if (it.isEmpty()) {
                                            profitOrLossPercentage = 0.0
                                        } else {
                                            profitOrLossPercentage = it.toDouble()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(2f),
                                //modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(string.action_review), modifier = Modifier
                            // .padding(2.dp)
                            .weight(2f)
                    )
                    OutlinedTextField(
                        value = actionReview,
                        onValueChange = {
                            actionReview = it
                        },
                        modifier = Modifier
                            // .padding(2.dp)
                            .weight(6f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                }


            }
        },

        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                shape = RoundedCornerShape(12.dp),
                onClick = {

                    var reasonReduce: String = ""
                    var actionReduce: String = ""

                    var update = StockTarget(
                        stockNoteId = stockNoteId,
                        code = code.value,
                        name = name.value,
                        //FIXME 暂时先这样
                        tabs = targetActionList.plus(targetReasonList),
                        stockTargetId = stockTarget.stockTargetId,
                        createDate = stockTarget.createDate
                    )

                    Log.d("stocktarget", update.toString())

                    //FIXME save persistence

                    targets[targetListIndex.value.toInt()] = update


                    //FIXME 数据乡下, event 向上，不要在下面用viewmodel
                    viewModel.onEvent(UpdateStockTargetEvent(update))



                    editTargetDialogValue.value = false

                }) {
                Text(stringResource(string.update), color = Color.White)
            }

        },
        dismissButton = {
            Button(shape = RoundedCornerShape(12.dp), onClick = {
                editTargetDialogValue.value = false
            }) {
                Text(stringResource(string.cancel), color = Color.White)
            }

        })
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DialogAddNewTarget(
    targets: SnapshotStateList<StockTarget>,
    stockNoteId: Long,
    createTargetDialogValue: MutableState<Boolean>,
    createDate: String,
    uiState: NoteDetailUiState
) {

    var code = rememberSaveable { mutableStateOf("") }
    var name = rememberSaveable { mutableStateOf("") }


    var targetReasonList = remember { mutableStateListOf<TabEntity>() }
    var targetActionList = remember { mutableStateListOf<TabEntity>() }

    AlertDialog(
        //shape = RoundedCornerShape(25.dp),
        onDismissRequest = { createTargetDialogValue.value = false },
        title = { Text(stringResource(string.add_operation)) },
        text = {
            Column() {
                Row() {

                    OutlinedTextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = code.value,
                        label = { Text(stringResource(string.stock_code)) },
                        onValueChange = { code.value = it },
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(4f),
                    )

                    OutlinedTextField(
                        value = name.value,
                        onValueChange = {
                            name.value = it
                        },
                        label = { Text(stringResource(R.string.stock_name))},
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(4f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                }

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(id = R.string.target_reason),
                        modifier = Modifier.padding(2.dp),
                        style = MaterialTheme.typography.body1
                    )
                }

                FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = 3,
                ) {

                    if (uiState.targetConstants != null) {

                        uiState.targetConstants.tabReasons.forEachIndexed { index, meta ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                //.any { it -> it.title == meta.title && it.description == meta.description && it.id == meta.id },
                                Checkbox(checked = targetReasonList.contains(meta),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            targetReasonList.add(
                                                meta
                                            )
                                        } else {
                                            targetReasonList.remove(meta)
                                        }
                                    })
                                Text(
                                    meta.title,
                                    modifier = Modifier.padding(2.dp),
                                    style = MaterialTheme.typography.body1
                                )
                            }

                        }
                    }


                }//flowrow


                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(id = R.string.target_action),
                        modifier = Modifier.padding(2.dp),
                        style = MaterialTheme.typography.body1
                    )
                }
                FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top
                ) {

                    if (uiState.targetConstants != null) {
                        uiState.targetConstants.tabActions.forEachIndexed { index, meta ->
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
                                    })
                                Text(meta.title)
                            }
                        }
                    }

                }//flowrow


            }
        },

        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green),
                shape = RoundedCornerShape(12.dp),
                onClick = {

                    var item = StockTarget(
                        stockNoteId = stockNoteId,
                        code = code.value,
                        name = name.value,

                        createDate = createDate,
                        //FIXME 暂时先这样
                        tabs = targetActionList.plus(targetReasonList),
                    )

                    Log.d("stocktarget", item.toString())

                    targets.add(item)

                    createTargetDialogValue.value = false

                }) {
                Text(stringResource(string.save), color = Color.White)
            }

        },
        dismissButton = {
            Button(shape = RoundedCornerShape(12.dp), onClick = {
                createTargetDialogValue.value = false
            }) {
                Text(stringResource(string.cancel), color = Color.White)
            }

        })
}


@Composable
fun TargetRows(
    targetListIndex: MutableState<Long>,
    targets: SnapshotStateList<StockTarget>,
    viewModel: StockNoteListItemDetailViewModel,
) {


    targets.forEachIndexed {

            index, stockTarget ->

        if (viewModel.uiState.targetConstants != null) {
            if (viewModel.uiState.item != null) {
                TargetCard(
                    index = index,
                    targetListIndex = targetListIndex,
                    targetConstants = viewModel.uiState.targetConstants!!,
                    stockNote = viewModel.uiState.item!!.note,
                    stockTarget = stockTarget,
                    backgroundColor = Orange,

                    onUpdateStockTarget = { stockNote, stockTarget ->
                        viewModel.onEvent(
                            StockNoteDetailEvent.UpdateStockNoteAndStockTargetEvent(
                                stockNote, stockTarget
                            )
                        )
                    },
                    onClickDelete = {
                        viewModel.onEvent(
                            StockNoteDetailEvent.DeleteStockTargetEvent(
                                stockTarget
                            )
                        )
                    },
                    listExpanded = true
                )
            }
        }


    }
}


@Composable
fun AnimatedTabIndicatorForNoteColor(modifier: Modifier) {
    Box(
        modifier = modifier
            .padding(6.dp)
            .fillMaxSize()
            .border(BorderStroke(2.dp, Color.White), RoundedCornerShape(8.dp))
    )
}


@Composable
fun ColorTabRow(
    colors: List<StockNoteColor>, selectedColor: StockNoteColor, onChange: (StockNoteColor) -> Unit
) {

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        AnimatedTabIndicatorForNoteColor(Modifier.tabIndicatorOffset(tabPositions[selectedColor.toInt()]))
    }

    TabRow(
        selectedTabIndex = selectedColor.toInt(),
        indicator = indicator,
        modifier = Modifier.clip(RoundedCornerShape(14.dp))
    ) {
        colors.forEachIndexed { index, noteColor ->
            androidx.compose.material.Tab(
                text = { Text(stringResource(noteColor.title)) },

                selected = selectedColor.toInt() == index,

                onClick = {
                    onChange(index.toStockNoteColor())
                },

                modifier = Modifier.background(noteColor.color)
            )
        }
    }

}