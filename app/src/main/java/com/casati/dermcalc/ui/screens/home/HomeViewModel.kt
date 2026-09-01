package com.casati.dermcalc.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.repository.MisurazioneRepository
import com.casati.dermcalc.data.repository.PazienteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Voce dell'elenco "misurazioni recenti": mostra chi, quale indice e quanto è
// cambiato rispetto alla volta precedente.
data class MisurazioneRecente(
    val misurazione: MisurazioneEntity,
    val paziente: PazienteEntity,
    val delta: Double?
)

data class HomeUiState(
    val recenti: List<MisurazioneRecente> = emptyList(),
    val totaleMisurazioni: Int = 0
)

class HomeViewModel(
    pazienteRepository: PazienteRepository,
    misurazioneRepository: MisurazioneRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        pazienteRepository.osservaPazienti(),
        misurazioneRepository.osservaTutte()
    ) { pazienti, misurazioni ->
        val perId = pazienti.associateBy { it.id }
        val recenti = misurazioni
            .sortedByDescending { it.data }
            .take(3)
            .mapNotNull { misurazione ->
                val paziente = perId[misurazione.pazienteId] ?: return@mapNotNull null
                val precedente = misurazioni
                    .filter {
                        it.pazienteId == misurazione.pazienteId &&
                            it.tipo == misurazione.tipo &&
                            it.id != misurazione.id &&
                            it.data <= misurazione.data
                    }
                    .maxByOrNull { it.data }
                MisurazioneRecente(
                    misurazione = misurazione,
                    paziente = paziente,
                    delta = precedente?.let { misurazione.risultato - it.risultato }
                )
            }
        HomeUiState(recenti = recenti, totaleMisurazioni = misurazioni.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    class Factory(
        private val pazienteRepository: PazienteRepository,
        private val misurazioneRepository: MisurazioneRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(pazienteRepository, misurazioneRepository) as T
        }
    }
}
