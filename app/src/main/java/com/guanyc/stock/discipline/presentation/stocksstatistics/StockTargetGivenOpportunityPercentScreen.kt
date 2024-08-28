package com.guanyc.stock.discipline.presentation.stocksstatistics

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.presentation.stocks.StockTargetPeriod
import com.guanyc.stock.discipline.util.settings.OrderType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StockTargetGivenOpportunityPercentScreen(
    navController: NavHostController,
    viewModel: StockTargetGivenOpportunityPercentViewModel = hiltViewModel()
) {

    var orderSettingsVisible by remember { mutableStateOf(false) }

    var selectedStockTargetPeriod by rememberSaveable { mutableStateOf(StockTargetPeriod.WEEK) }

    //var uiState = viewModel.uiState
    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()


    LaunchedEffect(viewModel.uiState) {
        Log.d("LaunchedEffect", "notesUiState")
        if (viewModel.uiState.error != null) {
            scaffoldState.snackbarHostState.showSnackbar(viewModel.uiState.error!!)
            viewModel.onEvent(StockTargetGivenOpportunityPercentEvent.ErrorDisplayed)
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

            //header item
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
                                    viewModel.onEvent(StockTargetGivenOpportunityPercentEvent.GetStockTargetsInDateRange)

                                },
                                onIncludingCompleted = {
                                    viewModel.uiState =
                                        viewModel.uiState.copy(includingCompleted = it)
                                    viewModel.onEvent(StockTargetGivenOpportunityPercentEvent.GetStockTargetsInDateRange)
                                },

                                onIncludingUnCompleted = {
                                    viewModel.uiState =
                                        viewModel.uiState.copy(includingUnCompleted = it)
                                    viewModel.onEvent(StockTargetGivenOpportunityPercentEvent.GetStockTargetsInDateRange)
                                }

                            )

                        }
                    }
                }
            }//header item

            //QueryDateRangeTabRow
            item {
                //周期 或者时间的起始截止
                //FIXME TODO PRO 时间起止文字表示  时间选择前后箭头
                QueryDateRangeTabRow(StockTargetPeriod.entries,
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
                            StockTargetPeriod.WEEK -> {
                                cal = Calendar.getInstance()
                                cal.add(Calendar.DAY_OF_YEAR, -7)
                                dateFrom = df.format(cal.time)
                                viewModel.uiState = viewModel.uiState.copy(
                                    dateFrom = df.format(cal.time),
                                    dateTo = df.format(Date()),
                                )
                            }

                            StockTargetPeriod.MONTH -> {
                                cal = Calendar.getInstance()
                                cal.add(Calendar.MONTH, -1)
                                dateFrom = df.format(cal.time)
                                viewModel.uiState = viewModel.uiState.copy(
                                    dateFrom = df.format(cal.time),
                                    dateTo = df.format(Date()),
                                )
                            }

                            StockTargetPeriod.QUARTER -> {
                                cal = Calendar.getInstance()
                                cal.add(Calendar.MONTH, -3)
                                dateFrom = df.format(cal.time)
                                viewModel.uiState = viewModel.uiState.copy(
                                    dateFrom = df.format(cal.time),
                                    dateTo = df.format(Date()),
                                )
                            }

                            StockTargetPeriod.YEAR -> {
                                cal = Calendar.getInstance()
                                cal.add(Calendar.YEAR, -1)
                                dateFrom = df.format(cal.time)
                                viewModel.uiState = viewModel.uiState.copy(
                                    dateFrom = df.format(cal.time),
                                    dateTo = df.format(Date()),
                                )
                            }

                            StockTargetPeriod.CUSTOMIZED -> {
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

                        viewModel.onEvent(StockTargetGivenOpportunityPercentEvent.GetStockTargetsInDateRange)

                    })
            }//item time period tab

            item {
                IsGivenOpportunityCircularBar(
                    entries = viewModel.uiState.groupByIsGivenOpportunity,
                    dateFrom = viewModel.uiState.dateFrom,
                    dateTo = viewModel.uiState.dateTo
                )
            }//item circular chart

            item {
                IsGivenOpportunityHistogramChart(
                    entries = viewModel.uiState.groupByIsGivenOpportunity,
                    dateFrom = viewModel.uiState.dateFrom,
                    dateTo = viewModel.uiState.dateTo
                )
            }


        }//LazyColumn

    }
}


