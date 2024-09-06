@file:OptIn(ExperimentalFoundationApi::class)

package com.guanyc.stock.discipline.presentation.main.components


import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material.primarySurface
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.app.getString
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.tabActions
import com.guanyc.stock.discipline.domain.model.tabReasons
import com.guanyc.stock.discipline.domain.model.targetActionList
import com.guanyc.stock.discipline.domain.model.targetReasonList
import com.guanyc.stock.discipline.presentation.util.Screen
import com.guanyc.stock.discipline.theme.Golden
import com.guanyc.stock.discipline.theme.Orange
import com.guanyc.stock.discipline.util.BackupUtil.toJson
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiSelectDialog(
    items: List<String>,
    selectedItems: List<String>,
    onItemsSelected: (List<String>) -> Unit,
    onDismissRequest: () -> Unit
) {

    var currentSelections by remember { mutableStateOf<List<String>>(selectedItems) }

    AlertDialog(onDismissRequest = { onDismissRequest() },
        title = { Text(stringResource(R.string.select_tabs)) },
        text = {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.Top,
                //mainAxisSpacing = 8.dp,
                //crossAxisSpacing = 8.dp
                maxItemsInEachRow = 4
            ) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Checkbox(checked = currentSelections.contains(item), onCheckedChange = {
                            if (it) {
                                currentSelections =
                                    currentSelections.toMutableList().apply { add(item) }

                            } else {
                                currentSelections =
                                    currentSelections.toMutableList().apply { remove(item) }
                            }
                        })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = item)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onItemsSelected(currentSelections.toList())
                    onDismissRequest()
                }, enabled = currentSelections.isNotEmpty()
            ) {
                Text(stringResource(R.string.string_ok))
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(stringResource(R.string.string_cancel))
            }
        })
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ActionScreen(
    navController: NavHostController, viewModel: ActionViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberScrollState()
    val lazyListState = rememberLazyListState()


    val scope = rememberCoroutineScope()

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    var contents: List<StockTarget> by remember { mutableStateOf(emptyList()) }
    var favorites: List<Boolean> by remember { mutableStateOf(emptyList()) }
    var tabStrings = remember { mutableStateListOf<String>() }
    //var targetReasonList = remember { mutableStateListOf<TabEntity>() }


    val showContextMenuIconsDialog = remember { mutableStateOf(false) }
    var showStockTargetNoteEditDialog = remember { mutableStateOf(false) }

    var openShareStockNoteDialog = remember { mutableStateOf(false) }


    var showTabEntitySelectingDialog = remember { mutableStateOf(false) }

    var createTargetDialogValue = remember { mutableStateOf(false) }

    val commentText = remember { mutableStateOf("") }

    val selectedItemIndex = remember { mutableStateOf(-1) }
    var dropDownItemsExpanded by remember { mutableStateOf(false) }


    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                //Log.d("imageUri", imageUri.toString())
                imageUri = it
            }
        })


    BackHandler {
        /*
        if (sheetState.isVisible) scope.launch {
            sheetState.hide()
            focusRequester.freeFocus()
        }*/
        //else
        navController.popBackStack()
    }




    fun reloadContentForTab(
        selectedTab: TabEntity, keyName: String
    ) {
        var selectedTab1 = selectedTab
        var keyName1 = keyName

        if (selectedTabIndex != -1) {
            selectedTab1 = viewModel.uiState.tabs[selectedTabIndex]
            keyName1 = selectedTab1.title



            if (selectedTab1.tabType == TAB_TYPE.TAB_SPECIAL) {
                if (keyName1.lowercase(Locale.getDefault()) == context.getString(R.string.string_all)
                        .lowercase(Locale.getDefault()) || keyName1.lowercase(Locale.getDefault()) == "全部".lowercase(
                        Locale.getDefault()
                    )
                ) {
                    contents = viewModel.uiState.stockTargets
                } else if (keyName1.lowercase() == context.getString(R.string.string_indices)
                        .lowercase() || keyName1.lowercase() == "指数".lowercase()
                ) {
                    contents = viewModel.uiState.stockTargets.filter {
                        //it.name.endsWith("指") || it.name.endsWith("指数") || it.name.uppercase()
                        //  .contains("ETF") || it.name.uppercase().contains("INDEX")
                        it.tabs.any {
                            it.tabType == TAB_TYPE.TAB_SPECIAL && (it.title.equals("指数") || it.title.equals(
                                context.getString(R.string.string_indices)
                            ))
                        }

                    }
                } else if (keyName1 == context.getString(R.string.string_position) || keyName1 == "Position" || keyName1 == "头寸") {
                    contents = viewModel.uiState.stockTargets.filter { stockTarget ->
                        //it.hasPosition
                        stockTarget.tabs.any {
                            it.title.contains("头寸") || it.title.contains("Position")
                        }
                    }

                    //这个可以从settings preference里面保存获取
                } else if (keyName1 == context.getString(R.string.string_special) || keyName1 == "特别关注" || keyName1 == "Special") {
                    contents = viewModel.uiState.stockTargets.filter {
                        it.isFavorite || it.tabs.any {
                            it.title.contains("特别关注") || it.title.contains("Special")
                        }
                    }

                }

            } else {
                contents = viewModel.uiState.stockTargets.filter {
                    it.tabs.any {
                        it.title.trim() == keyName1.trim()
                    }
                }
            }

            favorites = contents.map { it.isFavorite }

            tabStrings.clear()
            tabStrings.addAll(contents.map { stockTarget ->
                stockTarget.createDate + "\n" + stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
                    .map { it.title }.joinToString(";")})



        }
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

                viewModel.onEvent(ActionViewEvent.OnScanPhoto(visionText))

            }.addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                e.printStackTrace()
            }

        }
    }//扫描图片   LaunchedEffect(imageUri)


    Scaffold(
        modifier = Modifier.padding(2.dp),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(R.string.stocktarget),
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                )
            },
                backgroundColor = colors.background,
                elevation = 0.dp,
                navigationIcon = {},
                actions = {

                    IconButton(onClick = {
                        scope.launch {
                            createTargetDialogValue.value = true
                        }
                    }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = stringResource(R.string.add_stock_daily_note),
                            tint = colors.primary
                        )


                    }

                    IconButton(onClick = {}) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    galleryLauncher.launch("image/*")
                                },
                            tint = colors.primary,
                            painter = painterResource(R.drawable.document_scanner_40px),
                            contentDescription = stringResource(R.string.select_image_to_scan),
                        )


                    }
                })
        },

        ) {

        if (viewModel.uiState.tabs.isEmpty()) {
            //Text(text = "No Item Loaded!")

        } else {

            var selectedTab = viewModel.uiState.tabs[selectedTabIndex]
            var keyName = selectedTab.title


            LaunchedEffect(selectedTabIndex) {

                reloadContentForTab(selectedTab, keyName)
            }


            if (createTargetDialogValue.value) {
                AddStockTargetDialog(uiState = viewModel.uiState, onDismissRequest = {
                    createTargetDialogValue.value = false
                }, selectedTabEntity = selectedTab,

                    onStockTargetAdd = { stockTarget ->

                        if (!viewModel.uiState.stockTargets.any { it.code.equals(stockTarget.code) }) {

                            viewModel.onEvent(
                                ActionViewEvent.addNewFreshStockTarget(
                                    stockTarget
                                )
                            )

                            createTargetDialogValue.value = false

                            Log.d("createTargetDialogValue", stockTarget.toString())

                            contents = contents + stockTarget
                            favorites = contents.map { it.isFavorite }
                            tabStrings.clear()
                            tabStrings.addAll(contents.map { stockTarget ->
                                stockTarget.createDate + "\n" + stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
                                    .map { it.title }.joinToString(";")})


                        } else {
                            Toast.makeText(
                                context, context.getString(
                                    R.string.stocktarget_already_exists, stockTarget.code
                                ), Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }



            Card(
                elevation = 2.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {

                Column {
                    Row {
                        ScrollableTabRow(
                            modifier = Modifier.weight(1f),
                            selectedTabIndex = selectedTabIndex,
                            edgePadding = 4.dp,
                        ) {
                            viewModel.uiState.tabs.forEach { tab: TabEntity ->
                                Tab(selected = selectedTab == tab, onClick = {
                                    selectedTabIndex = viewModel.uiState.tabs.indexOf(tab)
                                    viewModel.onEvent(
                                        ActionViewEvent.onSelectedTabIndexChanged(
                                            tab, selectedTabIndex
                                        )
                                    )

                                }, text = {
                                    //if(tabs.indexOf(tab)!=tabs.size-1)
                                    Text(
                                        tab.title,
                                        modifier = Modifier.padding(4.dp),
                                        style = if (selectedTab == tab) MaterialTheme.typography.h5 else MaterialTheme.typography.h6
                                    )
                                })
                            }
                        }

                        Box(
                            modifier = Modifier.background(color = colors.primarySurface)
                        ) {
                            IconButton(onClick = {
                                scope.launch {
                                    //openDialogEditTabEntityValue.value = true
                                    dropDownItemsExpanded = true

                                }
                            }) {
                                //Icons.Default.MoreVert
                                Icon(
                                    Icons.Default.MoreVert, contentDescription = null
                                )

                                //dropdown items to select tab index
                                DropdownMenu(
                                    expanded = dropDownItemsExpanded,
                                    onDismissRequest = { dropDownItemsExpanded = false },
                                    properties = PopupProperties(
                                        dismissOnBackPress = true, dismissOnClickOutside = true
                                    )
                                ) {
                                    viewModel.uiState.tabs.forEach { tab: TabEntity ->

                                        DropdownMenuItem(onClick = {

                                            selectedTabIndex = viewModel.uiState.tabs.indexOf(tab)

                                            dropDownItemsExpanded = false

                                            viewModel.onEvent(
                                                ActionViewEvent.onSelectedTabIndexChanged(
                                                    tab, selectedTabIndex
                                                )
                                                //TODO 刷新UI的tab text和值
                                            )

                                        }) {
                                            Text(tab.title)
                                        }
                                    }

                                    // the last item does something different
                                    DropdownMenuItem(onClick = {

                                        dropDownItemsExpanded = false

                                        navController.navigate(Screen.TabEntityEditorScreen.route)
                                    }) {
                                        Text(
                                            "Edit", style = MaterialTheme.typography.h5.copy(
                                                color = colors.primarySurface,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp

                                            )
                                        )
                                    }


                                }
                            }
                        }//Box icon
                    }

                    Row {
                        LazyColumn(
                            state = lazyListState, contentPadding = PaddingValues(bottom = 70.dp)
                        ) {

                            Log.d("contents", contents.toJson())

                            //stockTarget.createDate + "\n" + stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
                            //  .map { it.title }.joinToString(";")

                            itemsIndexed(contents) { index, item ->
                                ListItem(index,
                                    item,
                                    favorites,
                                    tabStrings.toList(),
                                    onClickToShowDialog = {
                                        selectedItemIndex.value = index
                                        showContextMenuIconsDialog.value = true
                                    })
                            }
                        }
                    }


                    if (showTabEntitySelectingDialog.value) {

                        val stockTarget = contents[selectedItemIndex.value]

                        val tabEntityList = viewModel.uiState.tabs

                        val items = tabEntityList.map { it.title }

                        val onDismissRequest1 = {
                            showTabEntitySelectingDialog.value = false
                        }
                        //var targetActionList = remember { mutableStateListOf<TabEntity>() }
                        var currentSelections: List<String> by remember { mutableStateOf(value = contents[selectedItemIndex.value].tabs.map { it.title }) }

                        AlertDialog(onDismissRequest = { onDismissRequest1() },
                            title = { Text("Select Items") },
                            text = {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalArrangement = Arrangement.Top,
                                    //mainAxisSpacing = 8.dp,
                                    //crossAxisSpacing = 8.dp
                                    maxItemsInEachRow = 4
                                ) {
                                    items.forEach { item: String ->
                                        Row(
                                            modifier = Modifier.padding(4.dp)
                                        ) {
                                            Checkbox(
                                                checked = currentSelections.any {
                                                    it.equals(item) },

                                                onCheckedChange = { checked ->
                                                if (checked) {
                                                    if (!currentSelections.any { it.equals(item) }) {
                                                        currentSelections =
                                                            currentSelections.toMutableList<String>()
                                                                .apply {
                                                                    this.add(
                                                                        item
                                                                    )
                                                                }
                                                    }
                                                } else {
                                                    if (currentSelections.any { it.equals(item) }) {
                                                        currentSelections =
                                                            currentSelections.toMutableList<String>()
                                                                .apply {
                                                                    this.remove(
                                                                        item
                                                                    )
                                                                }
                                                    }
                                                }
                                            })
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = item)
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        //contents[selectedItemIndex.value].tabs.add(TabEntity(it))

                                        var currentTabs =
                                            tabEntityList.filter { it.title in currentSelections }

                                        Log.d(
                                            "currentTabs",
                                            currentSelections.joinToString(";")
                                        )

                                        Log.d(
                                            "currentTabs",
                                            currentTabs.map { it.title }.joinToString(";")
                                        )

                                        val tabOld = stockTarget.tabs
                                        stockTarget.tabs = currentTabs

                                        val tabNew = stockTarget.tabs

                                        viewModel.onEvent(
                                            ActionViewEvent.onItemTabEntitiesChanged(
                                                stockTarget, tabOld, tabNew, selectedItemIndex.value
                                            )
                                        )


                                        //val stockTarget = contents[selectedItemIndex.value]

                                        contents = contents.toMutableList()
                                        favorites = contents.map { it.isFavorite }
                                        tabStrings.clear()
                                        tabStrings.addAll(contents.map { stockTarget ->
                                            stockTarget.createDate + "\n" + stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
                                                .map { it.title }.joinToString(";")})

                                        //selectedItemIndex for refresh


                                        //R.string.string_special
                                        if (tabNew.any { it.title.equals(getString(R.string.string_special)) }) {
                                            if (stockTarget.isFavorite == false) {
                                                stockTarget.isFavorite = true
                                                viewModel.onEvent(
                                                    ActionViewEvent.onStockTargetFavoriteUpdated(
                                                        stockTarget, true
                                                    )
                                                )
                                            }
                                        } else {
                                            if (stockTarget.isFavorite == true) {
                                                stockTarget.isFavorite = false
                                                viewModel.onEvent(
                                                    ActionViewEvent.onStockTargetFavoriteUpdated(
                                                        stockTarget, false
                                                    )
                                                )
                                            }
                                        }

                                        //contents[selectedItemIndex.value].tabs.add(TabEntity(it))
                                        showTabEntitySelectingDialog.value = false


                                        onDismissRequest1()
                                    }, enabled = currentSelections.isNotEmpty<String>()
                                ) {
                                    Text(getString(R.string.string_ok))
                                }
                            },

                            dismissButton = {
                                Button(onClick = { onDismissRequest1() }) {
                                    Text(getString(R.string.string_cancel))
                                }
                            })
                    }

                    if (showStockTargetNoteEditDialog.value) {
                        var stockTarget = contents[selectedItemIndex.value]
                        val initialValue = stockTarget.comment

                        var text by remember { mutableStateOf(initialValue) }

                        val isValid = remember { mutableStateOf(!initialValue.isEmpty()) }


                        AlertDialog(onDismissRequest = {
                            showStockTargetNoteEditDialog.value = false
                        }, confirmButton = {
                            Button(
                                onClick = {

                                    viewModel.onEvent(
                                        ActionViewEvent.onStockTargetCommentUpdated(
                                            stockTarget, text
                                        )
                                    )

                                    showStockTargetNoteEditDialog.value = false
                                },
                                enabled = isValid.value,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = colors.onBackground,
                                    contentColor = colors.primaryVariant
                                ),
                            ) {
                                Text(getString(R.string.save))
                            }
                        },

                            dismissButton = {
                                Button(onClick = {
                                    showStockTargetNoteEditDialog.value = false
                                }) {
                                    Text(getString(R.string.cancel))
                                }
                            }, title = {},

                            text = {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    OutlinedTextField(value = text,
                                        onValueChange = {
                                            text = it
                                            isValid.value =
                                                it.isNotBlank() && !it.equals(initialValue)
                                        },
                                        label = { Text(getString(R.string.enter_your_text)) },
                                        isError = !isValid.value
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                    if (!isValid.value) {
                                        Text(
                                            text = getString(R.string.text_canot_be_empty),
                                            color = colors.error
                                        )
                                    }
                                }
                            })
                    }

                    if (openShareStockNoteDialog.value) {
                        var stockTarget = contents[selectedItemIndex.value]
                        //share dialog
                        AlertDialog(//shape = RoundedCornerShape(25.dp),
                            onDismissRequest = { openShareStockNoteDialog.value = false },
                            title = { Text(stringResource(R.string.share)) },
                            text = {
                                Column {
                                    Text(stringResource(R.string.choose_how_you_d_like_to_share_the_information))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(onClick = {
                                        val intent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, targetToString(stockTarget))
                                            type = "text/plain"
                                        }
                                        context.startActivity(
                                            Intent.createChooser(
                                                intent, context.getString(R.string.share_as_text)
                                            )
                                        )
                                    }) {
                                        Text(context.getString(R.string.share_as_text))
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(onClick = {
                                        val intent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_TEXT, targetToMarkdown(stockTarget)
                                            )
                                            type = "text/markdown"
                                        }
                                        context.startActivity(
                                            Intent.createChooser(
                                                intent,
                                                context.getString(R.string.share_as_markdown)
                                            )
                                        )
                                    }) {
                                        Text(context.getString(R.string.share_as_markdown))
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(onClick = {
                                        val intent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_EMAIL, arrayOf("recipient@example.com")
                                            )
                                            putExtra(Intent.EXTRA_SUBJECT, "User Information")
                                            putExtra(
                                                Intent.EXTRA_TEXT, targetToEmailContent(stockTarget)
                                            )
                                            type = "message/rfc822"
                                        }
                                        context.startActivity(
                                            Intent.createChooser(
                                                intent, context.getString(R.string.send_email)
                                            )
                                        )
                                    }) {
                                        Text(context.getString(R.string.send_email))
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(onClick = {
                                        val clipboardManager =
                                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText(
                                            "UserData", targetToString(stockTarget)
                                        )
                                        clipboardManager.setPrimaryClip(clip)
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.copied_to_clipboard),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }) {
                                        Text(context.getString(R.string.copied_to_clipboard))
                                    }
                                }


                            },
                            confirmButton = {
                                Button(onClick = { openShareStockNoteDialog.value = false }) {
                                    Text("Close")
                                }
                            })
                    }

                    if (showContextMenuIconsDialog.value) {

                        var stockTarget = contents[selectedItemIndex.value]

                        AlertDialog(onDismissRequest = {
                            showContextMenuIconsDialog.value = false
                        }, confirmButton = {
                            Button(onClick = { showContextMenuIconsDialog.value = false }) {
                                Text(getString(R.string.close))
                            }
                        }, title = {},

                            text = {
                                Column {
                                    CenteredTitle(stockTarget.code + "-" + stockTarget.name)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    IconGrid(selectedItemIndex.value,

                                        contents.size,

                                        stockTarget,

                                        favorites[selectedItemIndex.value],

                                        onIconClick = { index ->
                                            // Handle icon click
                                            when (index) {
                                                0 -> {
                                                    viewModel.onEvent(
                                                        ActionViewEvent.onStockTargetDialogDeleteClicked(
                                                            stockTarget
                                                        )
                                                    )

                                                    var tml = contents.toMutableList()
                                                    tml.removeAt(selectedItemIndex.value)
                                                    contents = tml
                                                    favorites = contents.map { it.isFavorite }
                                                    tabStrings.clear()
                                                    tabStrings.addAll(contents.map { stockTarget ->
                                                        stockTarget.createDate + "\n" + stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
                                                            .map { it.title }.joinToString(";")})


                                                    showContextMenuIconsDialog.value = false
                                                }

                                                1 -> { //favorite

                                                    val oldfavorite = stockTarget.isFavorite
                                                    val newfavorite = !oldfavorite

                                                    var tml = contents.toMutableList()

                                                    var stockTargetOld =
                                                        tml.removeAt(selectedItemIndex.value)

                                                    stockTargetOld.isFavorite = newfavorite
                                                    tml.add(
                                                        selectedItemIndex.value, stockTargetOld
                                                    )

                                                    contents = tml
                                                    favorites = contents.map { it.isFavorite }
                                                    tabStrings.clear()
                                                    tabStrings.addAll(contents.map { stockTarget ->
                                                        stockTarget.createDate + "\n" + stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
                                                            .map { it.title }.joinToString(";")})


                                                    stockTarget.isFavorite = newfavorite

                                                    viewModel.onEvent(
                                                        ActionViewEvent.onStockTargetFavoriteUpdated(
                                                            stockTarget, newfavorite
                                                        )
                                                    )

                                                    showContextMenuIconsDialog.value = false
                                                }

                                                2 -> { //take note
                                                    showContextMenuIconsDialog.value = false
                                                    showStockTargetNoteEditDialog.value = true

                                                }

                                                3 -> {//pin top
                                                    //stockTarget pin top with tabname
                                                    //merge contents with top pinned items
                                                    var tml = contents.toMutableList()

                                                    if (!stockTarget.isTopPinned) {//pin top
                                                        tml.removeIf { it -> it.code == stockTarget.code }
                                                        stockTarget.isTopPinned =
                                                            !stockTarget.isTopPinned
                                                        tml.add(0, stockTarget)
                                                    } else {//unpin top
                                                        tml.removeIf { it -> it.code == stockTarget.code }
                                                        stockTarget.isTopPinned =
                                                            !stockTarget.isTopPinned
                                                        tml.add(stockTarget)
                                                    }

                                                    contents = tml
                                                    favorites = contents.map { it.isFavorite }
                                                    tabStrings.clear()
                                                    tabStrings.addAll(contents.map { stockTarget ->
                                                        stockTarget.createDate + "\n" + stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
                                                            .map { it.title }.joinToString(";")})



                                                    viewModel.onEvent(
                                                        ActionViewEvent.onStockTargetTopPinned(
                                                            stockTarget,
                                                            viewModel.uiState.tabs[selectedTabIndex],
                                                            stockTarget.isTopPinned
                                                        )
                                                    )

                                                    showContextMenuIconsDialog.value = false

                                                }

                                                4 -> { //move top
                                                    if (stockTarget.isTopPinned) {
                                                        showContextMenuIconsDialog.value = false
                                                        return@IconGrid
                                                    }

                                                    var tml = contents.toMutableList()

                                                    var indexFirst =
                                                        tml.indexOfFirst { it -> it.isTopPinned == false }

                                                    tml.remove(stockTarget)

                                                    if (indexFirst < tml.size) {
                                                        tml.add(indexFirst, stockTarget)
                                                    } else {
                                                        tml.add(0, stockTarget)
                                                    }

                                                    contents = tml
                                                    favorites = contents.map { it.isFavorite }
                                                    tabStrings.clear()
                                                    tabStrings.addAll(contents.map { stockTarget ->
                                                        stockTarget.createDate + "\n" + stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
                                                            .map { it.title }.joinToString(";")})


                                                    ActionViewEvent.onStockTargetTopMoved(
                                                        stockTarget,
                                                        viewModel.uiState.tabs[selectedTabIndex],
                                                        true
                                                    )

                                                    showContextMenuIconsDialog.value = false
                                                }

                                                5 -> {
                                                    showContextMenuIconsDialog.value = false
                                                    openShareStockNoteDialog.value = true

                                                }

                                                "move bottom".hashCode() -> {//move bottom


                                                    if (stockTarget.isTopPinned) {
                                                        showContextMenuIconsDialog.value = false
                                                        return@IconGrid
                                                    }

                                                    var tml = contents.toMutableList()

                                                    tml.removeIf { it -> it.stockTargetId == stockTarget.stockTargetId }
                                                    tml.add(stockTarget)

                                                    contents = tml
                                                    favorites = contents.map { it.isFavorite }
                                                    tabStrings.clear()
                                                    tabStrings.addAll(contents.map { stockTarget ->
                                                        stockTarget.createDate + "\n" + stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
                                                            .map { it.title }.joinToString(";")})



                                                    ActionViewEvent.onStockTargetTopMoved(
                                                        stockTarget,
                                                        viewModel.uiState.tabs[selectedTabIndex],
                                                        false
                                                    )

                                                    showContextMenuIconsDialog.value = false
                                                }

                                                6 -> { //group

                                                    showContextMenuIconsDialog.value = false

                                                    showTabEntitySelectingDialog.value = true

                                                }

                                                7 -> {//remove from group
                                                    Log.d("onIconClick", "Icon 7 clicked")
                                                    println("Icon 7 clicked")

                                                    var tml = contents.toMutableList()

                                                    if (stockTarget.isTopPinned) {
                                                        tml.removeIf { it -> it.code == stockTarget.code }
                                                        stockTarget.isTopPinned =
                                                            !stockTarget.isTopPinned

                                                        viewModel.onEvent(
                                                            ActionViewEvent.onStockTargetTopPinned(
                                                                stockTarget,
                                                                viewModel.uiState.tabs[selectedTabIndex],
                                                                false
                                                            )
                                                        )

                                                    } else {
                                                        tml.removeIf { it -> it.code == stockTarget.code }
                                                    }

                                                    var tabOld = stockTarget.tabs

                                                    stockTarget.tabs =
                                                        stockTarget.tabs.filter { it.title != selectedTab.title }

                                                    viewModel.onEvent(
                                                        ActionViewEvent.onItemTabEntitiesChanged(
                                                            stockTarget = stockTarget,
                                                            tabOld = tabOld,
                                                            tabNew = stockTarget.tabs,
                                                            selectedIndex = selectedTabIndex
                                                        )
                                                    )

                                                    contents = tml
                                                    favorites = contents.map { it.isFavorite }
                                                    tabStrings.clear()
                                                    tabStrings.addAll(contents.map { stockTarget ->
                                                        stockTarget.createDate + "\n" + stockTarget.tabs.filter { it.tabType == TAB_TYPE.TAB_REASON }
                                                            .map { it.title }.joinToString(";")})



                                                    showContextMenuIconsDialog.value = false
                                                }
                                            }
                                        })
                                }
                            })
                    }


                }
            }//Column


        }


    }
}

