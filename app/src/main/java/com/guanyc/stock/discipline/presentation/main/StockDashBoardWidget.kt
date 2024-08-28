package com.guanyc.stock.discipline.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.presentation.util.Screen
import com.guanyc.stock.discipline.theme.LightGray
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.settings.StockNoteColor

val TAG: String = "StockDashBoardWidget"

@OptIn(ExperimentalPermissionsApi::class, ExperimentalLayoutApi::class)

@Composable
fun StockDashBoardWidget(
    modifier: Modifier,
    navController: NavHostController,
    unCompleted: List<StockNote>,
    completed: List<StockNote>,
    onclick: () -> Unit,
    onAddStockNoteClicked: () -> Unit = {},
) {


    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        val context = LocalContext.current
        val isDark = !MaterialTheme.colors.isLight

        Column(
            modifier = modifier.clickable { onclick() }.padding(8.dp)
        ) {

            //第一行
            Row(
                Modifier.fillMaxWidth().padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.stocknote_title), style = MaterialTheme.typography.body1
                )
                Icon(painterResource(R.drawable.ic_add),
                    stringResource(R.string.add_stock_daily_note),
                    modifier = Modifier.size(18.dp).clickable {
                        onAddStockNoteClicked()
                    })
            }

            //间隔
            Spacer(Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.padding(2.dp).fillMaxSize().clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) Color.DarkGray else LightGray),
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.Top,

                ) {

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "未完结: "
                    )
                }
                unCompleted.forEach { stockNote ->
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                            .background(StockNoteColor.ORANGE.color).padding(12.dp).clickable {
                                val noteDetailSreen =
                                    Screen.StockDailyNoteDetailScreen.route.replace(
                                        oldValue = "{${Constants.STOCK_DAILY_NOTE_ID_ARG}}",
                                        newValue = "${stockNote.stockNoteId}"
                                    )

                                navController.navigate(noteDetailSreen)

                            }, verticalAlignment = Alignment.CenterVertically
                    ) {
                        var title = ""
                        if(stockNote.createDate.isNullOrEmpty()){
                            title = stockNote.stockNoteId.toString()
                        }else{
                            title = stockNote.createDate
                        }

                        Text(
                            text = title, style = TextStyle.Default.copy(
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )

                        )

                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }//flowrow


        }

    }

}


fun allStockNoteCompleted(events: List<StockNote>): Boolean {
    //return !events.any { it -> !it.isCompleted }
    return events.all { it -> it.isCompleted }
}
