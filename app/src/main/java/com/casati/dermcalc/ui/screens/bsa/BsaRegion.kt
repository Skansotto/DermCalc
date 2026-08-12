package com.casati.dermcalc.ui.screens.bsa

// Regola del nove (Wallace): suddivisione della superficie corporea in regioni,
// ciascuna con la propria percentuale, per un totale di 100%.
enum class BsaRegion(val label: String, val percentuale: Int) {
    TESTA_COLLO("Testa e collo", 9),
    ARTO_SUPERIORE_DESTRO("Arto superiore destro", 9),
    ARTO_SUPERIORE_SINISTRO("Arto superiore sinistro", 9),
    TRONCO_ANTERIORE("Tronco anteriore", 18),
    TRONCO_POSTERIORE("Tronco posteriore", 18),
    ARTO_INFERIORE_DESTRO("Arto inferiore destro", 18),
    ARTO_INFERIORE_SINISTRO("Arto inferiore sinistro", 18),
    GENITALI("Genitali", 1)
}
