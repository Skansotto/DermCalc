package com.casati.dermcalc.ui.screens.bmi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class BmiUiState(
    val peso: String = "",
    val altezza: String = ""
)

class BmiViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BmiUiState())
    val uiState: StateFlow<BmiUiState> = _uiState

    fun onPesoChange(value: String) {
        _uiState.update { it.copy(peso = value) }
    }

    fun onAltezzaChange(value: String) {
        _uiState.update { it.copy(altezza = value) }
    }
}
