package com.casati.dermcalc.ui.screens.bmi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.data.repository.MisurazioneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BmiUiState(
    val peso: String = "",
    val altezza: String = "",
    val risultato: Double? = null,
    val messaggioErrore: String? = null,
    val pazienteSelezionatoId: Long? = null
)

class BmiViewModel(private val misurazioneRepository: MisurazioneRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(BmiUiState())
    val uiState: StateFlow<BmiUiState> = _uiState

    fun onPesoChange(value: String) {
        _uiState.update { it.copy(peso = value, risultato = null, messaggioErrore = null) }
    }

    fun onAltezzaChange(value: String) {
        _uiState.update { it.copy(altezza = value, risultato = null, messaggioErrore = null) }
    }

    fun onPazienteSelezionatoChange(pazienteId: Long?) {
        _uiState.update { it.copy(pazienteSelezionatoId = pazienteId) }
    }

    fun onCalcolaClick() {
        val state = _uiState.value
        val pesoInput = state.peso.trim()
        val altezzaInput = state.altezza.trim()

        if (pesoInput.isEmpty() || altezzaInput.isEmpty()) {
            mostraErrore("Inserisci sia il peso che l'altezza.")
            return
        }

        val peso = pesoInput.replace(',', '.').toDoubleOrNull()
        val altezza = altezzaInput.replace(',', '.').toDoubleOrNull()

        if (peso == null || altezza == null) {
            mostraErrore("Inserisci valori numerici validi.")
            return
        }

        if (peso <= 0 || altezza <= 0) {
            mostraErrore("Peso e altezza devono essere maggiori di zero.")
            return
        }

        val bmi = peso / (altezza * altezza)
        _uiState.update { it.copy(risultato = bmi, messaggioErrore = null) }
        salvaSeNecessario(bmi)
    }

    fun reset() {
        _uiState.update { BmiUiState() }
    }

    private fun salvaSeNecessario(risultato: Double) {
        val pazienteId = _uiState.value.pazienteSelezionatoId ?: return
        viewModelScope.launch {
            misurazioneRepository.salvaMisurazione(pazienteId, TipoCalcolo.BMI, risultato)
        }
    }

    private fun mostraErrore(messaggio: String) {
        _uiState.update { it.copy(risultato = null, messaggioErrore = messaggio) }
    }

    class Factory(private val misurazioneRepository: MisurazioneRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BmiViewModel(misurazioneRepository) as T
        }
    }
}
