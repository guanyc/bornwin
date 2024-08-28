package com.guanyc.stock.discipline.presentation.main

import android.os.Bundle
import android.view.WindowManager.LayoutParams
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.guanyc.stock.discipline.presentation.main.components.TabEntityEditorScreen
import com.guanyc.stock.discipline.presentation.stocks.StockNoteListItemDetailScreen

import com.guanyc.stock.discipline.presentation.stocks.StockNoteListScreen
import com.guanyc.stock.discipline.presentation.stocksstatistics.RecentStockCodesInStockNotesScreen
import com.guanyc.stock.discipline.presentation.stocksstatistics.StockTargetsGroupByTargetReasonScreen
import com.guanyc.stock.discipline.presentation.stocksstatistics.StockNoteStatisticsScreen
import com.guanyc.stock.discipline.presentation.stocksstatistics.StockTargetGivenOpportunityPercentScreen

import com.guanyc.stock.discipline.presentation.util.Screen
import com.guanyc.stock.discipline.theme.MyBrainTheme
import com.guanyc.stock.discipline.theme.Rubik
import com.guanyc.stock.discipline.util.Constants
import com.guanyc.stock.discipline.util.settings.StartUpScreenSettings
import com.guanyc.stock.discipline.util.settings.ThemeSettings
import com.guanyc.stock.discipline.util.settings.toFontFamily
import com.guanyc.stock.discipline.util.settings.toInt
import com.guanyc.stock.discipline.presentation.targetconsts.TargetMetaScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Suppress("BlockingMethodInNonBlockingContext")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // 获得主题theme auto的值
            val themeMode = viewModel.themeMode.collectAsState(initial = ThemeSettings.AUTO.value)
            //获得字体大小
            val font = viewModel.font.collectAsState(initial = Rubik.toInt())
            //获得屏幕block设置
            val blockScreenshots = viewModel.blockScreenshots.collectAsState(initial = false)

            //记录初始启动洁面的设置
            var startUpScreenSettings by remember { mutableStateOf(StartUpScreenSettings.SPACES.value) }

            //记录系统控制器的状态
            val systemUiController = rememberSystemUiController()


            LaunchedEffect(true) {
                runBlocking {
                    startUpScreenSettings = viewModel.defaultStartUpScreen.first()
                }
            }
            LaunchedEffect(blockScreenshots.value) {
                if (blockScreenshots.value) {
                    window.setFlags(
                        LayoutParams.FLAG_SECURE,
                        LayoutParams.FLAG_SECURE
                    )
                } else
                    window.clearFlags(LayoutParams.FLAG_SECURE)
            }

            //是否夜晚模式
            val isDarkMode = when (themeMode.value) {
                ThemeSettings.DARK.value -> true
                ThemeSettings.LIGHT.value -> false
                else -> isSystemInDarkTheme()
            }

            //设置系统bar颜色
            SideEffect {
                systemUiController.setSystemBarsColor(
                    if (isDarkMode) Color.Black else Color.White,
                    darkIcons = !isDarkMode
                )
            }

            MyBrainTheme(darkTheme = isDarkMode, fontFamily = font.value.toFontFamily()) {

                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //启动screen
                    val startUpScreen =
                        //if (startUpScreenSettings == StartUpScreenSettings.SPACES.value)
                            Screen.Action.route
                    //else Screen.DashboardScreen.route

                    BuildNavController(navController, startUpScreen)


                }
            }
        }
    }

    @Composable
    fun BuildNavController(
        navController: NavHostController,
        startUpScreen: String
    ) {
        androidx.navigation.compose.NavHost(
            startDestination = Screen.Main.route,
            navController = navController
        ) {
            //启动screen MainScreen
            composable(Screen.Main.route) {
                MainScreen(
                    startUpScreen = startUpScreen,
                    mainNavController = navController
                )
            }



            /*
            composable(
                Screen.TasksScreen.route,
                arguments = listOf(navArgument(Constants.ADD_TASK_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                }),
                deepLinks =
                listOf(
                    navDeepLink {
                        uriPattern =
                            "${Constants.TASKS_SCREEN_URI}/{${Constants.ADD_TASK_ARG}}"
                    }
                )
            ) {
                TasksScreen(
                    navController = navController,
                    addTask = it.arguments?.getBoolean(Constants.ADD_TASK_ARG)
                        ?: false
                )
            }*/

            /*
            composable(
                Screen.TaskDetailScreen.route,
                arguments = listOf(navArgument(Constants.TASK_ID_ARG) {
                    type = NavType.IntType
                }),
                deepLinks =
                listOf(
                    navDeepLink {
                        uriPattern =
                            "${Constants.TASK_DETAILS_URI}/{${Constants.TASK_ID_ARG}}"
                    }
                )
            ) {
                TaskDetailScreen(
                    navController = navController,
                    it.arguments?.getInt(Constants.TASK_ID_ARG)!!
                )
            }*/



            composable(Screen.SettingsScreen.route){
                SettingsScreen(navController = navController)
            }


            //notes


//            composable(
//                Screen.NoteDetailsScreen.route,
//                arguments = listOf(navArgument(Constants.NOTE_ID_ARG) {
//                    type = NavType.IntType
//                },
//                    navArgument(Constants.FOLDER_ID) {
//                        type = NavType.IntType
//                    }
//                ),
//            ) {
//                NoteDetailsScreen(
//                    navController,
//                    it.arguments?.getInt(Constants.NOTE_ID_ARG) ?: -1,
//                    it.arguments?.getInt(Constants.FOLDER_ID) ?: -1
//                )
//            }

            /*
                composable(Screen.StockNoteDetailPage.route,
                    arguments = listOf(
                        navArgument(Constants.STOCK_DAILY_NOTE_ID_ARG)
                        {
                            type = NavType.LongType
                        },
                    ),
                ){
                    StockNoteDetailPage(
                        navController = navController,
                        stockNoteId = it.arguments?.getLong(Constants.STOCK_DAILY_NOTE_ID_ARG)?: -1
                    )

                }*/

            //StockDailyNoteDetailScreen
            composable(
                Screen.StockDailyNoteDetailScreen.route,
                arguments = listOf(
                    navArgument(Constants.STOCK_DAILY_NOTE_ID_ARG)
                    {
                        type = NavType.LongType
                    },
                ),
            ) {

                StockNoteListItemDetailScreen(
                    navController = navController,
                    stockNoteId = it.arguments?.getLong(Constants.STOCK_DAILY_NOTE_ID_ARG)?: -1
                )
            }


            /*
            composable(
                Screen.DiaryDetailScreen.route,
                arguments = listOf(navArgument(Constants.DIARY_ID_ARG) {
                    type = NavType.IntType
                })
            ) {
                DiaryEntryDetailsScreen(
                    navController = navController,
                    it.arguments?.getInt(Constants.DIARY_ID_ARG)!!
                )
            }*/



            //StockDailyNoteSearchScreen
            //composable(Screen.StockDailyNoteSearchScreen.route) {
            //  StockNoteSearchScreen(navController = navController)
            //}


            //navController.navigate(Screen.StockNoteListScreen.route)
            composable(Screen.StockNoteListScreen.route) {
                StockNoteListScreen(navController = navController)
            }

            //navController.navigate(Screen.StockNoteListScreen.route)
            /*composable(Screen.TargetReasonListScreen.route) {
                    TargetReasonListScreen(navController = navController)
                }*/

            composable(Screen.TargetMetaScreen.route) {
                TargetMetaScreen(navController = navController)
            }

            composable(Screen.RecentStockCodesInStockNotesScreen.route){
                RecentStockCodesInStockNotesScreen(navController = navController)
            }

            composable(Screen.StockCodesByTargetReasonScreen.route){
                StockTargetsGroupByTargetReasonScreen(navController = navController)
            }

            composable(Screen.StockTargetGivenOpportunityPercentScreen.route){
                StockTargetGivenOpportunityPercentScreen(navController = navController)
            }


            composable(Screen.TabEntityEditorScreen.route){
                TabEntityEditorScreen(navController = navController)
            }



            composable(Screen.StockNoteStatisticsScreen.route) {
                StockNoteStatisticsScreen(navController = navController)
            }


            /*
            composable(
                Screen.BookmarkDetailScreen.route,
                arguments = listOf(navArgument(Constants.BOOKMARK_ID_ARG) {
                    type = NavType.IntType
                })
            ) {
                BookmarkDetailsScreen(
                    navController = navController,
                    it.arguments?.getInt(Constants.BOOKMARK_ID_ARG)!!
                )
            }*/




            /*
            composable(
                Screen.CalendarScreen.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = Constants.CALENDAR_SCREEN_URI
                    }
                )
            ) {
                CalendarScreen(navController = navController)
            }*/

            /*
            composable(
                Screen.CalendarEventDetailsScreen.route,
                arguments = listOf(navArgument(Constants.CALENDAR_EVENT_ARG) {
                    type = NavType.StringType
                }),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =
                            "${Constants.CALENDAR_DETAILS_SCREEN_URI}/{${Constants.CALENDAR_EVENT_ARG}}"
                    }
                )
            ) {
                CalendarEventDetailsScreen(
                    navController = navController,
                    eventJson = it.arguments?.getString(Constants.CALENDAR_EVENT_ARG) ?: ""
                )
            }*/

            /*
            composable(
                Screen.NoteFolderDetailsScreen.route,
                arguments = listOf(navArgument(Constants.FOLDER_ID) {
                    type = NavType.IntType
                })
            ) {
                NoteFolderDetailsScreen(
                    navController = navController,
                    it.arguments?.getInt(Constants.FOLDER_ID) ?: -1
                )
            }*/


        }
    }

}