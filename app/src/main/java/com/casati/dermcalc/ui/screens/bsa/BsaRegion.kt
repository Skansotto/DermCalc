package com.casati.dermcalc.ui.screens.bsa

import androidx.annotation.StringRes
import com.casati.dermcalc.R

// Regola del nove (Wallace), con ogni distretto separato nella faccia anteriore e in
// quella posteriore: in visita si osserva un lato alla volta, e le lesioni raramente
// interessano entrambe le facce nella stessa misura.
//
// Testa e collo 9% (4,5 + 4,5) · tronco 36% (18 anteriore + 18 posteriore) ·
// ogni braccio 9% (4,5 + 4,5) · ogni gamba 18% (9 + 9) · area genitale 1%.
enum class BsaRegion(@param:StringRes val labelRes: Int, val percentuale: Double) {
    TESTA_COLLO_ANTERIORE(R.string.bsa_regione_testa_collo_anteriore, 4.5),
    TESTA_COLLO_POSTERIORE(R.string.bsa_regione_testa_collo_posteriore, 4.5),
    TRONCO_ANTERIORE(R.string.bsa_regione_tronco_anteriore, 18.0),
    TRONCO_POSTERIORE(R.string.bsa_regione_tronco_posteriore, 18.0),
    BRACCIO_DESTRO_ANTERIORE(R.string.bsa_regione_braccio_destro_anteriore, 4.5),
    BRACCIO_DESTRO_POSTERIORE(R.string.bsa_regione_braccio_destro_posteriore, 4.5),
    BRACCIO_SINISTRO_ANTERIORE(R.string.bsa_regione_braccio_sinistro_anteriore, 4.5),
    BRACCIO_SINISTRO_POSTERIORE(R.string.bsa_regione_braccio_sinistro_posteriore, 4.5),
    GAMBA_DESTRA_ANTERIORE(R.string.bsa_regione_gamba_destra_anteriore, 9.0),
    GAMBA_DESTRA_POSTERIORE(R.string.bsa_regione_gamba_destra_posteriore, 9.0),
    GAMBA_SINISTRA_ANTERIORE(R.string.bsa_regione_gamba_sinistra_anteriore, 9.0),
    GAMBA_SINISTRA_POSTERIORE(R.string.bsa_regione_gamba_sinistra_posteriore, 9.0),
    AREA_GENITALE(R.string.bsa_regione_area_genitale, 1.0)
}
