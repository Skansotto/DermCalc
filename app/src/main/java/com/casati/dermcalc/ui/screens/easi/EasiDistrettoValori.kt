package com.casati.dermcalc.ui.screens.easi

// Valori clinici di un distretto: Eritema, Edema/Papulazione, Escoriazioni,
// Lichenificazione (0-3) e Area (0-6).
data class EasiDistrettoValori(
    val eritema: Int = 0,
    val edemaPapulazione: Int = 0,
    val escoriazioni: Int = 0,
    val lichenificazione: Int = 0,
    val area: Int = 0
)
