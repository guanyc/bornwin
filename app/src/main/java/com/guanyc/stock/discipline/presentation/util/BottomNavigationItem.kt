package com.guanyc.stock.discipline.presentation.util

import com.guanyc.stock.discipline.R

sealed class BottomNavItem(val title: Int, val icon: Int, val iconSelected: Int, val route: String){

    object Action : BottomNavItem(R.string.action, R.drawable.ic_home, R.drawable.ic_home_filled, Screen.Action.route)
    object Dashboard : BottomNavItem(R.string.dashboard, R.drawable.ic_description, R.drawable.ic_description_filled, Screen.DashboardScreen.route)
    object Spaces : BottomNavItem(R.string.spaces, R.drawable.ic_spaces, R.drawable.ic_spaces_filled, Screen.SpacesScreen.route)
    object Settings: BottomNavItem(R.string.settings, R.drawable.ic_settings, R.drawable.ic_settings_filled, Screen.SettingsScreen.route)

}