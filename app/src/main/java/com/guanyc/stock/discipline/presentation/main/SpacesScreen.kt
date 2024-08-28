package com.guanyc.stock.discipline.presentation.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.presentation.main.components.SpaceRegularCard
import com.guanyc.stock.discipline.presentation.util.Screen
import com.guanyc.stock.discipline.theme.Green
import com.guanyc.stock.discipline.theme.Orange
import com.guanyc.stock.discipline.theme.Purple
import com.guanyc.stock.discipline.theme.Red


// SpaceRegularCard Screen 空间卡片

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SpacesScreen(
    navController: NavHostController,
    viewModel: SpaceScreenModel = hiltViewModel(),
) {

    val uiState = viewModel.uiState

    //var isTargetConstantsInitialized by remember { mutableStateOf(false) }
    //var targetConstants by remember { mutableStateOf(TargetConstants()) }

    //LaunchedEffect(uiState) {
    //isTargetConstantsInitialized = uiState.isTargetConstantsInitialized
    //targetConstants = uiState.targetConstants
    //}

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.spaces),
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                )
            }, backgroundColor = MaterialTheme.colors.background, elevation = 0.dp
        )
    }) {
        LazyColumn {

            item {
                Row {

                    //val istc = uiState.isTargetConstantsInitialized

                    SpaceRegularCard(
                        modifier = Modifier.weight(1f, fill = true),

                        title = stringResource(R.string.stockplan),

                        image = R.drawable.stock_img2_foreground, backgroundColor = Red

                    ) {

                        navController.navigate(Screen.StockNoteListScreen.route)

                    }

                    SpaceRegularCard(

                        modifier = Modifier.weight(1f, fill = true),

                        title = stringResource(R.string.targetConstantsInitialization),

                        image = R.drawable.ic_code, backgroundColor = Green
                    ) {
                        navController.navigate(Screen.TargetMetaScreen.route)
                    }


                }
            }
            item {
                Row {

                    SpaceRegularCard(
                        modifier = Modifier.weight(1f, fill = true),
                        title = stringResource(R.string.statistics),
                        image = R.drawable.ic_roadmap,
                        backgroundColor = Orange
                    ) {
                        navController.navigate(Screen.StockNoteStatisticsScreen.route)
                    }

                    SpaceRegularCard(
                        modifier = Modifier.weight(1f, fill = true),
                        title = stringResource(R.string.settings),
                        image = R.drawable.ic_settings,
                        backgroundColor = Purple
                    ) {
                        navController.navigate(Screen.SettingsScreen.route)
                    }


                }
            }

            //item { Spacer(Modifier.height(60.dp)) }
        }
    }
}

