package com.guanyc.stock.discipline.presentation.stocks


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.TargetConstants
import com.guanyc.stock.discipline.domain.model.tabActions
import com.guanyc.stock.discipline.domain.model.tabReasons
import com.guanyc.stock.discipline.domain.model.targetActionList
import com.guanyc.stock.discipline.domain.model.targetReasonList
import com.guanyc.stock.discipline.presentation.main.components.TabEntity


//fun Map<String, List<TargetReasonMeta>>.toPercentages(): Map<String, Float> {


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TargetCard(
    index: Int,
    targetListIndex: MutableState<Long>,
    targetConstants: TargetConstants,
    stockNote: StockNote,
    stockTarget: StockTarget,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClickDelete: () -> Unit = {},
    onUpdateStockTarget: (StockNote, StockTarget) -> Unit,
    listExpanded: Boolean
) {

    Log.d("stocklist", "StockTargetSpaceRegularCard")
    Log.d("stocklist", stockTarget.toString())


    //var reasonList = emptyList<Int>().toMutableList()
    //var actionList = emptyList<Int>().toMutableList()


    var targetReasonList = remember { mutableStateListOf<TabEntity>() }
    var targetActionList: SnapshotStateList<TabEntity> =
        remember { mutableStateListOf<TabEntity>() }

    if (stockTarget.targetReasonList.isNotEmpty()) {

        targetReasonList.clear()
        targetReasonList.addAll(stockTarget.targetReasonList)
    }


    if (stockTarget.targetActionList.isNotEmpty()) {
        targetActionList.clear()
        targetActionList.addAll(stockTarget.targetActionList)
    }

    val isCompleted by remember { mutableStateOf(stockTarget.isCompleted) }

    var openDialogDeleteTargetValue by remember { mutableStateOf(false) }

    var openDialogEditTargetValue = rememberSaveable { mutableStateOf(false) }

    val targetCardExpanded = rememberSaveable { mutableStateOf(listExpanded) }

    Log.d("stocklist", "listExpanded is " + listExpanded.toString())

    Card(
        modifier = modifier.padding(8.dp),
        //shape = RoundedCornerShape(12.dp),
        shape = MaterialTheme.shapes.large,
        backgroundColor = backgroundColor,
        elevation = 6.dp,
    ) {
        Column(
            Modifier
                .clickable {
                    // TODO
                }
                .fillMaxWidth()
                //.aspectRatio(0.67f)
                .padding(4.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 8.dp)
                    .background(
                        //color = MaterialTheme.colors.secondaryVariant,
                        color = MaterialTheme.colors.primary, shape = RoundedCornerShape(8.dp)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                StockNoteCheckBox(
                    isComplete = stockTarget.isCompleted, borderColor = Color.Black
                ) {
                    //none
                    //isCompleted = !isCompleted
                    //stockTarget.isCompleted = isCompleted
                    //onUpdateStockTarget(stockNote, stockTarget)
                }

                //code -name
                Text(
                    text = stockTarget.name + "-" + stockTarget.code,
                    style = MaterialTheme.typography.h6.copy(color = Color.White),
                    maxLines = 1,
                )

                Icon(
                    imageVector = Icons.Default.Edit,
                    stringResource(id = R.string.edit),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            openDialogEditTargetValue.value = true
                        },
                    //tint = Golden
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    stringResource(id = R.string.delete),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            openDialogDeleteTargetValue = true
                        },
                    //tint = Color.Red,
                )

                //
                if (openDialogDeleteTargetValue) {
                    AlertDialog(shape = RoundedCornerShape(25.dp),
                        onDismissRequest = { openDialogDeleteTargetValue = false },
                        title = {
                            Text(
                                listOf(
                                    stockTarget.name, stockTarget.code
                                ).reversed().joinToString("-")
                            )
                        },
                        text = {
                            Text(
                                stringResource(
                                    R.string.delete_stock_target_confirmation_message,
                                    stockTarget.name + stockTarget.code
                                )
                            )
                        },
                        confirmButton = {
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                                shape = RoundedCornerShape(25.dp),
                                onClick = {
                                    openDialogDeleteTargetValue = false
                                    onClickDelete()
                                },
                            ) {
                                Text(stringResource(R.string.delete), color = Color.White)
                            }
                        },
                        dismissButton = {
                            Button(shape = RoundedCornerShape(25.dp), onClick = {
                                openDialogDeleteTargetValue = false
                            }) {
                                Text(stringResource(R.string.cancel), color = Color.White)
                            }
                        })
                }//delete dialog


                if (targetCardExpanded.value) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_expand_less_24),
                        contentDescription = "Expand Less",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { targetCardExpanded.value = !targetCardExpanded.value },
                        tint = Color.Green
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_expand_more_24),
                        contentDescription = "Expand More",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { targetCardExpanded.value = !targetCardExpanded.value },
                        tint = Color.Green,

                        )

                }
            }  //row header

            if (openDialogEditTargetValue.value) {
                if (targetConstants != null) {
                    TargetCardDialogEdit(
                        targetConstants,
                        stockNote,
                        stockTarget,
                        openDialogEditTargetValue,
                        onUpdateStockTarget = onUpdateStockTarget
                    )
                }
            }

            if (targetCardExpanded.value) {
                //reason
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = 3,
                ) {

                    Text(
                        textAlign = TextAlign.Center,
                        text = "加标的原因:",
                        style = MaterialTheme.typography.body1.copy(color = Color.White)
                    )

                    var tc = targetConstants

                    if (tc != null) {
                        tc.tabReasons.forEachIndexed { index, meta ->
                            if (targetReasonList.contains(meta)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(enabled = false,
                                        checked = targetReasonList.contains(meta),
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                //reasonList.add(index)
                                            } else {
                                                //reasonList.remove(index)
                                            }
                                        })
                                    Text(meta.title)
                                }

                            }
                        }
                    }


                }//flowrow


                //actionplan
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = 3,
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "预期操作:",
                        style = MaterialTheme.typography.body1.copy(color = Color.White),
                    )


                    var tc = targetConstants
                    if (tc != null) {
                        tc.tabActions.forEachIndexed { index, meta ->
                            if (targetActionList.contains(meta)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(enabled = false,
                                        checked = targetActionList.contains(meta),
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                //reasonList.add(index)
                                            } else {
                                                //reasonList.remove(index)
                                            }
                                        })
                                    Text(meta.title)
                                }

                            }
                        }
                    }


                }//flowrow

                Divider()

                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = 3,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            //textAlign = TextAlign.Center,
                            text = "实操:",
                            style = MaterialTheme.typography.body1.copy(color = Color.White),
                        )

                        Checkbox(enabled = false,
                            checked = stockTarget.isOpportunityGiven,
                            onCheckedChange = { checked ->
                                if (checked) {
                                } else {
                                }
                            })
                        Text(
                            text = "市场给机会",
                            //style = MaterialTheme.typography.body2.copy(color = Color.White),
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            enabled = false,
                            checked = stockTarget.isPlanActed,
                            onCheckedChange = {})
                        Text(
                            text = "按计划交易",
                            //style = MaterialTheme.typography.body2.copy(color = Color.White),
                        )
                    }



                    if (stockTarget.isPlanActed) {
                        Divider()

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = if (stockTarget.isProfitable) "盈利" else "亏损",
                                //style = MaterialTheme.typography.body2.copy(color = Color.White),
                                modifier = Modifier.weight(2f)
                            )

                            Switch(
                                checked = stockTarget.isProfitable,
                                onCheckedChange = {},
                                enabled = false,
                                modifier = Modifier.weight(3f)
                            )

                            Text(
                                "盈亏比例:", modifier = Modifier.weight(2f)
                            )
                            OutlinedTextField(
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                value = stockTarget.profitOrLossPercentage.toString(),
                                enabled = false, readOnly = true,
                                onValueChange = { },
                                modifier = Modifier
                                    .padding(2.dp)
                                    .weight(3f),
                            )

                        }

                    }
                }

                if (stockTarget.actionReview.isNotEmpty()) {
                    Text(
                        //textAlign = TextAlign.Center,
                        text = "备注:",
                        style = MaterialTheme.typography.body1.copy(color = Color.White),
                    )
                    Divider()
                    Text(text = stockTarget.actionReview)
                }

            }

        }

    }

}