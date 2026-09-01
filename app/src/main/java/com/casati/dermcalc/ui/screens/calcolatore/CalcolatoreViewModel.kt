package com.casati.dermcalc.ui.screens.calcolatore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.SessioneCorrente
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.data.repository.MisurazioneRepository
import com.casati.dermcalc.domain.ContributoDistretto
import com.casati.dermcalc.domain.Distretto
import com.casati.dermcalc.domain.Indice
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.domain.ValoriDistretto
import com.casati.dermcalc.domain.calcolaContributi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.round

data class CalcolatoreUiState(
    val tipo: TipoCalcolo = TipoCalcolo.PASI,
    val valori: Map<Distretto, ValoriDistretto> = valoriIniziali(TipoCalcolo.PASI)
) {
    val contributi: List<ContributoDistretto> get() = calcolaContributi(tipo, valori)

    // Il totale è arrotondato a un decimale come richiesto dalla refertazione clinica.
    val totale: Double get() = round(contributi.sumOf { it.subtotale } * 10) / 10

    val dettaglioFormula: String
        get() = buildString {
            append(tipo.name).append(" = Σ peso × (somma parametri) × area\n\n")
            contributi.forEach { append("  ").append(it.formula).append('\n') }
            append("\nTOTALE = ").append(String.format("%.2f", totale)).append("  (range 0–72)")
        }
}

private fun valoriIniziali(tipo: TipoCalcolo): Map<Distretto, ValoriDistretto> {
    val quanti = Indice.parametri(tipo).size
    return Distretto.entries.associateWith { ValoriDistretto(List(quanti) { 0 }) }
}

// Un solo ViewModel per PASI ed EASI: gli indici condividono struttura e formula,
// e la schermata di risultato deve leggere gli stessi valori del calcolatore.
class CalcolatoreViewModel(
    private val misurazioneRepository: MisurazioneRepository,
    private val sessione: SessioneCorrente
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalcolatoreUiState())
    val uiState: StateFlow<CalcolatoreUiState> = _uiState

    val pazienteCollegatoId: StateFlow<Long?> = sessione.pazienteCollegatoId

    fun impostaTipo(tipo: TipoCalcolo) {
        if (_uiState.value.tipo == tipo) return
        _uiState.value = CalcolatoreUiState(tipo = tipo, valori = valoriIniziali(tipo))
    }

    fun onParametroChange(distretto: Distretto, indice: Int, valore: Int) {
        val massimo = Indice.parametri(_uiState.value.tipo)[indice].massimo
        _uiState.update { stato ->
            val corrente = stato.valori.getValue(distretto)
            val parametri = corrente.parametri.toMutableList()
            parametri[indice] = valore.coerceIn(0, massimo)
            stato.copy(valori = stato.valori + (distretto to corrente.copy(parametri = parametri)))
        }
    }

    fun onAreaChange(distretto: Distretto, valore: Int) {
        _uiState.update { stato ->
            val corrente = stato.valori.getValue(distretto)
            stato.copy(valori = stato.valori + (distretto to corrente.copy(area = valore.coerceIn(0, 6))))
        }
    }

    fun collegaPaziente(pazienteId: Long?) {
        sessione.collega(pazienteId)
    }

    fun azzera() {
        _uiState.update { CalcolatoreUiState(tipo = it.tipo, valori = valoriIniziali(it.tipo)) }
    }

    // Restituisce l'id del paziente su cui è stata salvata la misurazione, oppure null
    // se il calcolo è anonimo e quindi non va archiviato.
    fun salva(onSalvato: (Long, TipoCalcolo, Double) -> Unit) {
        val pazienteId = sessione.pazienteCollegatoId.value ?: return
        val stato = _uiState.value
        viewModelScope.launch {
            misurazioneRepository.salvaMisurazione(pazienteId, stato.tipo, stato.totale)
            onSalvato(pazienteId, stato.tipo, stato.totale)
        }
    }

    fun bandaCorrente() = Severita.banda(_uiState.value.tipo, _uiState.value.totale)

    class Factory(
        private val misurazioneRepository: MisurazioneRepository,
        private val sessione: SessioneCorrente
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalcolatoreViewModel(misurazioneRepository, sessione) as T
        }
    }
}
