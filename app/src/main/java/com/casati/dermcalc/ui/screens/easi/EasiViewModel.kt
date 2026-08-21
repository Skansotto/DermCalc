package com.casati.dermcalc.ui.screens.easi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class EasiUiState(
    val valori: Map<EasiDistretto, EasiDistrettoValori> =
        EasiDistretto.entries.associateWith { EasiDistrettoValori() }
)

class EasiViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EasiUiState())
    val uiState: StateFlow<EasiUiState> = _uiState

    fun onEritemaChange(distretto: EasiDistretto, valore: Int) {
        aggiornaDistretto(distretto) { it.copy(eritema = valore.coerceIn(0, 3)) }
    }

    fun onEdemaPapulazioneChange(distretto: EasiDistretto, valore: Int) {
        aggiornaDistretto(distretto) { it.copy(edemaPapulazione = valore.coerceIn(0, 3)) }
    }

    fun onEscoriazioniChange(distretto: EasiDistretto, valore: Int) {
        aggiornaDistretto(distretto) { it.copy(escoriazioni = valore.coerceIn(0, 3)) }
    }

    fun onLichenificazioneChange(distretto: EasiDistretto, valore: Int) {
        aggiornaDistretto(distretto) { it.copy(lichenificazione = valore.coerceIn(0, 3)) }
    }

    fun onAreaChange(distretto: EasiDistretto, valore: Int) {
        aggiornaDistretto(distretto) { it.copy(area = valore.coerceIn(0, 6)) }
    }

    private fun aggiornaDistretto(
        distretto: EasiDistretto,
        trasformazione: (EasiDistrettoValori) -> EasiDistrettoValori
    ) {
        _uiState.update { stato ->
            val valoriAggiornati = stato.valori.toMutableMap()
            valoriAggiornati[distretto] = trasformazione(valoriAggiornati.getValue(distretto))
            stato.copy(valori = valoriAggiornati)
        }
    }
}
