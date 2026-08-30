package com.casati.dermcalc.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.casati.dermcalc.data.local.OnboardingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class OnboardingUiState(
    val completato: Boolean
)

class OnboardingViewModel(private val onboardingManager: OnboardingManager) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState(completato = onboardingManager.isCompletato()))
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun completaOnboarding() {
        onboardingManager.segnaCompletato()
        _uiState.update { it.copy(completato = true) }
    }

    class Factory(private val onboardingManager: OnboardingManager) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OnboardingViewModel(onboardingManager) as T
        }
    }
}