fun targetToEmailContent(stockTarget: StockTarget): String {
    return stockTarget.toString()
}

fun targetToMarkdown(stockTarget: StockTarget): String {
    return stockTarget.toMarkdown()
}

private fun StockTarget.toMarkdown(): String {
    return buildString {
        appendLine("## ${this@toMarkdown.code}")
        appendLine("### ${this@toMarkdown.name}")
        appendLine("#### ${this@toMarkdown.tabs.joinToString(",") { it.title }}")
        appendLine("#### ${this@toMarkdown.targetReasonList.joinToString(",") { it.title }}")
        appendLine("#### ${this@toMarkdown.targetActionList.joinToString(",") { it.title }}")
        appendLine("#### ${this@toMarkdown.comment}")
    }
}

fun targetToString(stockTarget: StockTarget): String {
    return stockTarget.toString()
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddStockTargetDialog(
    uiState: ActionViewModel.UiState,
    onDismissRequest: () -> Unit,
    selectedTabEntity: TabEntity,
    onStockTargetAdd: (stockTarget: StockTarget) -> Unit

) {
    var code = rememberSaveable { mutableStateOf("") }
    var name = rememberSaveable { mutableStateOf("") }


    var targetReasonList = remember { mutableStateListOf<TabEntity>() }
    var targetActionList = remember { mutableStateListOf<TabEntity>() }

    AlertDialog(
        //shape = RoundedCornerShape(25.dp),
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.add_operation)) },
        text = {
            Column {
                Row {
                    OutlinedTextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = code.value,
                        label = { Text(stringResource(id = R.string.stock_code)) },
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
                        label = { Text(stringResource(id = R.string.stock_name)) },
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
                        stringResource(id = R.string.stock_target_reason),
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
                        text = stringResource(id = R.string.target_action),
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

                    var tabs = targetActionList.plus(targetReasonList)
                    if (!tabs.contains(selectedTabEntity)) tabs = tabs + selectedTabEntity


                    var item = StockTarget(
                        code = code.value, name = name.value,
                        //FIXME 暂时先这样
                        tabs = tabs
                    )

                    Log.d("stocktarget", item.toString())
                    onStockTargetAdd(item)

                }) {
                Text(stringResource(R.string.save), color = Color.White)
            }

        },
        dismissButton = {
            Button(shape = RoundedCornerShape(12.dp), onClick = {
                onDismissRequest()
            }) {
                Text(stringResource(R.string.cancel), color = Color.White)
            }

        })
}

