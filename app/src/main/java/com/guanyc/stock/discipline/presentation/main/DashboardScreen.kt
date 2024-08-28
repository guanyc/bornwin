package com.guanyc.stock.discipline.presentation.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.guanyc.stock.discipline.R

import com.guanyc.stock.discipline.presentation.util.Screen

// 概略图 概要

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    navController: NavHostController, viewModel: MainViewModel = hiltViewModel()
) {
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
        LaunchedEffect(true) { viewModel.onDashboardEvent(DashboardEvent.InitAll) }


        LazyColumn {

            item {
                StockDashBoardWidget(modifier = Modifier.fillMaxWidth().aspectRatio(1.5f),
                    navController = navController,
                    unCompleted = viewModel.uiState.unCompleted,
                    completed =  viewModel.uiState.completed,
                    onclick = {
                        navController.navigate(
                            Screen.StockNoteListScreen.route
                        )
                    })
            }


            item { Spacer(Modifier.height(65.dp)) }
        }
    }
}

