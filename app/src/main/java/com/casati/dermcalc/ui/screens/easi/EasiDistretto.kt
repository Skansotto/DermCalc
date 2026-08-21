package com.casati.dermcalc.ui.screens.easi

// Distretti corporei dell'EASI, con peso identico al PASI.
enum class EasiDistretto(val label: String, val peso: Double) {
    TESTA_COLLO("Testa e collo", 0.1),
    ARTI_SUPERIORI("Arti superiori", 0.2),
    TRONCO("Tronco", 0.3),
    ARTI_INFERIORI("Arti inferiori", 0.4)
}
