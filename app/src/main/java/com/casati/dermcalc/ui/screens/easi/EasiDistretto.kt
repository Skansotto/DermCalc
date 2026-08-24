package com.casati.dermcalc.ui.screens.easi

import androidx.annotation.StringRes
import com.casati.dermcalc.R

// Distretti corporei dell'EASI, con peso identico al PASI.
enum class EasiDistretto(@param:StringRes val labelRes: Int, val peso: Double) {
    TESTA_COLLO(R.string.distretto_testa_collo, 0.1),
    ARTI_SUPERIORI(R.string.distretto_arti_superiori, 0.2),
    TRONCO(R.string.distretto_tronco, 0.3),
    ARTI_INFERIORI(R.string.distretto_arti_inferiori, 0.4)
}
