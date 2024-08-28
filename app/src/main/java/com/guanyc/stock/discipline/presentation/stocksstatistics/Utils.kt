package com.guanyc.stock.discipline.presentation.stocksstatistics

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.presentation.main.components.TabEntity
import com.guanyc.stock.discipline.theme.colors


fun getColorForTitle(indexOf: Int): Color {
    val indexMod = indexOf % colors.size
    return colors.get(indexMod)
}

@Composable
fun HistogramBottomUp(
    modifier: Modifier = Modifier,
    data: List<Int>,
    labels: List<String>,
    barColor: Color = Color.Blue
) {
    val maxDataValue = data.maxOrNull() ?: 0

    val barWidth = 32.dp
    val spacing = 20.dp

    Column(
        modifier = modifier.padding(16.dp).background(Color.White).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            data.forEachIndexed { index, value ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(barWidth)
                ) {
                    Canvas(
                        modifier = Modifier.height(200.dp).width(barWidth)
                    ) {
                        val barHeight = size.height * (value.toFloat() / maxDataValue)
                        drawRect(
                            color = getColorForTitle(index),//barColor,
                            topLeft = Offset(0f, size.height - barHeight),
                            size = Size(size.width, barHeight)
                        )
                    }
                    Text(
                        text = value.toString(),
                        style = TextStyle(fontSize = 14.sp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier.width(barWidth),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun Histogram(
    modifier: Modifier = Modifier,
    data: List<Int>,
    labels: List<String>,
    barColor: Color = Color.Blue
) {
    val maxDataValue = data.maxOrNull() ?: 0
    val barWidth = 20.dp//40.dp
    val spacing = 8.dp

    Column(
        modifier = modifier.padding(16.dp).background(Color.White).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            data.forEachIndexed { index, value ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(barWidth)
                ) {
                    Canvas(modifier = Modifier.height(200.dp).width(barWidth)) {
                        drawRect(
                            //color = barColor,
                            color = getColorForTitle(index),
                            size = size.copy(height = size.height * (value.toFloat() / maxDataValue))
                        )
                    }
                    Text(
                        text = value.toString(),
                        style = TextStyle(fontSize = 14.sp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier.width(barWidth),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


fun Map<Boolean, List<StockTarget>>.BooleanKeytoPercentages(): Map<String, Float> {
    var count = this.values.fold(0) { acc, targetReasonList ->
        acc + targetReasonList.size
    }

    val mapped = this.map { me ->
        when (me.key) {
            true -> "获得机会"
            false -> "没机会"
        } to (me.value.size.toFloat() / count.toFloat())
    }

    val mappedMap = mapped.toMap()

    return mappedMap
}


fun Map<String, List<TabEntity>>.toPercentages(): Map<String, Float> {
    var count = this.values.fold(0) { acc, targetReasonList ->
        acc + targetReasonList.size
    }

    Log.d("toPercentages", "count is $count")

    val mapped: List<Pair<String, Float>> =
        this.map { me -> me.key to (me.value.size.toFloat() / count.toFloat()).toFloat() }

    Log.d("toPercentages", "mapped")
    Log.d("toPercentages", mapped.toString())

    val mappedMap: Map<String, Float> = mapped.toMap()

    Log.d("toPercentages", "mappedMap")
    Log.d("toPercentages", mappedMap.toString())

    return mappedMap

}