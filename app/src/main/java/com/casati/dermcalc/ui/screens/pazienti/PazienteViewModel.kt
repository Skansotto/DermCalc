package com.casati.dermcalc.ui.screens.pazienti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.repository.PazienteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PazienteViewModel(private val repository: PazienteRepository) : ViewModel() {

    val pazienti: StateFlow<List<PazienteEntity>> = repository.osservaPazienti()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun aggiungiPaziente(nome: String, dataNascita: Long) {
        val nomePulito = nome.trim()
        if (nomePulito.isEmpty()) return
        viewModelScope.launch {
            repository.aggiungiPaziente(nomePulito, dataNascita)
        }
    }

    fun eliminaPaziente(paziente: PazienteEntity) {
        viewModelScope.launch {
            repository.eliminaPaziente(paziente)
        }
    }

    class Factory(private val repository: PazienteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PazienteViewModel(repository) as T
        }
    }
}
