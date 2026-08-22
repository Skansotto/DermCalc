package com.casati.dermcalc.ui.screens.easi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class EasiUiState(
    val valori: Map<EasiDistretto, EasiDistrettoValori> =
        EasiDistretto.entries.associateWith { EasiDistrettoValori() },
    val risultato: Double? = null
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

    fun onCalcolaClick() {
        val punteggio = _uiState.value.valori.entries.sumOf { (distretto, valori) ->
            distretto.peso *
                (valori.eritema + valori.edemaPapulazione + valori.escoriazioni + valori.lichenificazione) *
                valori.area
        }
        _uiState.update { it.copy(risultato = punteggio) }
    }

    private fun aggiornaDistretto(
        distretto: EasiDistretto,
        trasformazione: (EasiDistrettoValori) -> EasiDistrettoValori
    ) {
        _uiState.update { stato ->
            val valoriAggiornati = stato.valori.toMutableMap()
            valoriAggiornati[distretto] = trasformazione(valoriAggiornati.getValue(distretto))
            stato.copy(valori = valoriAggiornati, risultato = null)
        }
    }
}
