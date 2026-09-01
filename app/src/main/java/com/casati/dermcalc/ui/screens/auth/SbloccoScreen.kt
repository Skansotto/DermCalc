package com.casati.dermcalc.ui.screens.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.components.Marchio
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoBordo
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.MonoGrande
import com.casati.dermcalc.ui.theme.Rosso
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoTenue
import kotlinx.coroutines.delay

@Composable
fun SbloccoScreen(
    viewModel: AuthViewModel,
    nomeMedico: String,
    biometriaAttiva: Boolean,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var errore by remember { mutableStateOf<String?>(null) }
    val messaggioErrore = stringResource(R.string.auth_errore_pin)
    val context = LocalContext.current

    // Con il PIN completo la verifica parte da sola: nessun pulsante da premere in più.
    LaunchedEffect(pin) {
        if (pin.length == AuthViewModel.LUNGHEZZA_PIN) {
            delay(160)
            if (!viewModel.verificaPin(pin)) {
                errore = messaggioErrore
                pin = ""
            }
        }
    }

    val avviaBiometria = {
        val activity = context as? FragmentActivity
        if (activity != null && biometriaDisponibile(context)) {
            val prompt = BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(context),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        viewModel.sbloccaConBiometria()
                    }
                }
            )
            prompt.authenticate(
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(context.getString(R.string.auth_biometria_titolo))
                    .setSubtitle(context.getString(R.string.auth_biometria_sottotitolo))
                    .setNegativeButtonText(context.getString(R.string.auth_biometria_annulla))
                    .build()
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Sfondo)
            .padding(horizontal = 32.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Marchio()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = nomeMedico, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = errore ?: stringResource(R.string.auth_sblocco_descrizione),
                style = MaterialTheme.typography.bodyMedium,
                color = if (errore != null) Rosso else TestoTenue,
                textAlign = TextAlign.Center
            )
        }
        IndicatorePin(pin.length, AuthViewModel.LUNGHEZZA_PIN)
        TastierinoPin(
            onCifra = { cifra ->
                if (pin.length < AuthViewModel.LUNGHEZZA_PIN) {
                    errore = null
                    pin += cifra
                }
            },
            onCancella = { pin = pin.dropLast(1) },
            onInvio = {
                if (pin.length == AuthViewModel.LUNGHEZZA_PIN && !viewModel.verificaPin(pin)) {
                    errore = messaggioErrore
                    pin = ""
                }
            }
        )
        if (biometriaAttiva && biometriaDisponibile(context)) {
            Row(
                modifier = Modifier
                    .height(46.dp)
                    .background(IndacoTenue, RoundedCornerShape(15.dp))
                    .border(1.dp, IndacoBordo, RoundedCornerShape(15.dp))
                    .clickable { avviaBiometria() }
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(2.dp, Indaco, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(7.dp).border(2.dp, Indaco, CircleShape))
                }
                Text(
                    text = stringResource(R.string.auth_azione_biometria),
                    style = MaterialTheme.typography.titleSmall,
                    color = Indaco
                )
            }
        }
        Text(
            text = stringResource(R.string.auth_nota_cifratura),
            style = MaterialTheme.typography.bodySmall,
            color = TestoDebole
        )
    }
}

fun biometriaDisponibile(context: android.content.Context): Boolean =
    BiometricManager.from(context)
        .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
        BiometricManager.BIOMETRIC_SUCCESS
