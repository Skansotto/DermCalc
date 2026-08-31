package com.casati.dermcalc.ui.screens.pazienti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.repository.MisurazioneRepository
import com.casati.dermcalc.data.repository.PazienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MisurazioneVoce(
    val misurazione: MisurazioneEntity,
    val differenza: Double?,
    val dataPrecedente: Long?
)

class PazienteDettaglioViewModel(
    private val pazienteId: Long,
    private val pazienteRepository: PazienteRepository,
    private val misurazioneRepository: MisurazioneRepository
) : ViewModel() {

    private val _paziente = MutableStateFlow<PazienteEntity?>(null)
    val paziente: StateFlow<PazienteEntity?> = _paziente

    private val _pazienteEliminato = MutableStateFlow(false)
    val pazienteEliminato: StateFlow<Boolean> = _pazienteEliminato

    val misurazioni: StateFlow<List<MisurazioneVoce>> = misurazioneRepository
        .osservaMisurazioni(pazienteId)
        .map(::calcolaVoci)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _paziente.value = pazienteRepository.trovaPaziente(pazienteId)
        }
    }

    fun aggiornaPaziente(nome: String, dataNascita: Long) {
        val nomePulito = nome.trim()
        if (nomePulito.isEmpty()) return
        val attuale = _paziente.value ?: return
        viewModelScope.launch {
            val aggiornato = attuale.copy(nome = nomePulito, dataNascita = dataNascita)
            pazienteRepository.aggiornaPaziente(aggiornato)
            _paziente.value = aggiornato
        }
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
