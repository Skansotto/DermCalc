package com.casati.dermcalc.ui.screens.pasi

import androidx.annotation.StringRes
import com.casati.dermcalc.R

// Distretti corporei del PASI, ciascuno con il proprio peso nella formula finale.
enum class PasiDistretto(@param:StringRes val labelRes: Int, val peso: Double) {
    TESTA(R.string.distretto_testa, 0.1),
    ARTI_SUPERIORI(R.string.distretto_arti_superiori, 0.2),
    TRONCO(R.string.distretto_tronco, 0.3),
    ARTI_INFERIORI(R.string.distretto_arti_inferiori, 0.4)
}
