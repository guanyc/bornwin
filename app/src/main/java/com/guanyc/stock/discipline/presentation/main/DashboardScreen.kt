package com.guanyc.stock.discipline.presentation.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.domain.model.StockNote
import com.guanyc.stock.discipline.presentation.stocks.StockNoteListEvent
import com.guanyc.stock.discipline.presentation.util.Screen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// 概略图 概要

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    navController: NavHostController, viewModel: MainViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()

    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.dashboard),
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                )
            }, backgroundColor = MaterialTheme.colors.background, elevation = 0.dp
        )
    }) {

        //使用launchEffect在compose中 调用可中断 collectDashboardData
        LaunchedEffect(true) { viewModel.onEvent(DashboardEvent.InitAll) }

        LaunchedEffect(viewModel.uiState) {
            Log.d("LaunchedEffect", "notesUiState")
            if (viewModel.uiState.error != null) {
                scaffoldState.snackbarHostState.showSnackbar(viewModel.uiState.error!!)
                viewModel.onEvent(DashboardEvent.ErrorDisplayed)
            }
        }

        LazyColumn {
            item {
                StockDashBoardWidget(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f),
                    navController = navController,
                    unCompleted = viewModel.uiState.unCompleted,
                    completed = viewModel.uiState.completed,
                    onclick = {
                        navController.navigate(
                            Screen.StockNoteListScreen.route
                        )
                    },

                    onAddStockNoteClicked = {
                        scope.launch {

                            val tempDate = Calendar.getInstance()
                            val date = Date(tempDate.timeInMillis)
                            val df = SimpleDateFormat("yyyyMMdd")

                            var locale = Locale.getDefault()

                            val createDate = df.format(date)

                            val stockNote = StockNote(createDate = createDate)


                            viewModel.onEvent(DashboardEvent.insertStockNote(stockNote))

                            // scaffoldState.snackbarHostState.showSnackbar(uiState.error!!)
                            //viewModel.onEvent(DashboardEvent.ErrorDisplayed)

                        }
                    }

                )


            }
        }
    }
}

