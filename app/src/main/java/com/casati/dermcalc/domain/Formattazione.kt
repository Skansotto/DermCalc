package com.casati.dermcalc.domain

import com.casati.dermcalc.data.local.PazienteEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val MESI = listOf(
    "gennaio", "febbraio", "marzo", "aprile", "maggio", "giugno",
    "luglio", "agosto", "settembre", "ottobre", "novembre", "dicembre"
)

private val GIORNI = listOf(
    "Domenica", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato"
)

// "14 febbraio 1992": forma estesa usata dove c'è spazio.
fun formattaDataEstesa(millis: Long): String {
    val c = Calendar.getInstance().apply { timeInMillis = millis }
    return "${c.get(Calendar.DAY_OF_MONTH)} ${MESI[c.get(Calendar.MONTH)]} ${c.get(Calendar.YEAR)}"
}

// "14/02/1992": forma compatta, la stessa che si digita nel form.
fun formattaDataBreve(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date(millis))

// "31/08/26": etichetta compatta per gli assi del grafico e i metadati di riga.
// Il giorno serve: nella stessa giornata si possono registrare più misurazioni,
// e mese e anno da soli non basterebbero a distinguerle.
fun formattaDataCompatta(millis: Long): String =
    SimpleDateFormat("dd/MM/yy", Locale.ITALY).format(Date(millis))

fun formattaOggiEsteso(): String {
    val c = Calendar.getInstance()
    return "${GIORNI[c.get(Calendar.DAY_OF_WEEK) - 1]} ${c.get(Calendar.DAY_OF_MONTH)} " +
        "${MESI[c.get(Calendar.MONTH)]} ${c.get(Calendar.YEAR)}"
}

fun formattaDataFile(millis: Long): String =
    SimpleDateFormat("dd-MM-yyyy", Locale.ITALY).format(Date(millis))

fun calcolaEta(dataNascitaMillis: Long): Int {
    val nascita = Calendar.getInstance().apply { timeInMillis = dataNascitaMillis }
    val oggi = Calendar.getInstance()
    var eta = oggi.get(Calendar.YEAR) - nascita.get(Calendar.YEAR)
    if (oggi.get(Calendar.DAY_OF_YEAR) < nascita.get(Calendar.DAY_OF_YEAR)) {
        eta--
    }
    return eta
}

const val CIFRE_DATA = 8

// La data di nascita si conserva come sole cifre "ggmmaaaa": le barre sono
// decorazione applicata al momento di mostrarla, non fanno parte del valore.
fun soloCifreData(input: String): String = input.filter { it.isDigit() }.take(CIFRE_DATA)

// Converte "14021992" nel timestamp corrispondente, oppure null se la data non esiste.
fun dataDaCifre(cifre: String): Long? {
    if (cifre.length != CIFRE_DATA || !cifre.all { it.isDigit() }) return null
    val g = cifre.substring(0, 2).toInt()
    val m = cifre.substring(2, 4).toInt()
    val a = cifre.substring(4, 8).toInt()
    val annoCorrente = Calendar.getInstance().get(Calendar.YEAR)
    if (g !in 1..31 || m !in 1..12 || a !in 1900..annoCorrente) return null
    val calendario = Calendar.getInstance().apply {
        isLenient = false
        clear()
        set(a, m - 1, g)
    }
    return runCatching { calendario.timeInMillis }.getOrNull()
}

fun cifreDaData(millis: Long): String = formattaDataBreve(millis).filter { it.isDigit() }

// Le facce anteriore e posteriore valgono mezzo distretto: il mezzo punto va
// mostrato, ma solo quando c'è.
fun formattaPercentuale(valore: Double): String =
    if (valore % 1.0 == 0.0) "${valore.toInt()}%" else String.format("%.1f%%", valore)

val PazienteEntity.nomeCompleto: String get() = "$nome $cognome"

val PazienteEntity.iniziali: String
    get() = ((nome.firstOrNull() ?: ' ').toString() + (cognome.firstOrNull() ?: ' ')).trim().uppercase()
