package com.guanyc.stock.discipline.presentation.stocksstatistics


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.util.StockTargetPeriod
import com.guanyc.stock.discipline.util.StockTargetPeriod.*
import com.guanyc.stock.discipline.util.toInt
import com.guanyc.stock.discipline.util.toStockTargetPeriod
import com.guanyc.stock.discipline.util.settings.OrderType
import com.guanyc.stock.discipline.util.settings.OrderType.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

val orderOptions = listOf(DESC().title, ASC().title)
val TAG = "RecentStockCodes"

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RecentStockCodesInStockNotesScreen(
    navController: NavHostController, viewModel: RecentStockCodesViewModel = hiltViewModel()
) {
    var orderSettingsVisible by remember { mutableStateOf(false) }

    var selectedStockTargetPeriod by rememberSaveable { mutableStateOf(WEEK) }

    //var uiState = viewModel.uiState
    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val queryStr =  remember {
        mutableStateOf("")
    }


    LaunchedEffect(viewModel.uiState) {
        Log.d("LaunchedEffect", "notesUiState")
        if (viewModel.uiState.error != null) {
            scaffoldState.snackbarHostState.showSnackbar(viewModel.uiState.error!!)
            viewModel.onEvent(RecentStockCodesEvent.ErrorDisplayed)
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.stockcodes_in_recent_notes),
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                )
            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
        )
    }) {

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 4.dp)
        ) {
            item {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { orderSettingsVisible = !orderSettingsVisible }) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(R.drawable.ic_settings_sliders),
                                contentDescription = stringResource(R.string.order_by)
                            )
                        }


                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            value = queryStr.value,
                            label = { Text("查询注释内容") },
                            onValueChange = {
                                queryStr.value = it

                                viewModel.uiState = if (it.isEmpty()) {
                                    viewModel.uiState.copy(queryStringForActionReview = "")
                                } else {
                                    viewModel.uiState.copy(queryStringForActionReview = it)
                                }

                                viewModel.onEvent(RecentStockCodesEvent.QueryChanged)
                             },
                            modifier = Modifier
                                .padding(2.dp)
                                .weight(4f),
                        )

                        IconButton(onClick = {  }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                modifier = Modifier.size(25.dp),
                                contentDescription = stringResource(R.string.order_by)
                            )
                        }


                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        //horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        AnimatedVisibility(visible = orderSettingsVisible) {

                            QuerySettingsSectionInRow(

                                viewModel.uiState.orderType.orderTitle,

                                includingCompleted = viewModel.uiState.includingCompleted,
                                includingUnCompleted = viewModel.uiState.includingUnCompleted,

                                onOptionSelected = {
                                    Log.d(TAG, "selectedOrderOption " + it)

                                    if (it == OrderType.DESC().title) {
                                        viewModel.uiState =
                                            viewModel.uiState.copy(orderType = OrderType.DESC())
                                    } else if (it == OrderType.ASC().title) {
                                        viewModel.uiState =
                                            viewModel.uiState.copy(orderType = OrderType.ASC())
                                    }

                                    Log.d(
                                        TAG,
                                        "viewModel.uiState.orderType.orderTitle is  " + viewModel.uiState.orderType.orderTitle
                                    )

                                    //viewModel.onEvent(StockNoteListEvent.ErrorDisplayed)
                                    viewModel.onEvent(RecentStockCodesEvent.GetStockCodeInDateRange)

                                },
                                onIncludingCompleted = {
                                    viewModel.uiState =
                                        viewModel.uiState.copy(includingCompleted = it)
                                    viewModel.onEvent(RecentStockCodesEvent.GetStockCodeInDateRange)
                                },

                                onIncludingUnCompleted = {
                                    viewModel.uiState =
                                        viewModel.uiState.copy(includingUnCompleted = it)
                                    viewModel.onEvent(RecentStockCodesEvent.GetStockCodeInDateRange)
                                }

                            )

                        }
                    }
                }
            }


            item {
                //周期 或者时间的起始截止
                //FIXME TODO PRO 时间起止文字表示  时间选择前后箭头
                QueryDateRangeTabRow(
                    StockTargetPeriod.entries,
                    selectedStockTargetPeriod = selectedStockTargetPeriod,
                    onChange = {

                        selectedStockTargetPeriod = it
                        Log.d(TAG, "selectedStockTargetPeriod is " + selectedStockTargetPeriod)

                        val df = SimpleDateFormat("yyyyMMdd")
                        var cal = Calendar.getInstance()
                        cal.add(Calendar.DAY_OF_YEAR, -7)
                        var dateFrom: String = df.format(cal.time)
                        val dateTo: String = df.format(Date())

                        viewModel.uiState = viewModel.uiState.copy(
                            dateFrom = df.format(cal.time),
                            dateTo = df.format(Date()),
                        )

                        when (selectedStockTargetPeriod) {
                            WEEK -> {
                                cal = Calendar.getInstance()
                                cal.add(Calendar.DAY_OF_YEAR, -7)
                                dateFrom = df.format(cal.time)
                                viewModel.uiState = viewModel.uiState.copy(
                                    dateFrom = df.format(cal.time),
                                    dateTo = df.format(Date()),
                                )
                            }

                            MONTH -> {
                                cal = Calendar.getInstance()
                                cal.add(Calendar.MONTH, -1)
                                dateFrom = df.format(cal.time)
                                viewModel.uiState = viewModel.uiState.copy(
                                    dateFrom = df.format(cal.time),
                                    dateTo = df.format(Date()),
                                )
                            }

                            QUARTER -> {
                                cal = Calendar.getInstance()
                                cal.add(Calendar.MONTH, -3)
                                dateFrom = df.format(cal.time)
                                viewModel.uiState = viewModel.uiState.copy(
                                    dateFrom = df.format(cal.time),
                                    dateTo = df.format(Date()),
                                )
                            }

                            YEAR -> {
                                cal = Calendar.getInstance()
                                cal.add(Calendar.YEAR, -1)
                                dateFrom = df.format(cal.time)
                                viewModel.uiState = viewModel.uiState.copy(
                                    dateFrom = df.format(cal.time),
                                    dateTo = df.format(Date()),
                                )
                            }

                            CUSTOMIZED -> {
                                //TODO in pro version，normal version's implementation is same as week
                                cal = Calendar.getInstance()
                                cal.add(Calendar.DAY_OF_YEAR, -7)
                                dateFrom = df.format(cal.time)

                                viewModel.uiState = viewModel.uiState.copy(
                                    dateFrom = df.format(cal.time),
                                    dateTo = df.format(Date()),
                                )
                            }
                        }

                        Log.d(TAG, "dateFrom is " + viewModel.uiState.dateFrom)
                        Log.d(TAG, "dateTo is " + viewModel.uiState.dateTo)

                        viewModel.onEvent(RecentStockCodesEvent.GetStockCodeInDateRange)

                    })
            }


            // no item message
            if (viewModel.uiState.stockCodeMap.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.no_stock_target_message),
                                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }

                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                modifier = Modifier.size(125.dp),
                                painter = painterResource(id = R.drawable.stock_img_foreground),
                                contentDescription = stringResource(R.string.no_stock_target_message),
                                alpha = 0.7f,
                            )
                        }

                    }//column
                }
            }
            // no item message

            var lp: List<Pair<String, List<StockTarget>>> = viewModel.uiState.stockCodeMap.toList();
            items(lp, key = { it.first }) {

                val stockNotes: List<StockTarget> = it.second

                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItemPlacement(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 8.dp
                ) {
                    Column(
                        Modifier
                            .clickable {
                                //onClick()
                            }
                            .padding(12.dp)) {
                        Row(
                            Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            //也可以适用flowrow
                            horizontalArrangement = Arrangement.Start
                        ) {
                            val name = it.second.first().name

                            Text(text = "${it.first}-${name} : ")

                            Text(text = it.second.map { it.createDate }.joinToString(","))
                        }
                    }
                }//Card
            }//items

        }//Lazycolumn
        // }//if-else
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuerySettingsSectionInRow(
    selectedOption: String,
    includingCompleted: Boolean,
    includingUnCompleted: Boolean,
    onOptionSelected: (String) -> Unit,
    onIncludingCompleted: (Boolean) -> Unit,
    onIncludingUnCompleted: (Boolean) -> Unit,

    ) {


    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {

            Text(
                text = "排序 : "
            )

            orderOptions.forEach { text ->

                // below line is use to get context.
                //val context = LocalContext.current

                // below line is use to
                // generate radio button
                RadioButton(
                    // inside this method we are
                    // adding selected with a option.
                    selected = (text == selectedOption),
                    //modifier = Modifier.padding(all = Dp(value = 0F)),
                    onClick = {
                        // inside on click method we are setting a
                        // selected option of our radio buttons.
                        onOptionSelected(text)

                        // after clicking a radio button
                        // we are displaying a toast message.
                        //context.let {
                        //Toast.makeText(context, text.toString(), Toast.LENGTH_LONG).show()
                        //}
                    })
                // below line is use to add
                // text to our radio buttons.
                Text(
                    text = text.toString(), modifier = Modifier.padding(start = 4.dp)
                )

            }

        }


        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(checked = includingCompleted, onCheckedChange = { checked ->
                onIncludingCompleted(checked)

            })

            Text(
                text = "包含已经完成条目"
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Checkbox(checked = includingUnCompleted, onCheckedChange = { checked ->
                onIncludingUnCompleted(checked)
            })

            Text(
                text = "包含未完成条目"
            )
        }
    }
}


@Composable
fun QueryDateRangeTabRow(
    periods: List<StockTargetPeriod>,
    selectedStockTargetPeriod: StockTargetPeriod,
    onChange: (StockTargetPeriod) -> Unit
) {

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        AnimatedTabIndicatorForNoteColor(
            Modifier.tabIndicatorOffset(
                tabPositions[selectedStockTargetPeriod.toInt()]
            )
        )
    }

    TabRow(
        selectedTabIndex = selectedStockTargetPeriod.toInt(),
        indicator = indicator,
        modifier = Modifier.clip(RoundedCornerShape(14.dp))
    ) {
        periods.forEachIndexed { index, stockTargetPeriod ->
            androidx.compose.material.Tab(
                text = { Text(stringResource(stockTargetPeriod.title)) },
                selected = selectedStockTargetPeriod.toInt() == index,

                onClick = {
                    onChange(index.toStockTargetPeriod())
                },

                modifier = Modifier.background(stockTargetPeriod.color)

            )

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

@Preview
@Composable
fun RecentStockCodesPreview() {
    RecentStockCodesInStockNotesScreen(rememberNavController())
}

