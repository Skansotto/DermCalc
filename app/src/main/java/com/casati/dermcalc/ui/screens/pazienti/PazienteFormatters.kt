package com.casati.dermcalc.ui.screens.pazienti

import java.text.DateFormat
import java.util.Date
import java.util.Locale

internal fun formattaData(millis: Long): String {
    val formato = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    return formato.format(Date(millis))
}
