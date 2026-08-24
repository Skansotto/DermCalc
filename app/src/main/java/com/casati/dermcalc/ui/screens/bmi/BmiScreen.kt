package com.casati.dermcalc.ui.screens.bmi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.components.CalcolatoreScaffold
import com.casati.dermcalc.ui.components.ConfermaPulisciDialog
import com.casati.dermcalc.ui.theme.DermCalcTheme
import kotlinx.coroutines.launch

@Composable
fun BmiScreen(
    modifier: Modifier = Modifier,
    viewModel: BmiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var mostraDialogPulisci by remember { mutableStateOf(false) }

    val messaggioCalcoloCompletato = stringResource(R.string.messaggio_calcolo_completato)

    CalcolatoreScaffold(
        titolo = stringResource(R.string.titolo_bmi),
        snackbarHostState = snackbarHostState,
        onPulisciClick = { mostraDialogPulisci = true },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.peso,
                onValueChange = viewModel::onPesoChange,
                label = { Text(stringResource(R.string.bmi_label_peso)) },
                isError = uiState.messaggioErrore != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.altezza,
                onValueChange = viewModel::onAltezzaChange,
                label = { Text(stringResource(R.string.bmi_label_altezza)) },
                isError = uiState.messaggioErrore != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    viewModel.onCalcolaClick()
                    val messaggio = viewModel.uiState.value.messaggioErrore ?: messaggioCalcoloCompletato
                    scope.launch { snackbarHostState.showSnackbar(messaggio) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.azione_calcola))
            }
            uiState.risultato?.let { risultato ->
                Text(
                    text = stringResource(R.string.bmi_risultato_formato, risultato),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }

    if (mostraDialogPulisci) {
        ConfermaPulisciDialog(
            onConferma = {
                viewModel.reset()
                mostraDialogPulisci = false
            },
            onAnnulla = { mostraDialogPulisci = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BmiScreenPreview() {
    DermCalcTheme {
        BmiScreen()
    }
}
