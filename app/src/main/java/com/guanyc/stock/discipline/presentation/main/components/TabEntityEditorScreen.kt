package com.guanyc.stock.discipline.presentation.main.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.app.getString

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TabEntityEditorScreen(
    navController: NavHostController, viewModel: ActionViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableStateOf(0) }

    var tabs: MutableList<TabEntity> by remember { mutableStateOf(mutableListOf<TabEntity>()) }

    ////var editedTexts = remember { tabs.map { mutableStateOf(TextFieldValue(it.tabName)) }.toMutableStateList() }


    LaunchedEffect(viewModel.uiState.tabs) {

        Log.d("LaunchedEffect", tabs.toString())
        if (viewModel.uiState.tabs.isNotEmpty()) {

            synchronized(this) {
                if (tabs.isEmpty()) {

                    tabs.clear()
                    tabs.addAll(viewModel.uiState.tabs)

                    ////editedTexts.clear()
                    ////editedTexts.addAll(tabs.map { mutableStateOf(TextFieldValue(it.tabName)) }.toMutableStateList())

                    Log.d("tabs", tabs.toString())
                }

            }
        }
    }

    BackHandler {
        navController.popBackStack()
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.dashboard),
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                )
            }, backgroundColor = MaterialTheme.colors.background, elevation = 0.dp
        )
    },

        content = itemList(tabs, focusRequester,
            ////editedTexts,
            onItemEdited = { old: TabEntity, editedText: TabEntity, index: Int ->
                //FIXME 持久化 就好
                viewModel.onEvent(
                    ActionViewEvent.onItemEdited(
                        old, editedText, index
                    )
                )
            }, onItemAdded = { newItem ->
                viewModel.onEvent(ActionViewEvent.addNewTabEntity(newItem))
            }, onItemDeleted = { itemDeleted: TabEntity, index: Int ->
                viewModel.onEvent(ActionViewEvent.onItemDeleted(itemDeleted, index))

            }, onItemsReordered = { draggingAction, draggingIndex, newItems: List<TabEntity> ->
                //tabs.toMutableList().clear(); tabs.toMutableList().addAll(newItems);
                //todo change in viewmodel
                viewModel.onEvent(
                    ActionViewEvent.onItemsReordered(
                        draggingAction, draggingIndex, newItems
                    )
                )
            })
    )

}

