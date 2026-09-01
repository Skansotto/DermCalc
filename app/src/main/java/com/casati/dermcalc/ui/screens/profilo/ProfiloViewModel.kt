package com.casati.dermcalc.ui.screens.profilo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.casati.dermcalc.data.local.ProfiloManager
import com.casati.dermcalc.data.local.ProfiloMedico
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ProfiloUiState(
    val registrato: Boolean,
    val profilo: ProfiloMedico?
)

class ProfiloViewModel(private val profiloManager: ProfiloManager) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfiloUiState(
            registrato = profiloManager.isRegistrato(),
            profilo = profiloManager.leggiProfilo()
        )
    )
    val uiState: StateFlow<ProfiloUiState> = _uiState

    fun salvaProfilo(nome: String, cognome: String, struttura: String): Boolean {
        val nomePulito = nome.trim()
        val cognomePulito = cognome.trim()
        if (nomePulito.length < 2 || cognomePulito.length < 2) return false
        val profilo = ProfiloMedico(nomePulito, cognomePulito, struttura.trim())
        profiloManager.salvaProfilo(profilo)
        _uiState.update { it.copy(registrato = true, profilo = profilo) }
        return true
    }

    class Factory(private val profiloManager: ProfiloManager) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfiloViewModel(profiloManager) as T
        }
    }
}
