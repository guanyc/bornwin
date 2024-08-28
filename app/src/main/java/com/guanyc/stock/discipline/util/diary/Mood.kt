package com.guanyc.stock.discipline.util.diary

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.guanyc.stock.discipline.theme.Blue
import com.guanyc.stock.discipline.theme.Green
import com.guanyc.stock.discipline.theme.Orange
import com.guanyc.stock.discipline.theme.Purple
import com.guanyc.stock.discipline.R

enum class Mood(@DrawableRes val icon: Int, val color: Color, @StringRes val title: Int, val value: Int) {
    AWESOME(R.drawable.ic_very_happy, Green, R.string.awesome, 5),
    GOOD(R.drawable.ic_happy, Blue, R.string.good, 4),
    OKAY(R.drawable.ic_ok_face, Purple, R.string.okay, 3),
    BAD(R.drawable.ic_sad, Orange, R.string.bad, 2),
    TERRIBLE(R.drawable.ic_very_sad, Color.Red, R.string.terrible, 1)
}