package com.casati.dermcalc.ui.screens.pasi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.components.CalcolatoreScaffold
import com.casati.dermcalc.ui.components.ConfermaPulisciDialog
import com.casati.dermcalc.ui.components.ParametroSlider
import com.casati.dermcalc.ui.theme.DermCalcTheme
import kotlinx.coroutines.launch

private val DISTRETTI_VISIBILI = PasiDistretto.entries

@Composable
fun PasiScreen(
    modifier: Modifier = Modifier,
    viewModel: PasiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var mostraDialogPulisci by remember { mutableStateOf(false) }
    val messaggioCalcoloCompletato = stringResource(R.string.messaggio_calcolo_completato)

    CalcolatoreScaffold(
        titolo = stringResource(R.string.titolo_pasi),
        snackbarHostState = snackbarHostState,
        onPulisciClick = { mostraDialogPulisci = true },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(DISTRETTI_VISIBILI) { distretto ->
                    val valori = uiState.valori.getValue(distretto)
                    DistrettoCard(
                        distretto = distretto,
                        valori = valori,
                        onEritemaChange = { viewModel.onEritemaChange(distretto, it) },
                        onIndurimentoChange = { viewModel.onIndurimentoChange(distretto, it) },
                        onDesquamazioneChange = { viewModel.onDesquamazioneChange(distretto, it) },
                        onAreaChange = { viewModel.onAreaChange(distretto, it) }
                    )
                }
            }
            Button(
                onClick = {
                    viewModel.onCalcolaClick()
                    scope.launch { snackbarHostState.showSnackbar(messaggioCalcoloCompletato) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.azione_calcola))
            }
            val risultato = uiState.risultato
            if (risultato != null) {
                Text(
                    text = stringResource(
                        R.string.pasi_risultato_formato,
                        risultato,
                        uiState.interpretazione.orEmpty()
                    ),
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

@Composable
private fun DistrettoCard(
    distretto: PasiDistretto,
    valori: PasiDistrettoValori,
    onEritemaChange: (Int) -> Unit,
    onIndurimentoChange: (Int) -> Unit,
    onDesquamazioneChange: (Int) -> Unit,
    onAreaChange: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = stringResource(distretto.labelRes), style = MaterialTheme.typography.titleMedium)
            ParametroSlider(stringResource(R.string.param_eritema), valori.eritema, 0..4, onEritemaChange)
            ParametroSlider(stringResource(R.string.pasi_param_indurimento), valori.indurimento, 0..4, onIndurimentoChange)
            ParametroSlider(stringResource(R.string.pasi_param_desquamazione), valori.desquamazione, 0..4, onDesquamazioneChange)
            ParametroSlider(stringResource(R.string.param_area), valori.area, 0..6, onAreaChange)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PasiScreenPreview() {
    DermCalcTheme {
        PasiScreen()
    }
}
