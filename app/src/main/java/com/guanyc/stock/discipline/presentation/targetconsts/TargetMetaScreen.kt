package com.guanyc.stock.discipline.presentation.targetconsts

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.domain.model.TargetConstants
import com.guanyc.stock.discipline.domain.model.tabActions
import com.guanyc.stock.discipline.domain.model.tabReasons
import com.guanyc.stock.discipline.domain.model.tabSpecialList
import com.guanyc.stock.discipline.domain.model.tabWatchList
import com.guanyc.stock.discipline.presentation.main.components.TAB_TYPE
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import com.guanyc.stock.discipline.presentation.util.Screen
import com.guanyc.stock.discipline.util.BackupUtil.toJson
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TargetMetaScreen(
    navController: NavHostController, viewModel: TargetMetaScreenModel = hiltViewModel()
) {

    val uiState = viewModel.uiState
    val scaffoldState = rememberScaffoldState()
    var openDialog by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    val targetActionList = remember { mutableStateListOf<TabEntity>() }
    val targetReasonList = remember { mutableStateListOf<TabEntity>() }
    val targetWatchListList = remember { mutableStateListOf<TabEntity>() }
    val targetSpecialListList = remember { mutableStateListOf<TabEntity>() }


    var buttonEnabled by remember { mutableStateOf(false) }

    val context = LocalContext.current


    LaunchedEffect(uiState.targetConstants) {


        targetReasonList.clear()
        targetReasonList.addAll(uiState.targetConstants.tabReasons)

        targetActionList.clear()
        targetActionList.addAll(uiState.targetConstants.tabActions)

        targetWatchListList.clear()
        targetWatchListList.addAll(uiState.targetConstants.tabWatchList)

        targetSpecialListList.clear()
        targetSpecialListList.addAll(uiState.targetConstants.tabSpecialList)

        title = uiState.targetConstants.title
        description = uiState.targetConstants.description


    }

    LaunchedEffect(uiState) {
        if (uiState.navigateUp) {
            openDialog = false
            navController.popBackStack(Screen.StockNoteListScreen.route, false)
        }

        if (uiState.error != null) {
            scaffoldState.snackbarHostState.showSnackbar(
                uiState.error
            )
            viewModel.onEvent(TargetActionEvent.ErrorDisplayed)

        }
    }

    BackHandler {
        updateTargetConstantsIfChanged(targetConstants = uiState.targetConstants,
            copy = uiState.targetConstants.copy(
                title = title,
                description = description,
                tabs = targetReasonList + targetActionList + targetWatchListList
            ),
            onNotChanged = {
                navController.popBackStack()
                //navController.popBackStack(Screen.StockNoteListScreen.route, false)
            }) {
            //onUpdate
            if (buttonEnabled) {
                viewModel.onEvent(TargetActionEvent.Update(it))
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        "信息已经保存"
                    )
                }
            }
            navController.popBackStack()
            //navController.popBackStack(Screen.StockNoteListScreen.route, false)
        }
    }

    Scaffold(scaffoldState = scaffoldState,

        topBar = {
            androidx.compose.material.TopAppBar(
                title = {
                    Text("常量列表")
                },

                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        modifier = Modifier
                            .size(30.dp)
                            .padding(4.dp)
                            .clickable {
                                updateTargetConstantsIfChanged(targetConstants = uiState.targetConstants,
                                    copy = uiState.targetConstants.copy(
                                        title = title,
                                        description = description,
                                        tabs = targetReasonList + targetActionList + targetWatchListList,
                                    ),
                                    onNotChanged = {
                                        navController.popBackStack()

                                    }) {
                                    //onUpdate
                                    viewModel.onEvent(TargetActionEvent.Update(it))
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "信息已经保存"
                                        )
                                    }
                                    navController.popBackStack()
                                }
                            },
                        contentDescription = "back",
                        tint = Color.White,

                        )
                },
                actions = {
                    IconButton(onClick = {
                        buttonEnabled = !buttonEnabled
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (buttonEnabled) R.drawable.lock_open_24px else R.drawable.lock_24px
                                ),
                                //imageVector = Icons.Default.Lock,
                                contentDescription = stringResource(R.string.update),
                                tint = Color.Blue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Enable Buttons", color = Color.Blue)
                        }
                    }

                },
                backgroundColor = MaterialTheme.colors.primary,
                elevation = 0.dp,
            )

        }

    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())

        ) {

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = stringResource(id = R.string.target_constants)) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(text = stringResource(id = R.string.target_constants)) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.target_reason_list),
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.h5,
                )
            }


            targetReasonList.forEachIndexed { index, item ->

                SubTargetReasonItem(targetReasonMeta = item,
                    buttonEnabled = buttonEnabled,
                    onChange = { targetReasonList[index] = it },
                    onDelete = { targetReasonList.removeAt(index) })

            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (buttonEnabled) {
                            targetReasonList.add(
                                TabEntity(
                                    title = "", tabType = TAB_TYPE.TAB_REASON
                                )
                            )
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.add_target_reason_element),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp),
                    painter = painterResource(id = R.drawable.ic_add),
                    tint = if (buttonEnabled) Color.Blue else Color.Gray,
                    contentDescription = stringResource(
                        id = R.string.add_sub_reason
                    )
                )

            }



            Divider(Modifier.height(1.dp))
            Spacer(modifier = Modifier.height(2.dp))



            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.target_action_list),
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.h5
                )
            }


            targetActionList.forEachIndexed { index, item ->
                SubTargetActionItem(targetActionMeta = item,
                    buttonEnabled = buttonEnabled,
                    onChange = { targetActionList[index] = it },
                    onDelete = { targetActionList.removeAt(index) })

            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (buttonEnabled) {
                            targetActionList.add(
                                TabEntity(
                                    title = "", tabType = TAB_TYPE.TAB_ACTION
                                )
                            )
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.add_target_action_element),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp),
                    painter = painterResource(id = R.drawable.ic_add),
                    tint = Color.Blue,
                    contentDescription = stringResource(
                        id = R.string.add_target_action_element
                    )
                )

            }



            Spacer(modifier = Modifier.height(4.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {


                TextButton(onClick = {

                    var tabs =
                        listOf<TabEntity>() + targetSpecialListList[0] + targetActionList + targetSpecialListList[1] + targetSpecialListList[3] + targetReasonList + targetSpecialListList[2] + targetWatchListList

                    updateTargetConstantsIfChanged(targetConstants = uiState.targetConstants,
                        copy = uiState.targetConstants.copy(
                            title = title,
                            description = description,
                            tabs = tabs,
                        ),
                        onNotChanged = {
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    context.getString(R.string.string_remain_unchanged)
                                )
                            }

                        }) {

                        if (buttonEnabled) {
                            Log.d("targetConstantsList", it.toJson())

                            //onUpdate
                            viewModel.onEvent(TargetActionEvent.Update(it))

                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    context.getString(R.string.string_changed)
                                )
                            }

                        } else {

                        }
                    }

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = stringResource(R.string.save),
                        tint = if (buttonEnabled) Color.Blue else Color.Gray,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("保存")

                }

            }
        }

    }
}


