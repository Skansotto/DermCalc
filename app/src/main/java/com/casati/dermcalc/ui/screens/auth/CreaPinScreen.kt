package com.casati.dermcalc.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.components.Marchio
import com.casati.dermcalc.ui.components.PulsanteIndietro
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.MonoGrande
import com.casati.dermcalc.ui.theme.Rosso
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.TestoTenue
import kotlinx.coroutines.delay

// Creazione del PIN in due passaggi: prima inserimento, poi conferma. La stessa
// schermata serve anche per cambiarlo dalle impostazioni.
@Composable
fun CreaPinScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    onIndietro: (() -> Unit)? = null,
    onPinImpostato: (() -> Unit)? = null
) {
    var primoPin by remember { mutableStateOf<String?>(null) }
    var pin by remember { mutableStateOf("") }
    var errore by remember { mutableStateOf<String?>(null) }
    val erroreDiverso = stringResource(R.string.auth_errore_diverso)

    LaunchedEffect(pin) {
        if (pin.length != AuthViewModel.LUNGHEZZA_PIN) return@LaunchedEffect
        delay(160)
        val primo = primoPin
        if (primo == null) {
            primoPin = pin
            pin = ""
        } else if (primo == pin) {
            viewModel.impostaPin(pin)
            onPinImpostato?.invoke()
        } else {
            errore = erroreDiverso
            primoPin = null
            pin = ""
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Sfondo)) {
        if (onIndietro != null) {
            PulsanteIndietro(
                onClick = onIndietro,
                modifier = Modifier.padding(start = 14.dp, top = 8.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            Marchio()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.auth_crea_pin_titolo),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = errore ?: stringResource(
                        if (primoPin == null) R.string.auth_crea_pin_descrizione
                        else R.string.auth_conferma_pin_descrizione
                    ),
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
                onInvio = { }
            )
        }
    }
}
