package com.casati.dermcalc.data.local

import android.content.Context

// Profilo del medico che usa l'app. L'account è locale: nessun dato lascia il dispositivo,
// serve solo a intestare i referti e a personalizzare la schermata iniziale.
data class ProfiloMedico(
    val nome: String,
    val cognome: String,
    val struttura: String
) {
    val nomeCompleto: String get() = "Dott. $nome $cognome"

    val iniziali: String
        get() = ((nome.firstOrNull() ?: 'D').toString() + (cognome.firstOrNull() ?: 'R')).uppercase()

    val strutturaVisibile: String
        get() = struttura.ifBlank { "Nessuna struttura indicata" }
}

class ProfiloManager(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NOME, Context.MODE_PRIVATE)

    fun isRegistrato(): Boolean = prefs.contains(CHIAVE_NOME)

    fun leggiProfilo(): ProfiloMedico? {
        val nome = prefs.getString(CHIAVE_NOME, null) ?: return null
        val cognome = prefs.getString(CHIAVE_COGNOME, null) ?: return null
        return ProfiloMedico(
            nome = nome,
            cognome = cognome,
            struttura = prefs.getString(CHIAVE_STRUTTURA, "").orEmpty()
        )
    }

    fun salvaProfilo(profilo: ProfiloMedico) {
        prefs.edit()
            .putString(CHIAVE_NOME, profilo.nome)
            .putString(CHIAVE_COGNOME, profilo.cognome)
            .putString(CHIAVE_STRUTTURA, profilo.struttura)
            .apply()
    }

    private companion object {
        const val PREFS_NOME = "dermcalc_profilo"
        const val CHIAVE_NOME = "nome"
        const val CHIAVE_COGNOME = "cognome"
        const val CHIAVE_STRUTTURA = "struttura"
    }
}
