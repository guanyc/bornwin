package com.guanyc.stock.discipline.presentation.stocksstatistics

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.guanyc.stock.discipline.R
import com.guanyc.stock.discipline.presentation.settings.SettingsBasicLinkItem
import com.guanyc.stock.discipline.presentation.util.Screen


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StockNoteStatisticsScreen(
    navController: NavHostController, viewModel: StockNoteStatisticsViewModel = hiltViewModel()
) {

    //未完成的target 在首页现实

    //完成的统计

    //某周
    //本周stocknotes几条, 有标的的交易日有几条，没有标的的交易日有几条
    //本周标的一共有多少， 多少标的给了动手机会，几个机会盈利了
    //本周是否有计划外交易
    //某月
    //统计日期从某日到某日


    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.statistics),
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
                )
            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
        )
    }) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {

            item {
                StockCodeInRecentStockNoteItem(R.string.stockcodes_in_recent_notes,
                    R.drawable.ic_calendar,
                    onClick = {
                        navController.navigate(Screen.RecentStockCodesInStockNotesScreen.route)
                    })
            }

            //FIXME TODO PRO  sort item function

            item {
                StockCodesByTargetReasonItem(R.string.stockcodes_by_target_reason,
                    R.drawable.target_reason_24px,
                    onClick = {
                        navController.navigate(Screen.StockCodesByTargetReasonScreen.route)
                    })
            }

            item {
                IsGivenOpportunityItem(R.string.target_reason_is_given_opportunity_statistics,
                    R.drawable.ic_check,
                    onClick = {
                        navController.navigate(Screen.StockTargetGivenOpportunityPercentScreen.route)
                    })
            }


            /*
            item {
                SettingsItemCard(cornerRadius = 16.dp, onClick = {
                    navController.navigate(Screen.ImportExportScreen.route)
                }) {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_import_export),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.import_data),
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.about),
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
                )
            }

            item {
                SettingsBasicLinkItem(
                    title = R.string.app_version,
                    icon = R.drawable.ic_code,
                    subtitle = BuildConfig.VERSION_NAME,
                    link = Constants.GITHUB_RELEASES_LINK
                )
            }
            item {
                SettingsBasicLinkItem(
                    title = R.string.project_on_github,
                    icon = R.drawable.ic_github,
                    link = Constants.PROJECT_GITHUB_LINK
                )
            }

            item {
                SettingsBasicLinkItem(
                    title = R.string.privacy_policy,
                    icon = R.drawable.ic_privacy,
                    link = Constants.PRIVACY_POLICY_LINK
                )
            }

            item {
                Text(
                    text = stringResource(R.string.product),
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
                )
            }

            item {
                SettingsBasicLinkItem(
                    title = R.string.request_feature_report_bug,
                    icon = R.drawable.ic_feature_issue,
                    link = Constants.GITHUB_ISSUES_LINK
                )
            }

            item {
                SettingsBasicLinkItem(
                    title = R.string.project_roadmap,
                    icon = R.drawable.ic_roadmap,
                    link = Constants.PROJECT_ROADMAP_LINK
                )
            }*/
            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
fun IsGivenOpportunityItem(title: Int, icon: Int, onClick: () -> Unit) {
    SettingsBasicLinkItem(title = title, icon = icon, onClick = {
        onClick()
    })
}

@Composable
fun StockCodesByTargetReasonItem(
    @StringRes title: Int, @DrawableRes icon: Int, onClick: () -> Unit
) {
    SettingsBasicLinkItem(title = title, icon = icon, onClick = {
        onClick()
    })
}


@Composable
fun StockCodeInRecentStockNoteItem(
    @StringRes title: Int, @DrawableRes icon: Int, onClick: () -> Unit = {}
) {
    SettingsBasicLinkItem(title = title, icon = icon, onClick = {
        onClick()
    })
}



