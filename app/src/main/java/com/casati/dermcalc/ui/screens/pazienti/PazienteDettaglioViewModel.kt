package com.casati.dermcalc.ui.screens.pazienti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.data.repository.MisurazioneRepository
import com.casati.dermcalc.data.repository.PazienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Voce dello storico con il confronto rispetto alla misurazione precedente
// dello stesso indice.
data class VoceStorico(
    val misurazione: MisurazioneEntity,
    val precedente: MisurazioneEntity?
) {
    val delta: Double? get() = precedente?.let { misurazione.risultato - it.risultato }
}

// Serie da disegnare nel grafico: già filtrata sull'indice scelto e ordinata nel tempo.
data class SerieGrafico(
    val tipo: TipoCalcolo,
    val punti: List<MisurazioneEntity>,
    val conteggiPerTipo: Map<TipoCalcolo, Int>
) {
    val haDati: Boolean get() = punti.size >= 2
}

data class SchedaUiState(
    val paziente: PazienteEntity? = null,
    // Storico completo: alimenta i riepiloghi, l'esportazione e il referto.
    val storico: List<VoceStorico> = emptyList(),
    // Sottoinsieme mostrato in elenco, secondo i filtri attivi.
    val storicoFiltrato: List<VoceStorico> = emptyList(),
    val tipiPresenti: List<TipoCalcolo> = emptyList(),
    val filtri: Set<TipoCalcolo> = emptySet(),
    val serie: SerieGrafico? = null,
    val eliminato: Boolean = false
) {
    val filtroAttivo: Boolean get() = filtri.isNotEmpty()
}

class PazienteDettaglioViewModel(
    private val pazienteId: Long,
    private val pazienteRepository: PazienteRepository,
    private val misurazioneRepository: MisurazioneRepository
) : ViewModel() {

    private val _tipoGrafico = MutableStateFlow<TipoCalcolo?>(null)
    private val _filtri = MutableStateFlow<Set<TipoCalcolo>>(emptySet())
    private val _eliminato = MutableStateFlow(false)

    val uiState: StateFlow<SchedaUiState> = combine(
        pazienteRepository.osservaPaziente(pazienteId),
        misurazioneRepository.osservaMisurazioni(pazienteId),
        _tipoGrafico,
        _filtri,
        _eliminato
    ) { paziente, misurazioni, tipoScelto, filtri, eliminato ->
        val ordinate = misurazioni.sortedBy { it.data }
        // Il confronto guarda la misurazione precedente dello stesso indice in ordine
        // di tempo, non di giorno: nella stessa giornata possono essercene più di una.
        val storico = ordinate.reversed().map { corrente ->
            VoceStorico(
                misurazione = corrente,
                precedente = ordinate.lastOrNull {
                    it.tipo == corrente.tipo && it.data < corrente.data
                }
            )
        }
        // I filtri riguardano solo l'elenco: le variazioni restano calcolate
        // sull'intero storico, altrimenti nascondere un indice cambierebbe i confronti.
        val filtrato = if (filtri.isEmpty()) storico else storico.filter { it.misurazione.tipo in filtri }
        SchedaUiState(
            paziente = paziente,
            storico = storico,
            storicoFiltrato = filtrato,
            tipiPresenti = TipoCalcolo.ORDINE_CLINICO.filter { tipo ->
                ordinate.any { it.tipo == tipo }
            },
            filtri = filtri,
            serie = costruisciSerie(ordinate, tipoScelto),
            eliminato = eliminato
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SchedaUiState())

    // Se l'indice scelto non ha abbastanza dati si ripiega sul primo che ne ha:
    // il grafico non deve mai aprirsi vuoto quando ci sono dati da mostrare.
    private fun costruisciSerie(
        misurazioni: List<MisurazioneEntity>,
        tipoScelto: TipoCalcolo?
    ): SerieGrafico {
        val conteggi = TipoCalcolo.ORDINE_CLINICO.associateWith { tipo ->
            misurazioni.count { it.tipo == tipo }
        }
        val attivo = tipoScelto?.takeIf { (conteggi[it] ?: 0) > 0 }
            ?: TipoCalcolo.ORDINE_CLINICO.firstOrNull { (conteggi[it] ?: 0) >= 2 }
            ?: TipoCalcolo.ORDINE_CLINICO.firstOrNull { (conteggi[it] ?: 0) > 0 }
            ?: TipoCalcolo.PASI
        return SerieGrafico(
            tipo = attivo,
            punti = misurazioni.filter { it.tipo == attivo },
            conteggiPerTipo = conteggi
        )
    }

    fun selezionaTipoGrafico(tipo: TipoCalcolo) {
        _tipoGrafico.value = tipo
    }

    // Il filtro è a scelta multipla: toccando un indice lo si aggiunge o toglie
    // dall'elenco, e nessun indice selezionato significa "mostrali tutti".
    fun commutaFiltro(tipo: TipoCalcolo) {
        _filtri.update { attuali ->
            if (tipo in attuali) attuali - tipo else attuali + tipo
        }
    }

    fun azzeraFiltri() {
        _filtri.value = emptySet()
    }

    fun eliminaMisurazione(misurazione: MisurazioneEntity) {
        viewModelScope.launch {
            misurazioneRepository.eliminaMisurazione(misurazione)
        }
    }

    fun eliminaPaziente(onEliminato: () -> Unit) {
        val paziente = uiState.value.paziente ?: return
        viewModelScope.launch {
            pazienteRepository.eliminaPaziente(paziente)
            _eliminato.value = true
            onEliminato()
        }
    }

    class Factory(
        private val pazienteId: Long,
        private val pazienteRepository: PazienteRepository,
        private val misurazioneRepository: MisurazioneRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PazienteDettaglioViewModel(pazienteId, pazienteRepository, misurazioneRepository) as T
        }
    }
}
