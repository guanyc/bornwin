package com.guanyc.stock.discipline.presentation.stocks


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.app.getString
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.domain.model.StockTarget
import com.guanyc.stock.discipline.domain.model.TargetConstants
import com.guanyc.stock.discipline.domain.model.tabOtherList
import com.guanyc.stock.discipline.domain.model.tabReasons
import com.guanyc.stock.discipline.domain.model.tabSpecialList
import com.guanyc.stock.discipline.domain.model.tabWatchList
import com.guanyc.stock.discipline.domain.model.targetActionList
import com.guanyc.stock.discipline.domain.model.targetOtherListList
import com.guanyc.stock.discipline.domain.model.targetReasonList
import com.guanyc.stock.discipline.domain.model.targetSpecialListList
import com.guanyc.stock.discipline.domain.model.targetWatchListList
import com.guanyc.stock.discipline.presentation.main.components.TabEntity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TargetCardDialogEdit(
    targetConstants: TargetConstants,
    stockNote: StockNote,
    stockTarget: StockTarget,
    openDialogEditTargetValue: MutableState<Boolean>,
    onUpdateStockTarget: (StockNote, StockTarget) -> Unit
) {

    Log.d("stocklist", "DialogEdit")

    var item = stockTarget

    var code = rememberSaveable { mutableStateOf(item.code) }
    var name = rememberSaveable { mutableStateOf(item.name) }

    var targetReasonList = remember { mutableStateListOf<TabEntity>() }
    var targetActionList = remember { mutableStateListOf<TabEntity>() }
    var targetOtherList = remember { mutableStateListOf<TabEntity>() }

    if (stockTarget.targetReasonList.isNotEmpty()) {
        targetReasonList.clear()
        targetReasonList.addAll(stockTarget.targetReasonList)
    }

    if (stockTarget.targetActionList.isNotEmpty()) {
        targetActionList.clear()
        targetActionList.addAll(stockTarget.targetActionList)
    }

    if ((stockTarget.targetWatchListList + stockTarget.targetSpecialListList).isNotEmpty()) {
        targetOtherList.clear()
        targetOtherList.addAll(stockTarget.targetOtherListList)
    }

    var isOpportunityGiven by remember { mutableStateOf(stockTarget.isOpportunityGiven) }
    var isPlanActed by remember { mutableStateOf(stockTarget.isPlanActed) }
    var isProfitable by remember { mutableStateOf(stockTarget.isProfitable) }
    var profitOrLossPercentage by remember { mutableStateOf(stockTarget.profitOrLossPercentage) }
    var actionReview by remember { mutableStateOf(stockTarget.actionReview) }
    var isCompleted by remember { mutableStateOf(stockTarget.isCompleted) }


    AlertDialog(
        //shape = RoundedCornerShape(25.dp),
        onDismissRequest = { openDialogEditTargetValue.value = false },
        title = { Text(stringResource(R.string.title_update_target)) },
        text = {
            Column {

                Row() {
                    StockNoteCheckBox(
                        isComplete = isCompleted, borderColor = Color.Black
                    ) {
                        isCompleted = !isCompleted
                    }

                    //Text(   getString(R.string.stock_code), modifier = Modifier.weight(2f))

                    OutlinedTextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        value = code.value,
                        label = { Text(stringResource(id = R.string.stock_code)) },
                        onValueChange = { code.value = it },
                        modifier = Modifier.weight(4f),
                    )
                    //Text( "名称", modifier = Modifier.weight(2f) )

                    OutlinedTextField(
                        label = { Text(stringResource(id = R.string.stock_name)) },
                        value = name.value,
                        onValueChange = {
                            name.value = it
                        },
                        modifier = Modifier.weight(4f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                }

                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = 3,
                )
                {
                    Text(
                        stringResource(R.string.reasons),
                        //modifier = Modifier.padding(2.dp),
                        //style = MaterialTheme.typography.body1
                    )

                    if (targetConstants != null) {
                        targetConstants.tabReasons.forEachIndexed { index, targetReason ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = targetReasonList.contains(targetReason),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            targetReasonList.add(targetReason)
                                        } else {
                                            targetReasonList.remove(targetReason)
                                        }
                                    })
                                Text(targetReason.title)
                            }
                        }
                    }
                }
                //flowrow


                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = 3,
                ) {
                    Text(
                        stringResource(id = R.string.actions),
                    )

                    if (targetConstants != null) {
                        (targetConstants.tabWatchList + targetConstants.tabSpecialList).forEachIndexed { index, targetAction ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = targetActionList.contains(targetAction),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            targetActionList.add(targetAction)
                                        } else {
                                            targetActionList.remove(targetAction)
                                        }
                                    })
                                Text(targetAction.title)
                            }
                        }
                    }


                }//flowrow


                //other tabentities
                FlowRow(
                    modifier = Modifier.padding(2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = 3,
                ) {
                    Text(
                        stringResource(id = R.string.others),
                    )

                    if (targetConstants != null) {
                        targetConstants.tabOtherList.forEachIndexed { index, targetOther ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = targetOtherList.contains(targetOther),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            targetOtherList.add(targetOther)
                                        } else {
                                            targetOtherList.remove(targetOther)
                                        }
                                    })
                                Text(targetOther.title)
                            }
                        }
                    }
                }


                Divider(thickness = 1.dp)
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
                        Checkbox(checked = isOpportunityGiven, onCheckedChange = { checked ->
                            isOpportunityGiven = checked
                        })
                        Text(
                            text = stringResource(id = R.string.string_given_opportunity),
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
                            text = stringResource(id = R.string.string_trading_according_to_plan),
                            //style = MaterialTheme.typography.body2.copy(color = Color.White),
                        )
                    }

                    AnimatedVisibility(visible = isPlanActed) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = if (isProfitable) getString(R.string.string_made_profit)
                                else getString(R.string.string_made_loss),
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
                        }
                    }


                    AnimatedVisibility(visible = isPlanActed) {


                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(id = R.string.profit_loss_ratio),
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
                    /*
                    Text(
                        stringResource(id = R.string.string_action_review), modifier = Modifier
                            // .padding(2.dp)
                            .weight(2f)
                    )*/

                    OutlinedTextField(
                        value = actionReview,
                        onValueChange = {
                            actionReview = it
                        },
                        label = { Text(stringResource(id = R.string.comment_on_note)) },
                        modifier = Modifier
                            // .padding(2.dp)
                            .weight(6f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                }


            }
        },

        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(),
                shape = RoundedCornerShape(6.dp),
                onClick = {


                    var update = StockTarget(
                        stockNoteId = stockTarget.stockNoteId,
                        code = code.value,
                        name = name.value,
                        //targetReasonList = targetReasonList,
                        //targetActionList = targetActionList,
                        //FIXME 暂时先这样
                        tabs = targetReasonList.plus(targetActionList).plus(targetOtherList),
                        stockTargetId = item.stockTargetId,
                        createDate = item.createDate,
                        isCompleted = isCompleted,
                        isOpportunityGiven = isOpportunityGiven,
                        isPlanActed = isPlanActed,
                        isProfitable = isProfitable,
                        actionReview = actionReview,
                        profitOrLossPercentage = profitOrLossPercentage,
                    )

                    //TODO update the stocktarget
                    Log.d("stocktarget", update.toString())

                    openDialogEditTargetValue.value = false

                    onUpdateStockTarget(stockNote, update)


                }) {
                Text(stringResource(R.string.update), color = Color.White)
            }

        },
        dismissButton = {
            Button(shape = RoundedCornerShape(6.dp), onClick = {
                openDialogEditTargetValue.value = false
            }) {
                Text(stringResource(R.string.cancel), color = Color.White)
            }

        })
}