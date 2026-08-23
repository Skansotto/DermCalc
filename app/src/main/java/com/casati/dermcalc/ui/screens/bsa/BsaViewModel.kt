package com.casati.dermcalc.ui.screens.bsa

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class BsaUiState(
    val regioniSelezionate: Set<BsaRegion> = emptySet()
) {
    val totale: Int get() = regioniSelezionate.sumOf { it.percentuale }
}

class BsaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BsaUiState())
    val uiState: StateFlow<BsaUiState> = _uiState

    fun onRegioneToggle(regione: BsaRegion, selezionata: Boolean) {
        _uiState.update { stato ->
            val nuoveRegioni = if (selezionata) {
                stato.regioniSelezionate + regione
            } else {
                stato.regioniSelezionate - regione
            }
            stato.copy(regioniSelezionate = nuoveRegioni)
        }
    }

    fun reset() {
        _uiState.update { BsaUiState() }
    }
}
