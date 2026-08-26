package com.casati.dermcalc.ui.screens.pazienti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.repository.PazienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PazienteDettaglioViewModel(
    private val pazienteId: Long,
    private val repository: PazienteRepository
) : ViewModel() {

    private val _paziente = MutableStateFlow<PazienteEntity?>(null)
    val paziente: StateFlow<PazienteEntity?> = _paziente

    init {
        viewModelScope.launch {
            _paziente.value = repository.trovaPaziente(pazienteId)
        }
    }

    class Factory(
        private val pazienteId: Long,
        private val repository: PazienteRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PazienteDettaglioViewModel(pazienteId, repository) as T
        }
    }
}
