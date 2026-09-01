package com.casati.dermcalc.data.local

import android.content.Context

// Preferenze dell'app modificabili dalla schermata Impostazioni.
class ImpostazioniManager(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NOME, Context.MODE_PRIVATE)

    fun isBiometriaAttiva(): Boolean = prefs.getBoolean(CHIAVE_BIOMETRIA, true)

    fun impostaBiometria(attiva: Boolean) {
        prefs.edit().putBoolean(CHIAVE_BIOMETRIA, attiva).apply()
    }

    private companion object {
        const val PREFS_NOME = "dermcalc_impostazioni"
        const val CHIAVE_BIOMETRIA = "biometria_attiva"
    }
}
