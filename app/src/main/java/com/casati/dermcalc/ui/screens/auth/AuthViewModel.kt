package com.casati.dermcalc.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.casati.dermcalc.data.local.PinManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val pinConfigurato: Boolean,
    val sbloccato: Boolean = false,
    val messaggioErrore: String? = null
)

class AuthViewModel(private val pinManager: PinManager) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(pinConfigurato = pinManager.isPinConfigurato()))
    val uiState: StateFlow<AuthUiState> = _uiState

    fun impostaPin(pin: String, conferma: String) {
        if (pin.isBlank() || conferma.isBlank()) {
            mostraErrore("Inserisci il PIN in entrambi i campi.")
            return
        }
        if (!pin.all { it.isDigit() } || pin.length !in 4..6) {
            mostraErrore("Il PIN deve contenere da 4 a 6 cifre.")
            return
        }
        if (pin != conferma) {
            mostraErrore("I due PIN inseriti non coincidono.")
            return
        }
        pinManager.impostaPin(pin)
        _uiState.update { it.copy(pinConfigurato = true, sbloccato = true, messaggioErrore = null) }
    }

    fun verificaPin(pin: String) {
        if (pin.isBlank()) {
            mostraErrore("Inserisci il PIN.")
            return
        }
        if (pinManager.verificaPin(pin)) {
            _uiState.update { it.copy(sbloccato = true, messaggioErrore = null) }
        } else {
            mostraErrore("PIN non corretto.")
        }
    }

    fun sbloccaConBiometria() {
        _uiState.update { it.copy(sbloccato = true, messaggioErrore = null) }
    }

    private fun mostraErrore(messaggio: String) {
        _uiState.update { it.copy(messaggioErrore = messaggio) }
    }

    class Factory(private val pinManager: PinManager) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(pinManager) as T
        }
    }
}
