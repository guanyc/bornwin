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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import com.guanyc.stock.discipline.presentation.stocks.StockTargetPeriod
import com.guanyc.stock.discipline.util.BackupUtil.toJson
import com.guanyc.stock.discipline.util.settings.OrderType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


val TAG_StockCodesChartByTargetReasonScreen = "StockCodesChartByTargetReasonScreen"

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StockTargetsGroupByTargetReasonScreen(
    navController: NavHostController,
    viewModel: StockTargetsGroupByTargetReasonViewModel = hiltViewModel()
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
            viewModel.onEvent(StockTargetsGroupByTargetReasonEvent.ErrorDisplayed)
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
                                    viewModel.onEvent(StockTargetsGroupByTargetReasonEvent.GetStockTargetsInDateRange)

                                },
                                onIncludingCompleted = {
                                    viewModel.uiState =
                                        viewModel.uiState.copy(includingCompleted = it)
                                    viewModel.onEvent(StockTargetsGroupByTargetReasonEvent.GetStockTargetsInDateRange)
                                },

                                onIncludingUnCompleted = {
                                    viewModel.uiState =
                                        viewModel.uiState.copy(includingUnCompleted = it)
                                    viewModel.onEvent(StockTargetsGroupByTargetReasonEvent.GetStockTargetsInDateRange)
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

                        viewModel.onEvent(StockTargetsGroupByTargetReasonEvent.GetStockTargetsInDateRange)

                    })
            }//item time period tab

            item {
                TargetReasonCircularBar(
                    entries = viewModel.uiState.targetGroupByReasonTitle,
                    dateFrom = viewModel.uiState.dateFrom,
                    dateTo = viewModel.uiState.dateTo
                )
            }//item circular chart

            item {
                TargetReasonHistogramChart(
                    entries = viewModel.uiState.targetGroupByReasonTitle,
                    dateFrom = viewModel.uiState.dateFrom,
                    dateTo = viewModel.uiState.dateTo
                )
            }


        }//LazyColumn

    }
}


