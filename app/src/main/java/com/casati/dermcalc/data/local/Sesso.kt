package com.casati.dermcalc.data.local

import androidx.annotation.StringRes
import com.casati.dermcalc.R

enum class Sesso(@param:StringRes val labelRes: Int, val sigla: String) {
    FEMMINA(R.string.pazienti_sesso_femmina, "F"),
    MASCHIO(R.string.pazienti_sesso_maschio, "M"),
    ALTRO(R.string.pazienti_sesso_altro, "Altro")
}
