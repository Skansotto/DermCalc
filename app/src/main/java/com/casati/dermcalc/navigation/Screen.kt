package com.casati.dermcalc.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Bmi : Screen("bmi")
    data object Bsa : Screen("bsa")
    data object Pasi : Screen("pasi")
    data object Easi : Screen("easi")
    data object Pazienti : Screen("pazienti")

    data object PazienteDettaglio : Screen("pazienti/{$ARG_PAZIENTE_ID}") {
        fun creaRoute(pazienteId: Long) = "pazienti/$pazienteId"
    }

    companion object {
        const val ARG_PAZIENTE_ID = "pazienteId"
    }
}