@Composable
fun IsGivenOpportunityHistogramChart(
    entries: Map<Boolean, List<StockTarget>>,
    dateFrom: String,
    dateTo: String,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 85f,
    showPercentage: Boolean = true,
    onClick: () -> Unit = {}
) {
    val TAG = "IsGivenOpportunityHistogramChart"

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp,
        modifier = modifier.fillMaxWidth().padding(6.dp)
    ) {
        Column(Modifier.clickable { onClick() }) {
            val moods by remember(entries) {
                derivedStateOf {
                    entries.map { it.key to it.value.size }.toMap()
                }
            }

            Text(
                text = dateFrom + "-" + dateTo,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.target_reason_is_given_opportunity_statistics),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center
            )

            if (entries.isNotEmpty()) {

                var sortedEntires: List<Map.Entry<Boolean, Int>> =
                    moods.entries.sortedByDescending { it.value }

                val labels: List<String> = sortedEntires.map {
                    when {
                        it.key -> {
                            "获得机会"
                        }

                        else -> {
                            "没机会"
                        }
                    }
                }
                val data: List<Int> = sortedEntires.map { it.value }

                HistogramBottomUp(
                    data = data, labels = labels
                )

            } else {
                Text(
                    text = stringResource(R.string.no_data_yet),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }


        }


    }

}


@Composable
fun IsGivenOpportunityCircularBar(
    modifier: Modifier = Modifier,
    entries: Map<Boolean, List<StockTarget>>,
    //一周内
    dateFrom: String = SimpleDateFormat("yyyyMMdd").format(Date()),
    dateTo: String = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis() - 7 * 86400 * 1000)),
    strokeWidth: Float = 85f,
    showPercentage: Boolean = true,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp,
        modifier = modifier.fillMaxWidth().padding(6.dp)
    ) {
        Column(Modifier.clickable { onClick() }) {

            val moods: Map<String, Float> by remember(entries) {
                derivedStateOf {
                    entries.BooleanKeytoPercentages()
                }
            }

            Text(
                text = dateFrom + "-" + dateTo,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.target_reason_is_given_opportunity_statistics),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center
            )

            if (entries.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    var currentAngle = remember { 90f }
                    Canvas(
                        modifier = Modifier.fillMaxSize().padding(29.dp)
                    ) {
                        var sortedEntires: List<Map.Entry<String, Float>> =
                            moods.entries.sortedByDescending { it.value }

                        sortedEntires.forEachIndexed { index, entry ->
                            drawArc(
                                color = getColorForTitle(index),
                                startAngle = currentAngle,
                                sweepAngle = entry.value * 360f,
                                useCenter = false,
                                size = Size(size.width, size.width),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
                            )
                            currentAngle += entry.value * 360f
                        }

                    }//canvas

                    if (showPercentage) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            var sortedEntires: List<Map.Entry<String, Float>> =
                                moods.entries.sortedByDescending { it.value }

                            sortedEntires.forEachIndexed { index, entry ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = buildAnnotatedString {
                                        withStyle(
                                            SpanStyle(
                                                fontWeight = FontWeight.Bold,
                                                color = getColorForTitle(index)
                                            )
                                        ) {
                                            append(entry.key)
                                        }

                                        append(" 出现 ")
                                        append(entries.get(entry.key.contains("获得"))?.size.toString())
                                        append(" 次 ")
                                        append(" 占比 ")
                                        append((entry.value * 100).toInt().toString())
                                        append("%")
                                    })
                                }
                            }

                        }

                    }

                }
            } else {
                Text(
                    text = stringResource(R.string.no_data_yet),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}