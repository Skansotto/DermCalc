package com.casati.dermcalc.ui.screens.bsa

import androidx.annotation.StringRes
import com.casati.dermcalc.R

// Regola del nove (Wallace): suddivisione della superficie corporea in regioni,
// ciascuna con la propria percentuale, per un totale di 100%.
enum class BsaRegion(@param:StringRes val labelRes: Int, val percentuale: Int) {
    TESTA_COLLO(R.string.bsa_regione_testa_collo, 9),
    ARTO_SUPERIORE_DESTRO(R.string.bsa_regione_arto_superiore_destro, 9),
    ARTO_SUPERIORE_SINISTRO(R.string.bsa_regione_arto_superiore_sinistro, 9),
    TRONCO_ANTERIORE(R.string.bsa_regione_tronco_anteriore, 18),
    TRONCO_POSTERIORE(R.string.bsa_regione_tronco_posteriore, 18),
    ARTO_INFERIORE_DESTRO(R.string.bsa_regione_arto_inferiore_destro, 18),
    ARTO_INFERIORE_SINISTRO(R.string.bsa_regione_arto_inferiore_sinistro, 18),
    GENITALI(R.string.bsa_regione_genitali, 1)
}
