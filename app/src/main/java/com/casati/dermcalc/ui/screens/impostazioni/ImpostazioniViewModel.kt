package com.casati.dermcalc.ui.screens.impostazioni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.ImpostazioniManager
import com.casati.dermcalc.data.repository.BackupRepository
import com.casati.dermcalc.data.repository.EsitoImportazione
import com.casati.dermcalc.data.repository.MisurazioneRepository
import com.casati.dermcalc.data.repository.PazienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ImpostazioniUiState(
    val numeroPazienti: Int = 0,
    val numeroMisurazioni: Int = 0
)

class ImpostazioniViewModel(
    private val backupRepository: BackupRepository,
    private val impostazioniManager: ImpostazioniManager,
    pazienteRepository: PazienteRepository,
    misurazioneRepository: MisurazioneRepository
) : ViewModel() {

    val uiState: StateFlow<ImpostazioniUiState> = combine(
        pazienteRepository.osservaPazienti(),
        misurazioneRepository.osservaTutte()
    ) { pazienti, misurazioni ->
        ImpostazioniUiState(pazienti.size, misurazioni.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ImpostazioniUiState())

    private val _biometriaAttiva = MutableStateFlow(impostazioniManager.isBiometriaAttiva())
    val biometriaAttiva: StateFlow<Boolean> = _biometriaAttiva

    fun cambiaBiometria() {
        val nuovo = !_biometriaAttiva.value
        impostazioniManager.impostaBiometria(nuovo)
        _biometriaAttiva.update { nuovo }
    }

    fun esporta(onPronto: (String) -> Unit) {
        viewModelScope.launch {
            onPronto(backupRepository.esporta())
        }
    }

    fun importa(contenuto: String, onEsito: (EsitoImportazione?) -> Unit) {
        viewModelScope.launch {
            onEsito(runCatching { backupRepository.importa(contenuto) }.getOrNull())
        }
    }

    fun eliminaTuttiIDati(onFatto: () -> Unit) {
        viewModelScope.launch {
            backupRepository.eliminaTutto()
            onFatto()
        }
    }

    class Factory(
        private val backupRepository: BackupRepository,
        private val impostazioniManager: ImpostazioniManager,
        private val pazienteRepository: PazienteRepository,
        private val misurazioneRepository: MisurazioneRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ImpostazioniViewModel(
                backupRepository,
                impostazioniManager,
                pazienteRepository,
                misurazioneRepository
            ) as T
        }
    }
}
