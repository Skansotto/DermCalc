package com.casati.dermcalc.ui.screens.bmi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class BmiUiState(
    val peso: String = "",
    val altezza: String = "",
    val risultato: Double? = null,
    val messaggioErrore: String? = null
)

class BmiViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BmiUiState())
    val uiState: StateFlow<BmiUiState> = _uiState

    fun onPesoChange(value: String) {
        _uiState.update { it.copy(peso = value, risultato = null, messaggioErrore = null) }
    }

    fun onAltezzaChange(value: String) {
        _uiState.update { it.copy(altezza = value, risultato = null, messaggioErrore = null) }
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
    }

    fun reset() {
        _uiState.update { BmiUiState() }
    }

    private fun mostraErrore(messaggio: String) {
        _uiState.update { it.copy(risultato = null, messaggioErrore = messaggio) }
    }
}
