package com.casati.dermcalc.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.data.repository.MisurazioneRepository
import com.casati.dermcalc.data.repository.PazienteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Riepilogo mostrato accanto a ogni paziente nell'elenco di selezione: l'ultimo
// valore registrato dice subito se il paziente è già seguito e con quale indice.
data class PazienteSelezionabile(
    val paziente: PazienteEntity,
    val ultimaMisurazione: MisurazioneEntity?,
    val deltaUltima: Double? = null
)

// Elenco dei pazienti arricchito con l'ultima misurazione, condiviso da tutte le
// schermate che permettono di collegare un calcolo a un paziente.
class SelezionePazienteViewModel(
    pazienteRepository: PazienteRepository,
    misurazioneRepository: MisurazioneRepository
) : ViewModel() {

    // Serve per intero al referto, che elenca tutte le misurazioni del paziente.
    // La condivisione è Eagerly perché il valore viene letto direttamente al momento
    // della generazione del PDF, non solo osservato dalla UI.
    val tutteLeMisurazioni: StateFlow<List<MisurazioneEntity>> = misurazioneRepository
        .osservaTutte()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun misurazioniDi(pazienteId: Long): List<MisurazioneEntity> =
        tutteLeMisurazioni.value.filter { it.pazienteId == pazienteId }

    val pazienti: StateFlow<List<PazienteSelezionabile>> = combine(
        pazienteRepository.osservaPazienti(),
        misurazioneRepository.osservaTutte()
    ) { pazienti, misurazioni ->
        pazienti.map { paziente ->
            val diPaziente = misurazioni.filter { it.pazienteId == paziente.id }
            val ultima = diPaziente.maxByOrNull { it.data }
            // Il confronto è sempre con la misurazione precedente dello stesso indice:
            // mettere a confronto un PASI con un BMI non direbbe nulla.
            val precedente = ultima?.let { corrente ->
                diPaziente
                    .filter { it.tipo == corrente.tipo && it.id != corrente.id && it.data <= corrente.data }
                    .maxByOrNull { it.data }
            }
            PazienteSelezionabile(
                paziente = paziente,
                ultimaMisurazione = ultima,
                deltaUltima = if (ultima != null && precedente != null) {
                    ultima.risultato - precedente.risultato
                } else {
                    null
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun ultimaDelTipo(pazienteId: Long?, tipo: TipoCalcolo): MisurazioneEntity? {
        if (pazienteId == null) return null
        val voce = pazienti.value.firstOrNull { it.paziente.id == pazienteId } ?: return null
        return voce.ultimaMisurazione?.takeIf { it.tipo == tipo }
    }

    class Factory(
        private val pazienteRepository: PazienteRepository,
        private val misurazioneRepository: MisurazioneRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SelezionePazienteViewModel(pazienteRepository, misurazioneRepository) as T
        }
    }
}