val lowercase_special_string_list = listOf(
    getString(R.string.TAB_ALL).lowercase(Locale.getDefault()),
    getString(R.string.TAB_SPECIAL).lowercase(Locale.getDefault()),
    getString(R.string.TAB_POSITION).lowercase(Locale.getDefault()),
    getString(R.string.TAB_INDEX).lowercase(Locale.getDefault())
)

val special_string_cn_list = listOf("全部", "指数", "头寸", "特别关注")

val special_string_list = listOf(
    getString(R.string.TAB_ALL).lowercase(),
    getString(R.string.TAB_SPECIAL).lowercase(),
    getString(R.string.TAB_POSITION).lowercase(),
    getString(R.string.TAB_INDEX).lowercase()
)


@Composable
fun CenteredTitle(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), contentAlignment = Alignment.Center
    ) {
        Text(text = title, fontSize = 20.sp)
    }
}

@Composable
fun ListItem(
    index: Int,
    stockTarget: StockTarget,
    isFavorite: List<Boolean>,
    itemString: List<String>,
    onClickToShowDialog: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = colors.primarySurface),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Column {
            Text(
                text = stockTarget.code,
                // modifier = Modifier.padding(16.dp)
            )

            Row(
                //modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stockTarget.name,
                    // modifier = Modifier.padding(16.dp)
                )

                if (stockTarget.isTopPinned) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = Golden
                    )
                }


                if (isFavorite[index]) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Default.Star,
                        contentDescription = "favorite",
                        tint = Golden
                    )
                }

            }

        }

        Text(text = itemString[index], modifier = Modifier.padding(16.dp))

        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onClickToShowDialog()
                },
            //painter = painterResource(R.drawable.ic_add),
            imageVector = Icons.Default.Edit,
            contentDescription = stringResource(R.string.add_stock_daily_note),
            tint = Orange
        )
    }

}

