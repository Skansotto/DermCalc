package com.casati.dermcalc.ui.screens.bsa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.data.repository.MisurazioneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BsaUiState(
    val regioniSelezionate: Set<BsaRegion> = emptySet(),
    val pazienteSelezionatoId: Long? = null,
    val salvataggioConfermato: Boolean = false
) {
    val totale: Int get() = regioniSelezionate.sumOf { it.percentuale }
}

class BsaViewModel(private val misurazioneRepository: MisurazioneRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(BsaUiState())
    val uiState: StateFlow<BsaUiState> = _uiState

    fun onRegioneToggle(regione: BsaRegion, selezionata: Boolean) {
        _uiState.update { stato ->
            val nuoveRegioni = if (selezionata) {
                stato.regioniSelezionate + regione
            } else {
                stato.regioniSelezionate - regione
            }
            stato.copy(regioniSelezionate = nuoveRegioni, salvataggioConfermato = false)
        }
    }

    fun onPazienteSelezionatoChange(pazienteId: Long?) {
        _uiState.update { it.copy(pazienteSelezionatoId = pazienteId, salvataggioConfermato = false) }
    }

    fun onSalvaClick() {
        val stato = _uiState.value
        val pazienteId = stato.pazienteSelezionatoId ?: return
        if (stato.regioniSelezionate.isEmpty()) return
        viewModelScope.launch {
            misurazioneRepository.salvaMisurazione(pazienteId, TipoCalcolo.BSA, stato.totale.toDouble())
        }
        _uiState.update { it.copy(salvataggioConfermato = true) }
    }

    fun reset() {
        _uiState.update { BsaUiState() }
    }

    class Factory(private val misurazioneRepository: MisurazioneRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BsaViewModel(misurazioneRepository) as T
        }
    }
}
