package com.casati.dermcalc

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.navigation.DermCalcNavHost
import com.casati.dermcalc.ui.screens.auth.AuthViewModel
import com.casati.dermcalc.ui.screens.auth.CreaPinScreen
import com.casati.dermcalc.ui.screens.auth.SbloccoScreen
import com.casati.dermcalc.ui.screens.onboarding.OnboardingScreen
import com.casati.dermcalc.ui.screens.onboarding.OnboardingViewModel
import com.casati.dermcalc.ui.theme.DermCalcTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DermCalcTheme {
                DermCalcApp()
            }
        }
    }
}

@Composable
private fun DermCalcApp() {
    val application = LocalContext.current.applicationContext as DermCalcApplication
    val onboardingViewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModel.Factory(application.onboardingManager)
    )
    val onboardingState by onboardingViewModel.uiState.collectAsState()

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(application.pinManager)
    )
    val authState by authViewModel.uiState.collectAsState()

    when {
        !onboardingState.completato -> OnboardingScreen(viewModel = onboardingViewModel)
        !authState.pinConfigurato -> CreaPinScreen(viewModel = authViewModel)
        !authState.sbloccato -> SbloccoScreen(viewModel = authViewModel)
        else -> DermCalcNavHost()
    }
}
