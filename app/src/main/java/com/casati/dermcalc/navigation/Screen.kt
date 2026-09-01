package com.casati.dermcalc.navigation

import com.casati.dermcalc.data.local.TipoCalcolo

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Bmi : Screen("bmi")
    data object Bsa : Screen("bsa")
    data object Risultato : Screen("risultato")
    data object Pazienti : Screen("pazienti")
    data object NuovoPaziente : Screen("pazienti/nuovo")
    data object Impostazioni : Screen("impostazioni")
    data object ModificaProfilo : Screen("profilo/modifica")
    data object CambioPin : Screen("profilo/pin")

    // PASI ed EASI condividono la schermata: cambia solo l'indice passato come argomento.
    data object Calcolatore : Screen("calcolatore/{$ARG_TIPO}") {
        fun creaRoute(tipo: TipoCalcolo) = "calcolatore/${tipo.name}"
    }

    data object PazienteDettaglio : Screen("pazienti/{$ARG_PAZIENTE_ID}") {
        fun creaRoute(pazienteId: Long) = "pazienti/$pazienteId"
    }

    data object ModificaPaziente : Screen("pazienti/{$ARG_PAZIENTE_ID}/modifica") {
        fun creaRoute(pazienteId: Long) = "pazienti/$pazienteId/modifica"
    }

    companion object {
        const val ARG_PAZIENTE_ID = "pazienteId"
        const val ARG_TIPO = "tipo"
    }
}