@Composable
fun TargetReasonHistogramChart(
    modifier: Modifier = Modifier,
    entries: Map<String, List<TabEntity>>,
    //一周内
    dateFrom: String = SimpleDateFormat("yyyyMMdd").format(Date()),
    dateTo: String = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis() - 7 * 86400 * 1000)),
    strokeWidth: Float = 85f,
    showPercentage: Boolean = true,
    onClick: () -> Unit = {}
) {
    val TAG = "TargetReasonHistogramChart"
    Log.d(TAG, entries.toJson().toString())

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp,
        modifier = modifier.fillMaxWidth().padding(6.dp)
    ) {

        Column(Modifier.clickable { onClick() }) {
            val mostFrequentMood by remember(entries) {

                derivedStateOf {

                    val entriesGrouped = entries

                    val max: Int = entriesGrouped.maxOf { it.value.size }

                    val filter: Map<String, List<TabEntity>> =
                        entriesGrouped.filter { it.value.size == max }

                    val maxByOrNull: Map.Entry<String, List<TabEntity>>? =
                        filter.maxByOrNull { it.key }

                    //this is the closure value
                    maxByOrNull?.key ?: entriesGrouped.filter { it.value.size == max }
                }
            }

            val moods: Map<String, Int> by remember(entries) {
                derivedStateOf {
                    val x: List<Pair<String, Int>> = entries.map { it.key to it.value.size }
                    x.toMap()
                }
            }


            Text(
                text = dateFrom + "-" + dateTo,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.target_reason_statistics),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center
            )


            if (entries.isNotEmpty()) {

                var sortedEntires: List<Map.Entry<String, Int>> =
                    moods.entries.sortedByDescending { it.value }

                val labels = sortedEntires.map { it.key }
                val data = sortedEntires.map { it.value }

                HistogramBottomUp(
                    data = data,
                    labels = labels
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


//chatgpt
// how to ge android compose current drawing area width
// In Jetpack Compose, you can obtain the current width of the drawing area
// (or a specific composable) using the Modifier.onGloballyPositioned modifier
// in combination with the LayoutCoordinates class. Here is a detailed example of how to achieve this:

/*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.composeexample.ui.theme.ComposeExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeExampleTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CurrentWidthExample()
                }
            }
        }
    }
}*/
//chatgpt continue
@Composable
fun CurrentWidthExample() {
    var width by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->
                width = layoutCoordinates.size.width
            }
    ) {
        // You can now use the width variable as needed
    }

    // Display the width for demonstration purposes
    Text(text = "Current width: $width dp")
}


//baidu
//在Android Jetpack Compose中，获取当前的屏幕宽度可以通过LocalConfiguration来实现。LocalConfiguration是一个Compose提供的CompositionLocal，它包含了当前设备的配置信息，包括屏幕的尺寸。
//
//以下是一个简单的示例代码，展示了如何在Compose中获取当前屏幕宽度
//import androidx.compose.foundation.layout.BoxWithConstraints
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.lerp
//
//@Composable
//fun ScreenWidthIndicator() {
//    val configuration = LocalConfiguration.current
//    val screenWidthDp = configuration.screenWidthDp.dp
//
//    BoxWithConstraints(modifier = Modifier.size(screenWidthDp)) {
//        val color = lerp(Color.Red, Color.Blue, constraints.maxWidth.value / screenWidthDp.value)
//        Box(modifier = Modifier.size(constraints.maxWidth.dp).background(color, CircleShape))
//    }
//}
//在这个示例中，BoxWithConstraints用于获取组件的最大宽度，然后通过lerp函数根据当前宽度与最大宽度的比例计算颜色的渐变
// 。这个组件会显示为一个圆形，其颜色从红色渐变到蓝色，反映当前屏幕宽度的使用情况。


@Composable
fun TargetReasonCircularBar(
    modifier: Modifier = Modifier,
    entries: Map<String, List<TabEntity>>,
    //一周内
    dateFrom: String = SimpleDateFormat("yyyyMMdd").format(Date()),
    dateTo: String = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis() - 7 * 86400 * 1000)),
    strokeWidth: Float = 85f,
    showPercentage: Boolean = true,
    onClick: () -> Unit = {}
) {
    val TAG = "TargetReasonCircularBar"

    Log.d(TAG, entries.toJson().toString())

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp,
        modifier = modifier.fillMaxWidth().padding(6.dp)
    ) {
        Column(Modifier.clickable { onClick() }) {
            val mostFrequentMood by remember(entries) {

                derivedStateOf {

                    val entriesGrouped = entries

                    val max: Int = entriesGrouped.maxOf { it.value.size }

                    val filter: Map<String, List<TabEntity>> =
                        entriesGrouped.filter { it.value.size == max }

                    val maxByOrNull: Map.Entry<String, List<TabEntity>>? =
                        filter.maxByOrNull { it.key }

                    //this is the closure value
                    maxByOrNull?.key ?: entriesGrouped.filter { it.value.size == max }
                }
            }

            val moods: Map<String, Float> by remember(entries) {
                derivedStateOf {
                    entries.toPercentages()
                }
            }

            Text(
                text = dateFrom + "-" + dateTo,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.target_reason_statistics),
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

                    //画图的canvas
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

                        /*
                        for ((title, percentage) in moods) {
                            drawArc(
                                color = getColorForTitle(moods.keys.toList().indexOf(title)),
                                startAngle = currentAngle,
                                sweepAngle = percentage * 360f,
                                useCenter = false,
                                size = Size(size.width, size.width),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
                            )
                            currentAngle += percentage * 360f
                        }*/
                    }//canvas

                    //中间的文字信息
                    if (showPercentage) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            var sortedEntires: List<Map.Entry<String, Float>> =
                                moods.entries.sortedByDescending { it.value }

                            sortedEntires.forEachIndexed { index, entry ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    //TODO annotated String 加大 title
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
                                        append(entries.get(entry.key)?.size.toString())
                                        append(" 次 ")
                                        append(" 占比 ")
                                        append((entry.value * 100).toInt().toString())
                                        append("%")
                                    })
                                    //Text("" + entry.key + " 出现" + entries.get(entry.key)?.size.toString() + "次" + ", 占比 " + (entry.value * 100).toInt() + "%")
                                }
                            }

                            /*
                            for((title, percentage) in moods){
                                Row(verticalAlignment = Alignment.CenterVertically){
                                    //TODO annotated String 加大 title
                                    Text(""+ title + " 出现"+entries.get(title)?.size.toString()+"次"+", 占比 "+(percentage * 100).toInt()+"%")
                                }
                                //Spacer(Modifier.width(8.dp))
                            }*/
                        }
                    }

                }

            } //if
            else {
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


//----------------------


data class ChartSegment(
    val value: Float, val color: Color
)

@Composable
fun CircularChart(
    modifier: Modifier = Modifier, segments: List<ChartSegment>
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val totalValue = segments.sumOf { it.value.toDouble() }.toFloat()
        var startAngle = -90f

        segments.forEach { segment ->
            val sweepAngle = (segment.value / totalValue) * 360f
            drawArc(
                color = segment.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun CircularChartExample() {
    val segments = listOf(
        ChartSegment(40f, Color(0xFFE57373)),
        ChartSegment(30f, Color(0xFF81C784)),
        ChartSegment(20f, Color(0xFF64B5F6)),
        ChartSegment(10f, Color(0xFFFFF176))
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        CircularChart(
            modifier = Modifier.size(200.dp).padding(16.dp), segments = segments
        )
    }
}