@Composable
fun MyRow(
    text: String,
    index: Int,
    isFirst: Boolean,
    isLast: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    var editableText by remember { mutableStateOf(text) }
    var isInWatchlist by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(value = editableText,
                onValueChange = { editableText = it },
                modifier = Modifier.weight(1f),
                label = { Text("Edit Text") })
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                Checkbox(checked = isInWatchlist, onCheckedChange = { isChecked ->
                    isInWatchlist = isChecked
                })
                Text(text = "Watchlist")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onMoveUp, enabled = !isFirst) {
                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Move Up")
            }
            IconButton(onClick = onMoveDown, enabled = !isLast) {
                Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Move Down")
            }
            IconButton(onClick = { /* Handle edit action */ }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = { /* Handle delete action */ }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun SubTargetReasonItem(
    targetReasonMeta: TabEntity,
    onChange: (TabEntity) -> Unit,
    onDelete: () -> Unit,
    buttonEnabled: Boolean,
) {
    Spacer(modifier = Modifier.height(2.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        Column() {
            if (buttonEnabled) {
                Icon(painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.delete_target_action),
                    modifier = Modifier.clickable { onDelete() })
            } else {
                Icon(painter = painterResource(R.drawable.ic_delete),
                    tint = Color.Gray,
                    contentDescription = stringResource(R.string.delete_target_action),
                    modifier = Modifier.clickable { })
            }

        }

        Spacer(modifier = Modifier.width(2.dp))

        Column(Modifier.weight(1f)) {
            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = targetReasonMeta.title,
                    onValueChange = {
                        onChange(targetReasonMeta.copy(title = it))
                    },
                    label = { Text(text = stringResource(R.string.target_reason)) },
                    shape = RoundedCornerShape(6.dp),
                    textStyle = MaterialTheme.typography.body1,

                    /*
                    textStyle = if (subTask.isCompleted)
                        TextStyle(
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colors.onBackground
                        )
                    else
                        MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.onBackground),

                     */
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(2.dp))

            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = targetReasonMeta.note,
                    onValueChange = {
                        onChange(targetReasonMeta.copy(note = it))
                    },
                    label = { Text(text = stringResource(R.string.target_action_description)) },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }


        }
    }

}


