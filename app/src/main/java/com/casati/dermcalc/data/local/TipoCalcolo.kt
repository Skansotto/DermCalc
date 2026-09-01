package com.casati.dermcalc.data.local

enum class TipoCalcolo {
    BMI, BSA, PASI, EASI;

    companion object {
        // Ordine di presentazione: prima gli indici dermatologici specifici, poi le
        // misure generali. L'ordine della enum non si tocca perché i nomi sono
        // già salvati nel database.
        val ORDINE_CLINICO = listOf(PASI, EASI, BMI, BSA)
    }
}
