package com.guanyc.stock.discipline.presentation.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import java.util.*


fun showDatePicker(
    date: Calendar,
    context: Context,
    onDateSelected: (Long) -> Unit,
    onCancel: DialogInterface.OnCancelListener?,
    onDismiss: DialogInterface.OnDismissListener?
){
    val tempDate = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->

            tempDate[Calendar.YEAR] = year
            tempDate[Calendar.MONTH] = month
            tempDate[Calendar.DAY_OF_MONTH] = day

            onDateSelected(tempDate.timeInMillis)
        },

        date[Calendar.YEAR],
        date[Calendar.MONTH],
        date[Calendar.DAY_OF_MONTH]
    )

    if(onCancel!=null) {
        datePicker.setOnCancelListener(onCancel)
    }

    if(onDismiss!=null) {
        datePicker.setOnDismissListener(onDismiss)
    }

    datePicker.show()
}



fun showDatePickerAndTimePicker(
    date: Calendar,
    context: Context,
    onDateSelected: (Long) -> Unit
) {

    val tempDate = Calendar.getInstance()
    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            tempDate[Calendar.HOUR_OF_DAY] = hour
            tempDate[Calendar.MINUTE] = minute
            onDateSelected(tempDate.timeInMillis)
        }, date[Calendar.HOUR_OF_DAY], date[Calendar.MINUTE], false
    )
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            tempDate[Calendar.YEAR] = year
            tempDate[Calendar.MONTH] = month
            tempDate[Calendar.DAY_OF_MONTH] = day
            timePicker.show()
        },
        date[Calendar.YEAR],
        date[Calendar.MONTH],
        date[Calendar.DAY_OF_MONTH]
    )


    datePicker.show()
}