@Composable
fun SubTargetActionItem(
    targetActionMeta: TabEntity,
    onChange: (TabEntity) -> Unit,
    onDelete: () -> Unit,
    buttonEnabled: Boolean
) {
    Spacer(modifier = Modifier.height(2.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            // 优化1: 对于Icon，明确区分可点击与不可点击状态，增强可访问性
            if (buttonEnabled) {
                ClickableIcon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.delete_target_action),
                    onClick = onDelete
                )
            } else {
                DisabledIcon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.delete_target_action)
                )
            }
        }

        Spacer(modifier = Modifier.width(2.dp))

        Column(Modifier.weight(1f)) {
            // 优化2: 提取通用的TextField逻辑到一个函数中，减少代码重复
            TextFieldWithLabel(value = targetActionMeta.title,
                label = stringResource(R.string.target_action),
                onValueChange = {

                    try {
                        onChange(targetActionMeta.copy(title = it))
                    } catch (e: Exception) {
                        // 优化3: 添加异常处理，确保界面的健壮性
                        Log.e("SubTargetActionItem", "Failed to update title", e)
                    }
                })

            Spacer(modifier = Modifier.height(2.dp))

            TextFieldWithLabel(value = targetActionMeta.note,
                label = stringResource(R.string.target_action_description),
                onValueChange = {
                    try {
                        onChange(targetActionMeta.copy(note = it))
                    } catch (e: Exception) {
                        Log.e("SubTargetActionItem", "Failed to update description", e)
                    }
                })
        }
    }
}

// 优化4: 分离Icon的点击逻辑到独立的Composable，增强代码的可读性和可维护性
@Composable
fun ClickableIcon(
    painter: Painter, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Icon(painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.clickable { onClick() })
}

@Composable
fun DisabledIcon(
    painter: Painter, contentDescription: String, modifier: Modifier = Modifier
) {
    Icon(
        painter = painter,
        tint = Color.Gray,
        contentDescription = contentDescription,
        modifier = modifier
    )
}

// 优化5: 提取通用的TextField逻辑，减少代码重复
@Composable
fun TextFieldWithLabel(
    value: String, label: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        shape = RoundedCornerShape(6.dp),
        textStyle = MaterialTheme.typography.body1,
        modifier = modifier.fillMaxWidth()
    )
}


fun updateTargetConstantsIfChanged(
    targetConstants: TargetConstants,
    copy: TargetConstants,
    onNotChanged: () -> Unit = {},
    onUpdate: (TargetConstants) -> Unit
) {
    if (targetConstantsChanged(targetConstants, copy)) onUpdate(copy) else onNotChanged()
}

fun targetConstantsChanged(
    targetConstants: TargetConstants, copy: TargetConstants
): Boolean {
    return targetConstants.title != copy.title
            || targetConstants.description != copy.description
            || targetConstants.tabActions != copy.tabActions
            || targetConstants.tabReasons != copy.tabReasons
            || targetConstants.tabWatchList != copy.tabWatchList
}