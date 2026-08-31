package com.casati.dermcalc.ui.screens.pazienti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.Sesso
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.data.repository.MisurazioneRepository
import com.casati.dermcalc.data.repository.PazienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MisurazioneVoce(
    val misurazione: MisurazioneEntity,
    val differenza: Double?,
    val dataPrecedente: Long?
)

data class PuntoAndamento(val data: Long, val risultato: Double)

data class SerieAndamento(val tipo: TipoCalcolo, val punti: List<PuntoAndamento>)

private val TIPI_CON_ANDAMENTO = listOf(TipoCalcolo.PASI, TipoCalcolo.EASI)

class PazienteDettaglioViewModel(
    private val pazienteId: Long,
    private val pazienteRepository: PazienteRepository,
    private val misurazioneRepository: MisurazioneRepository
) : ViewModel() {

    private val _paziente = MutableStateFlow<PazienteEntity?>(null)
    val paziente: StateFlow<PazienteEntity?> = _paziente

    private val _pazienteEliminato = MutableStateFlow(false)
    val pazienteEliminato: StateFlow<Boolean> = _pazienteEliminato

    private val _filtroStorico = MutableStateFlow<TipoCalcolo?>(null)
    val filtroStorico: StateFlow<TipoCalcolo?> = _filtroStorico

    val misurazioni: StateFlow<List<MisurazioneVoce>> = misurazioneRepository
        .osservaMisurazioni(pazienteId)
        .map(::calcolaVoci)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val misurazioniFiltrate: StateFlow<List<MisurazioneVoce>> = combine(
        misurazioni,
        _filtroStorico
    ) { voci, filtro ->
        if (filtro == null) voci else voci.filter { it.misurazione.tipo == filtro }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val andamento: StateFlow<List<SerieAndamento>> = misurazioneRepository
        .osservaMisurazioni(pazienteId)
        .map(::calcolaAndamento)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _tipoGraficoScelto = MutableStateFlow<TipoCalcolo?>(null)

    val serieGraficoSelezionata: StateFlow<SerieAndamento?> = combine(
        andamento,
        _tipoGraficoScelto
    ) { serie, scelto ->
        if (serie.isEmpty()) {
            null
        } else {
            serie.firstOrNull { it.tipo == scelto } ?: serie.first()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            _paziente.value = pazienteRepository.trovaPaziente(pazienteId)
        }
    }

    fun aggiornaPaziente(nome: String, cognome: String, sesso: Sesso, dataNascita: Long) {
        val nomePulito = nome.trim()
        val cognomePulito = cognome.trim()
        if (nomePulito.isEmpty() || cognomePulito.isEmpty()) return
        val attuale = _paziente.value ?: return
        viewModelScope.launch {
            val aggiornato = attuale.copy(
                nome = nomePulito,
                cognome = cognomePulito,
                sesso = sesso,
                dataNascita = dataNascita
            )
            pazienteRepository.aggiornaPaziente(aggiornato)
            _paziente.value = aggiornato
        }
    }

    fun impostaFiltroStorico(tipo: TipoCalcolo?) {
        _filtroStorico.value = tipo
    }

    fun selezionaGraficoTipo(tipo: TipoCalcolo) {
        _tipoGraficoScelto.value = tipo
    }

    fun eliminaPaziente() {
        val attuale = _paziente.value ?: return
        viewModelScope.launch {
            pazienteRepository.eliminaPaziente(attuale)
            _pazienteEliminato.value = true
        }
    }

    fun eliminaMisurazione(misurazione: MisurazioneEntity) {
        viewModelScope.launch {
            misurazioneRepository.eliminaMisurazione(misurazione)
        }
    }

    private fun calcolaVoci(misurazioni: List<MisurazioneEntity>): List<MisurazioneVoce> {
        return misurazioni.mapIndexed { indice, misurazione ->
            val precedente = misurazioni.drop(indice + 1).firstOrNull { it.tipo == misurazione.tipo }
            MisurazioneVoce(
                misurazione = misurazione,
                differenza = precedente?.let { misurazione.risultato - it.risultato },
                dataPrecedente = precedente?.data
            )
        }
    }

    private fun calcolaAndamento(misurazioni: List<MisurazioneEntity>): List<SerieAndamento> {
        return TIPI_CON_ANDAMENTO.mapNotNull { tipo ->
            val punti = misurazioni
                .filter { it.tipo == tipo }
                .sortedBy { it.data }
                .map { PuntoAndamento(it.data, it.risultato) }
            if (punti.size >= 2) SerieAndamento(tipo, punti) else null
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
