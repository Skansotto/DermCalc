package com.casati.dermcalc.ui.screens.pasi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.data.repository.MisurazioneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PasiUiState(
    val valori: Map<PasiDistretto, PasiDistrettoValori> =
        PasiDistretto.entries.associateWith { PasiDistrettoValori() },
    val risultato: Double? = null,
    val pazienteSelezionatoId: Long? = null
) {
    val interpretazione: String?
        get() = risultato?.let {
            when {
                it < 5.0 -> "Lieve"
                it <= 10.0 -> "Moderata"
                else -> "Severa"
            }
        }
}

class PasiViewModel(private val misurazioneRepository: MisurazioneRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PasiUiState())
    val uiState: StateFlow<PasiUiState> = _uiState

    fun onEritemaChange(distretto: PasiDistretto, valore: Int) {
        aggiornaDistretto(distretto) { it.copy(eritema = valore.coerceIn(0, 4)) }
    }

    fun onIndurimentoChange(distretto: PasiDistretto, valore: Int) {
        aggiornaDistretto(distretto) { it.copy(indurimento = valore.coerceIn(0, 4)) }
    }

    fun onDesquamazioneChange(distretto: PasiDistretto, valore: Int) {
        aggiornaDistretto(distretto) { it.copy(desquamazione = valore.coerceIn(0, 4)) }
    }

    fun onAreaChange(distretto: PasiDistretto, valore: Int) {
        aggiornaDistretto(distretto) { it.copy(area = valore.coerceIn(0, 6)) }
    }

    fun onPazienteSelezionatoChange(pazienteId: Long?) {
        _uiState.update { it.copy(pazienteSelezionatoId = pazienteId) }
    }

    fun onCalcolaClick() {
        val punteggio = _uiState.value.valori.entries.sumOf { (distretto, valori) ->
            distretto.peso * (valori.eritema + valori.indurimento + valori.desquamazione) * valori.area
        }
        _uiState.update { it.copy(risultato = punteggio) }
        salvaSeNecessario(punteggio)
    }

    fun reset() {
        _uiState.update { PasiUiState() }
    }

    private fun salvaSeNecessario(risultato: Double) {
        val pazienteId = _uiState.value.pazienteSelezionatoId ?: return
        viewModelScope.launch {
            misurazioneRepository.salvaMisurazione(pazienteId, TipoCalcolo.PASI, risultato)
        }
    }

    private fun aggiornaDistretto(
        distretto: PasiDistretto,
        trasformazione: (PasiDistrettoValori) -> PasiDistrettoValori
    ) {
        _uiState.update { stato ->
            val valoriAggiornati = stato.valori.toMutableMap()
            valoriAggiornati[distretto] = trasformazione(valoriAggiornati.getValue(distretto))
            stato.copy(valori = valoriAggiornati, risultato = null)
        }
    }

    class Factory(private val misurazioneRepository: MisurazioneRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PasiViewModel(misurazioneRepository) as T
        }
    }
}
