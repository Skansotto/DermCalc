package com.casati.dermcalc.ui.screens.pazienti

import com.casati.dermcalc.data.local.PazienteEntity
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal fun formattaData(millis: Long): String {
    val formato = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    return formato.format(Date(millis))
}

internal val PazienteEntity.nomeCompleto: String
    get() = "$nome $cognome"

internal fun calcolaEta(dataNascitaMillis: Long): Int {
    val nascita = Calendar.getInstance().apply { timeInMillis = dataNascitaMillis }
    val oggi = Calendar.getInstance()
    var eta = oggi.get(Calendar.YEAR) - nascita.get(Calendar.YEAR)
    if (oggi.get(Calendar.DAY_OF_YEAR) < nascita.get(Calendar.DAY_OF_YEAR)) {
        eta--
    }
    return eta
}
