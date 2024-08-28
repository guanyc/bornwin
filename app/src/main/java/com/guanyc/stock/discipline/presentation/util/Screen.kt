package com.guanyc.stock.discipline.presentation.util

import com.guanyc.stock.discipline.util.Constants

sealed class Screen(val route: String) {
    object StockTargetAddScreen :Screen("stock_target_add_screen")

    object StockTargetDetailScreen :Screen("stock_target_detail_screen")
    object StockTargetEditScreen :Screen("stock_target_edit_screen")
    object StockTargetListScreen :Screen("stock_target_list_screen")


    object TabEntityEditorScreen:Screen("tab_entity_editor_list")
    object StockTargetGivenOpportunityPercentScreen: Screen("stock_target_by_is_given_opportunity")
    object  StockCodesByTargetReasonScreen: Screen("stock_codes_by_target_reason")
    object RecentStockCodesInStockNotesScreen : Screen("recent_stock_codes")
    object TargetMetaScreen: Screen("target_meta")


    object Action :Screen( "action_screen")

    object Main : Screen("main_screen")
    object SpacesScreen : Screen("spaces_screen")
    object DashboardScreen : Screen("dashboard_screen")
    object SettingsScreen : Screen("settings_screen")
    //object TasksScreen : Screen("tasks_screen?${Constants.ADD_TASK_ARG}={${Constants.ADD_TASK_ARG}}")
    //object TaskSearchScreen : Screen("task_search_screen")
    //object NotesScreen : Screen("notes_screen")
    //object NoteDetailsScreen : Screen("note_detail_screen/{${Constants.NOTE_ID_ARG}}?${Constants.FOLDER_ID}={${Constants.FOLDER_ID}}")


    //object NoteSearchScreen : Screen("note_search_screen")
    //object DiaryScreen : Screen("diary_screen")
    //object DiaryDetailScreen : Screen("diary_detail_screen/{${Constants.DIARY_ID_ARG}}")
    //object DiarySearchScreen : Screen("diary_search_screen")
    //object DiaryChartScreen : Screen("diary_chart_screen")
    //object BookmarksScreen : Screen("bookmarks_screen")
    //object BookmarkDetailScreen : Screen("bookmark_detail_screen/{${Constants.BOOKMARK_ID_ARG}}")
    //object BookmarkSearchScreen : Screen("bookmark_search_screen")
    //object CalendarScreen : Screen("calendar_screen")


    //object CalendarEventDetailsScreen : Screen("calendar_event_details_screen/{${Constants.CALENDAR_EVENT_ARG}}")
    //object NoteFolderDetailsScreen : Screen("note_folder_details_screen/{${Constants.FOLDER_ID}}")
    //object ImportExportScreen : Screen("import_export_screen")

    object StockNoteListScreen : Screen("stock_note_list")
    object StockNoteStatisticsScreen : Screen("stock_note_statistics")

    object StockDailyNoteSearchScreen : Screen("stock_note_search")

    object StockDailyNoteDetailScreen : Screen("stock_note_detail/{${Constants.STOCK_DAILY_NOTE_ID_ARG}}")

    object StockNoteDetailPage : Screen("note_detail_page/{${Constants.STOCK_DAILY_NOTE_ID_ARG}}")


    //object TaskDetailScreen : Screen("task_detail_screen/{${Constants.TASK_ID_ARG}}")
    object TargetReasonListScreen : Screen("target_reason_List_screen")



    //    object TaskDetailScreen : Screen("task_detail_screen/{${Constants.TASK_ID_ARG}}")
}