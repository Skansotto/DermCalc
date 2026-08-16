package com.casati.dermcalc.ui.screens.pasi

// Distretti corporei del PASI, ciascuno con il proprio peso nella formula finale.
enum class PasiDistretto(val label: String, val peso: Double) {
    TESTA("Testa", 0.1),
    ARTI_SUPERIORI("Arti superiori", 0.2),
    TRONCO("Tronco", 0.3),
    ARTI_INFERIORI("Arti inferiori", 0.4)
}
