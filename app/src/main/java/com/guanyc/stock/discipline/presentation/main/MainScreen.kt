package com.guanyc.stock.discipline.presentation.main

import android.annotation.SuppressLint
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.guanyc.stock.discipline.presentation.main.components.MainBottomBar
import com.guanyc.stock.discipline.presentation.main.components.NavigationGraph
import com.guanyc.stock.discipline.presentation.util.BottomNavItem


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    startUpScreen: String,
    mainNavController: NavHostController
) {
    val navController = rememberNavController()
    val bottomNavItems =
        listOf(BottomNavItem.Action, BottomNavItem.Dashboard, BottomNavItem.Spaces, BottomNavItem.Settings)

    Scaffold(
        bottomBar = {
            MainBottomBar(navController = navController, items = bottomNavItems)
        }
    ) {
        NavigationGraph(
            navController = navController,
            mainNavController = mainNavController,
            startUpScreen = startUpScreen
        )
    }
}