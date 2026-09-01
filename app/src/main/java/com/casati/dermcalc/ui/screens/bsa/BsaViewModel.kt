package com.casati.dermcalc.ui.screens.bsa

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.SessioneCorrente
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.data.repository.MisurazioneRepository
import com.casati.dermcalc.ui.theme.Ambra
import com.casati.dermcalc.ui.theme.AmbraTenue
import com.casati.dermcalc.ui.theme.Rosso
import com.casati.dermcalc.ui.theme.RossoTenue
import com.casati.dermcalc.ui.theme.Verde
import com.casati.dermcalc.ui.theme.VerdeTenue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BsaUiState(
    val regioniSelezionate: Set<BsaRegion> = emptySet(),
    val palmi: Int = 0
) {
    // I palmi servono per le lesioni sparse: un palmo del paziente vale circa l'1%.
    val totale: Double
        get() = (regioniSelezionate.sumOf { it.percentuale } + palmi).coerceAtMost(100.0)

    @get:StringRes
    val categoriaRes: Int
        get() = when {
            totale <= 10.0 -> R.string.bsa_categoria_lieve
            totale <= 30.0 -> R.string.bsa_categoria_moderata
            else -> R.string.bsa_categoria_severa
        }

    val colore: Color
        get() = when {
            totale <= 10.0 -> Verde
            totale <= 30.0 -> Ambra
            else -> Rosso
        }

    val tinta: Color
        get() = when {
            totale <= 10.0 -> VerdeTenue
            totale <= 30.0 -> AmbraTenue
            else -> RossoTenue
        }
}

class BsaViewModel(
    private val misurazioneRepository: MisurazioneRepository,
    private val sessione: SessioneCorrente
) : ViewModel() {

    private val _uiState = MutableStateFlow(BsaUiState())
    val uiState: StateFlow<BsaUiState> = _uiState

    val pazienteCollegatoId: StateFlow<Long?> = sessione.pazienteCollegatoId

    fun onRegioneToggle(regione: BsaRegion) {
        _uiState.update { stato ->
            val nuove = if (regione in stato.regioniSelezionate) {
                stato.regioniSelezionate - regione
            } else {
                stato.regioniSelezionate + regione
            }
            stato.copy(regioniSelezionate = nuove)
        }
    }

    fun onPalmiIncrementa() {
        _uiState.update { it.copy(palmi = (it.palmi + 1).coerceAtMost(20)) }
    }

    fun onPalmiDecrementa() {
        _uiState.update { it.copy(palmi = (it.palmi - 1).coerceAtLeast(0)) }
    }

    fun collegaPaziente(pazienteId: Long?) {
        sessione.collega(pazienteId)
    }

    fun azzera() {
        _uiState.value = BsaUiState()
    }

    fun salva(onSalvato: (Long, Double) -> Unit) {
        val pazienteId = sessione.pazienteCollegatoId.value ?: return
        val totale = _uiState.value.totale
        viewModelScope.launch {
            misurazioneRepository.salvaMisurazione(pazienteId, TipoCalcolo.BSA, totale)
            onSalvato(pazienteId, totale)
        }
    }

    class Factory(
        private val misurazioneRepository: MisurazioneRepository,
        private val sessione: SessioneCorrente
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BsaViewModel(misurazioneRepository, sessione) as T
        }
    }
}
