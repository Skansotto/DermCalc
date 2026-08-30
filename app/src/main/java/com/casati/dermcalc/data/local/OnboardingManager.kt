package com.casati.dermcalc.data.local

import android.content.Context

// Ricorda se l'utente ha già visto la guida introduttiva, per mostrarla una sola volta.
class OnboardingManager(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NOME, Context.MODE_PRIVATE)

    fun isCompletato(): Boolean = prefs.getBoolean(CHIAVE_COMPLETATO, false)

    fun segnaCompletato() {
        prefs.edit().putBoolean(CHIAVE_COMPLETATO, true).apply()
    }

    private companion object {
        const val PREFS_NOME = "dermcalc_onboarding"
        const val CHIAVE_COMPLETATO = "completato"
    }
}