@Composable
private fun itemList(
    tabs: MutableList<TabEntity>,
    focusRequester: FocusRequester,
    ////editedTexts: SnapshotStateList<MutableState<TextFieldValue>>,
    onItemEdited: (TabEntity, TabEntity, Int) -> Unit,
    onItemAdded: (TabEntity) -> Unit,
    onItemDeleted: (TabEntity, Int) -> Unit,
    onItemsReordered: (Int, Int, List<TabEntity>) -> Unit
): @Composable (PaddingValues) -> Unit {


    //val special_string_en_list = listOf("ALL", "INDEX", "POSITION", "SPECIAL")

    var newItem by remember { mutableStateOf("") }

    var draggingItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggingAction by remember { mutableStateOf<Int?>(1) }

    var editableIndex by remember { mutableStateOf<Int?>(null) }

    var showRowDialog by remember { mutableStateOf(false) }
    var showRowDialogIndex by remember { mutableStateOf(-1) }


    return {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colors.background,
            modifier = Modifier.padding(2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.sort_tab_items),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f)

                ) {
                    itemsIndexed(items = tabs) { index, item ->
                        //items(items.size) { index ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                        ) {
                            if (item.tabType == TAB_TYPE.TAB_ACTION) {
                                Icon(
                                    painter = painterResource(id = R.drawable.target_action_24px),
                                    contentDescription = "Action",
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colors.primary,
                                )
                            } else if (item.tabType == TAB_TYPE.TAB_REASON) {
                                Icon(
                                    painter = painterResource(id = R.drawable.target_reason_24px),
                                    contentDescription = "Reason",
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colors.primary
                                )
                            } else if (item.tabType == TAB_TYPE.TAB_WATCHLIST) {
                                Icon(
                                    painter = painterResource(id = R.drawable.edit_square_24px),
                                    contentDescription = "WatchList",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable {
                                            showRowDialog = true
                                            showRowDialogIndex = index
                                        },
                                    tint = MaterialTheme.colors.primary
                                )

                                if (showRowDialog) {
                                    EditStringDialog(
                                        initialValue = tabs[showRowDialogIndex].title,
                                        onDismissRequest = {
                                            showRowDialog = false
                                            showRowDialogIndex = -1
                                        },
                                        onSave = { newText ->
                                            if (newText.isNotEmpty()) {

                                                if (tabs.any {
                                                        it.title == newText
                                                    }) {
                                                    return@EditStringDialog
                                                }

                                                Log.d(
                                                    "onItemEditedUI",
                                                    "index ${showRowDialogIndex.toString()}"
                                                )
                                                var old = tabs[showRowDialogIndex]
                                                Log.d("onItemEditedUI", "old ${old.toString()}")

                                                tabs.removeAt(showRowDialogIndex)
                                                Log.d(
                                                    "onItemEditedUI",
                                                    "old removed ${tabs.toString()}"
                                                )
                                                tabs.add(
                                                    showRowDialogIndex,
                                                    old.copy(title = newText)
                                                )
                                                Log.d(
                                                    "onItemEditedUI",
                                                    "new added ${tabs.toString()}"
                                                )

                                                onItemEdited(
                                                    old,
                                                    tabs[showRowDialogIndex],
                                                    showRowDialogIndex
                                                )
                                                showRowDialog = false
                                                showRowDialogIndex = -1
                                            }

                                        },
                                    )
                                }
                            } else if (item.tabType == TAB_TYPE.TAB_SPECIAL) {
                                Icon(
                                    painter = painterResource(id = R.drawable.folder_special_24px),
                                    contentDescription = "Special",
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colors.primary
                                )
                            }

                            Text(
                                text = tabs[index].title,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            )

                            IconButton(enabled = index != 0, onClick = {
                                draggingItemIndex = tabs.indexOf(item)
                                draggingAction = 1

                            }) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up")
                            }

                            IconButton(enabled = index != tabs.size - 1, onClick = {
                                draggingItemIndex = tabs.indexOf(item)
                                draggingAction = -1
                            }) {
                                Icon(
                                    Icons.Default.ArrowDownward, contentDescription = "Move Down"
                                )
                            }

                            IconButton(enabled = isModifiableTabEntity(
                                item, lowercase_special_string_list, special_string_cn_list
                            ), onClick = {
                                //draggingItemIndex = items.indexOf(item)
                                //draggingAction = 0
                                //editableIndex = null

                                var deleted = tabs.get(index)
                                tabs.removeAt(index)
                                ////editedTexts.removeAt(index)

                                onItemDeleted(deleted, index)

                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }

                        }//row
                    }//itemindexed
                }//lazycolumn


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newItem,
                        onValueChange = {
                            newItem = it
                        },
                        label = { Text(stringResource(R.string.new_tabentity_item)) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    )
                    Button(onClick = {

                        if (newItem.isNotEmpty()) {
                            //onItemAdded(newItem)
                            //newItem = ""

                            //prevent confliction with existing tabs
                            if (tabs.any {
                                    it.title == newItem
                                }) {
                                return@Button
                            }

                            var title = newItem.toString().trim().toLowerCase()
                            //
                            if (lowercase_special_string_list.any {
                                    it == title
                                } || special_string_cn_list.any {
                                    it == title
                                }
                            ) {
                                return@Button
                            }


                            val tabEntity = TabEntity(
                                title = newItem,
                                tabType = TAB_TYPE.TAB_WATCHLIST
                            )

                            Log.d("TAG", "onItemAdded: $tabEntity")

                            tabs.add(
                                0, tabEntity
                            )

                            Log.d("TAG", "items added: $tabs ")

                            ////editedTexts.add(0, mutableStateOf(TextFieldValue(newItem)))

                            ////Log.d("TAG", "editedTexts added: ${editedTexts.toString()} ")

                            onItemAdded(tabEntity)

                            newItem = ""
                        }
                    }) {
                        Text("OK")
                    }

                }
            }
        }//end of Surface

        LaunchedEffect(draggingItemIndex) {
            if (draggingItemIndex != null) {

                onItemsReordered(draggingAction!!, draggingItemIndex!!, tabs.apply {
                    when (draggingAction) {
                        1 -> {
                            //move upwards
                            val temp = this[draggingItemIndex!! - 1]
                            this[draggingItemIndex!! - 1] = this[draggingItemIndex!!]
                            this[draggingItemIndex!!] = temp

                            ////val tempState = editedTexts[draggingItemIndex!! - 1]
                            ////editedTexts[draggingItemIndex!! - 1] = editedTexts[draggingItemIndex!!]
                            ////editedTexts[draggingItemIndex!!] = tempState

                        }

                        -1 -> {
                            //move downwards
                            val temp = this[draggingItemIndex!! + 1]
                            this[draggingItemIndex!! + 1] = this[draggingItemIndex!!]
                            this[draggingItemIndex!!] = temp

                            ////val tempState = editedTexts[draggingItemIndex!! + 1]
                            ////editedTexts[draggingItemIndex!! + 1] = editedTexts[draggingItemIndex!!]
                            ////editedTexts[draggingItemIndex!!] = tempState
                        }

                        /*0 -> {
                            //delete
                            this.removeAt(draggingItemIndex!!)
                            editedTexts.removeAt(draggingItemIndex!!)
                        }*/
                    }

                })

                draggingItemIndex = null
                draggingAction = null
            }
        }


    }//
}


fun isModifiableTabEntity(
    item: TabEntity, special_string_list: List<String>, special_string_cn_list: List<String>
): Boolean {
    return item.tabType == TAB_TYPE.TAB_WATCHLIST
}


@Composable
fun EditStringDialog(
    initialValue: String, onDismissRequest: () -> Unit, onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }
    val isValid = remember { mutableStateOf(!initialValue.isEmpty()) }

    AlertDialog(onDismissRequest = onDismissRequest,
        title = { Text(stringResource(id = R.string.edit)) },
        text = {
            Column(modifier = Modifier.padding(24.dp)) {
                OutlinedTextField(value = text, onValueChange = {
                    text = it
                    isValid.value = it.isNotBlank()
                }, label = { Text(stringResource(id = R.string.enter_your_text)) },
                    isError = !isValid.value
                )
                Spacer(modifier = Modifier.size(8.dp))
                if (!isValid.value) {
                    Text(
                        text = stringResource(id = R.string.text_canot_be_empty),
                        color = MaterialTheme.colors.error
                    )
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    onSave(text)
                    onDismissRequest()
                },
                enabled = isValid.value,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.onBackground,
                    contentColor = MaterialTheme.colors.primaryVariant
                ),
            ) {
                Text(getString(R.string.save))
            }
        }, dismissButton = {
            Button(onClick = onDismissRequest) {
                Text(getString(R.string.cancel))

            }
        })
}

@Preview(showBackground = true)
@Composable
fun PreviewEditStringDialog() {
    var text by remember { mutableStateOf("") }
    EditStringDialog(
        initialValue = getString(R.string.initial_value),
        onDismissRequest = {},
        onSave = { newText ->
            text = newText
        }
    )
}
