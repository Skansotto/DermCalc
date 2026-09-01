package com.casati.dermcalc

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.navigation.DermCalcNavHost
import com.casati.dermcalc.ui.screens.auth.AuthViewModel
import com.casati.dermcalc.ui.screens.auth.CreaPinScreen
import com.casati.dermcalc.ui.screens.auth.SbloccoScreen
import com.casati.dermcalc.ui.screens.onboarding.OnboardingScreen
import com.casati.dermcalc.ui.screens.onboarding.OnboardingViewModel
import com.casati.dermcalc.ui.screens.profilo.ProfiloViewModel
import com.casati.dermcalc.ui.screens.profilo.RegistrazioneScreen
import com.casati.dermcalc.ui.theme.DermCalcTheme
import com.casati.dermcalc.ui.theme.Sfondo

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DermCalcTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        // Il padding di sistema è applicato una sola volta qui: tutte le
                        // schermate, comprese quelle dentro al NavHost, restano dentro
                        // l'area sicura senza doversene occupare.
                        .windowInsetsPadding(WindowInsets.systemBars),
                    color = Sfondo
                ) {
                    DermCalcApp(modifier = Modifier.fillMaxSize().background(Sfondo))
                }
            }
        }
    }
}

// Ordine di avvio: prima si crea l'account locale, poi il PIN, poi la guida
// introduttiva. Lo sblocco è richiesto a ogni apertura successiva.
@Composable
private fun DermCalcApp(modifier: Modifier = Modifier) {
    val application = LocalContext.current.applicationContext as DermCalcApplication

    val profiloViewModel: ProfiloViewModel = viewModel(
        factory = ProfiloViewModel.Factory(application.profiloManager)
    )
    val profiloState by profiloViewModel.uiState.collectAsState()

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(application.pinManager, application.impostazioniManager)
    )
    val authState by authViewModel.uiState.collectAsState()

    val onboardingViewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModel.Factory(application.onboardingManager)
    )
    val onboardingState by onboardingViewModel.uiState.collectAsState()

    when {
        !profiloState.registrato -> RegistrazioneScreen(
            viewModel = profiloViewModel,
            inModifica = false,
            onCompletato = { },
            modifier = modifier
        )

        !authState.pinConfigurato -> CreaPinScreen(viewModel = authViewModel, modifier = modifier)

        !onboardingState.completato -> OnboardingScreen(
            viewModel = onboardingViewModel,
            modifier = modifier
        )

        !authState.sbloccato -> SbloccoScreen(
            viewModel = authViewModel,
            nomeMedico = profiloState.profilo?.nomeCompleto.orEmpty(),
            biometriaAttiva = authState.biometriaAttiva,
            modifier = modifier
        )

        else -> DermCalcNavHost(
            authViewModel = authViewModel,
            profiloViewModel = profiloViewModel
        )
    }
}
