package com.casati.dermcalc.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.casati.dermcalc.data.local.ImpostazioniManager
import com.casati.dermcalc.data.local.PinManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val pinConfigurato: Boolean,
    val sbloccato: Boolean = false,
    val biometriaAttiva: Boolean = true
)

class AuthViewModel(
    private val pinManager: PinManager,
    private val impostazioniManager: ImpostazioniManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            pinConfigurato = pinManager.isPinConfigurato(),
            biometriaAttiva = impostazioniManager.isBiometriaAttiva()
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState

    fun impostaPin(pin: String) {
        if (pin.length != LUNGHEZZA_PIN) return
        pinManager.impostaPin(pin)
        _uiState.update { it.copy(pinConfigurato = true, sbloccato = true) }
    }

    fun verificaPin(pin: String): Boolean {
        val corretto = pinManager.verificaPin(pin)
        if (corretto) {
            _uiState.update { it.copy(sbloccato = true) }
        }
        return corretto
    }

    fun sbloccaConBiometria() {
        _uiState.update { it.copy(sbloccato = true) }
    }

    fun impostaBiometria(attiva: Boolean) {
        impostazioniManager.impostaBiometria(attiva)
        _uiState.update { it.copy(biometriaAttiva = attiva) }
    }

    companion object {
        const val LUNGHEZZA_PIN = 6
    }

    class Factory(
        private val pinManager: PinManager,
        private val impostazioniManager: ImpostazioniManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(pinManager, impostazioniManager) as T
        }
    }
}