@Composable
fun IconGrid(
    selectedItemIndex: Int,
    itemSize: Int,
    stockTarget: StockTarget,
    isFavorite: Boolean,
    onIconClick: (Int) -> Unit
) {

    var isFavoriteInner by remember { mutableStateOf(isFavorite) }


    Column(
        modifier = Modifier.padding(2.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            stringResource(id = R.string.make_profit)
            IconItem(
                index = 0, onIconClick = { iconIndex ->
                    onIconClick(iconIndex)
                    isFavoriteInner = !isFavoriteInner
                }, stockTarget, imageVectors[0], labelList[0], tint = Color.Black
            )
            IconItem( // complete
                index = 1,
                onIconClick,
                stockTarget,
                imageVectors[1],
                labelList[1],
                tint = if (isFavoriteInner) Golden else Gray
            )
            IconItem( //favorite
                index = 2,
                onIconClick = onIconClick,
                stockTarget = stockTarget,
                imageVector = imageVectors[2],
                label = labelList[2],
                tint = if (stockTarget.isTopPinned) Golden else Gray
            )
            IconItem(
                index = 3,
                onIconClick = onIconClick,
                stockTarget = stockTarget,
                imageVector = imageVectors[3],
                label = labelList[3],
                tint = Color.Black
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            IconItem(//move top
                index = 4,
                onIconClick = onIconClick,
                stockTarget = stockTarget,
                imageVector = imageVectors[4],
                label = labelList[4],
                //FIXME 考虑tab top pinned items
                tint = if (selectedItemIndex == 0) Gray else Color.Black,
                enabled = selectedItemIndex != 0
            )
            IconItem(
                index = 5,
                onIconClick = onIconClick,
                stockTarget = stockTarget,
                imageVector = imageVectors[5],
                label = labelList[5],
                tint = colors.primary,

                )
            IconItem(//group
                index = 6,
                onIconClick = onIconClick,
                stockTarget = stockTarget,
                imageVector = imageVectors[6],
                label = labelList[6],
                tint = Color.Black
            )
            IconItem(//group2
                index = 7,
                onIconClick = onIconClick,
                stockTarget = stockTarget,
                imageVector = imageVectors[7],
                label = labelList[7],
                tint = Color.Black
            )


        }
    }
}


//val labelList = listOf(
//  "删除自选", "特别关注", "备注", "置顶", "移到最上", "移到最下", "分组", "移除本组"
//)

val labelList = listOf(
    getString(R.string.complete),
    getString(R.string.string_special),
    getString(R.string.target_note),
    getString(R.string.pin_top),
    getString(R.string.move_top),
    getString(R.string.share),
    getString(R.string.group),
    getString(R.string.remove_from_group),


    //"特别关注", "备注", "置顶", "移到最上", "移到最下", "分组", "移除本组"
)


// 0 delete 1 special 2 note 3 pin top
// 4  move top 5 move last  6 group(tab) 7 batch group(tab)
val imageVectors = listOf(
    Icons.Default.Check,
    Icons.Default.Star,
    Icons.Default.EditNote,
    Icons.Default.PushPin,
    Icons.Default.VerticalAlignTop,
    Icons.Default.Share,
    Icons.Default.Group,
    Icons.Default.GroupOff,

    )

@Composable
fun IconItem(
    index: Int,
    onIconClick: (Int) -> Unit,
    stockTarget: StockTarget,
    imageVector: ImageVector,
    label: String,
    tint: Color,
    enabled: Boolean = true
) {
    Box(modifier = Modifier
        //.padding(4.dp)
        .clickable {
            if (enabled) {
                onIconClick(index)
            }
        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.size(width = 64.dp, height = 64.dp)
            //.background(color = colors.primarySurface)
            //.padding(8.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = imageVector,
                contentDescription = label,
                tint = tint,
            )
            Text(text = label)
        }
    }
}