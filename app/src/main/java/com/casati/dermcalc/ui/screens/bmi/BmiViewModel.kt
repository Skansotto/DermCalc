package com.casati.dermcalc.ui.screens.bmi

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
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.Rosso
import com.casati.dermcalc.ui.theme.RossoTenue
import com.casati.dermcalc.ui.theme.Verde
import com.casati.dermcalc.ui.theme.VerdeTenue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.round

// Peso e altezza si regolano a passi invece che da tastiera: in visita si parte
// sempre da un valore plausibile e si aggiusta di poco.
data class BmiUiState(
    val peso: Double = 74.0,
    val altezzaCm: Int = 176
) {
    val valore: Double
        get() {
            val metri = altezzaCm / 100.0
            return peso / (metri * metri)
        }

    val valoreArrotondato: Double get() = round(valore * 10) / 10

    @get:StringRes
    val categoriaRes: Int
        get() = when {
            valore < 18.5 -> R.string.bmi_categoria_sottopeso
            valore < 25 -> R.string.bmi_categoria_normopeso
            valore < 30 -> R.string.bmi_categoria_sovrappeso
            else -> R.string.bmi_categoria_obesita
        }

    val colore: Color
        get() = when {
            valore < 18.5 -> Indaco
            valore < 25 -> Verde
            valore < 30 -> Ambra
            else -> Rosso
        }

    val tinta: Color
        get() = when {
            valore < 18.5 -> IndacoTenue
            valore < 25 -> VerdeTenue
            valore < 30 -> AmbraTenue
            else -> RossoTenue
        }

    // Posizione dell'indicatore sulla scala 15-40 mostrata sotto il punteggio.
    val posizioneScala: Float
        get() = (((valore - 15) / 25).coerceIn(0.0, 1.0)).toFloat()
}

class BmiViewModel(
    private val misurazioneRepository: MisurazioneRepository,
    private val sessione: SessioneCorrente
) : ViewModel() {

    private val _uiState = MutableStateFlow(BmiUiState())
    val uiState: StateFlow<BmiUiState> = _uiState

    val pazienteCollegatoId: StateFlow<Long?> = sessione.pazienteCollegatoId

    fun onPesoIncrementa() {
        _uiState.update { it.copy(peso = (it.peso + 0.5).coerceAtMost(250.0)) }
    }

    fun onPesoDecrementa() {
        _uiState.update { it.copy(peso = (it.peso - 0.5).coerceAtLeast(30.0)) }
    }

    fun onAltezzaIncrementa() {
        _uiState.update { it.copy(altezzaCm = (it.altezzaCm + 1).coerceAtMost(230)) }
    }

    fun onAltezzaDecrementa() {
        _uiState.update { it.copy(altezzaCm = (it.altezzaCm - 1).coerceAtLeast(120)) }
    }

    fun collegaPaziente(pazienteId: Long?) {
        sessione.collega(pazienteId)
    }

    fun salva(onSalvato: (Long, Double) -> Unit) {
        val pazienteId = sessione.pazienteCollegatoId.value ?: return
        val valore = _uiState.value.valoreArrotondato
        viewModelScope.launch {
            misurazioneRepository.salvaMisurazione(pazienteId, TipoCalcolo.BMI, valore)
            onSalvato(pazienteId, valore)
        }
    }

    class Factory(
        private val misurazioneRepository: MisurazioneRepository,
        private val sessione: SessioneCorrente
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BmiViewModel(misurazioneRepository, sessione) as T
        }
    }
}
