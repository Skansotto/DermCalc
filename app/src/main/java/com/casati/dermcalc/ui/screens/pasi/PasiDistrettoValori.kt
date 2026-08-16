package com.casati.dermcalc.ui.screens.pasi

// Valori clinici di un distretto: Eritema, Indurimento, Desquamazione (0-4) e Area (0-6).
data class PasiDistrettoValori(
    val eritema: Int = 0,
    val indurimento: Int = 0,
    val desquamazione: Int = 0,
    val area: Int = 0
)
