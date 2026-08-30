package com.casati.dermcalc.ui.screens.easi

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.DermCalcApplication
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.components.CalcolatoreScaffold
import com.casati.dermcalc.ui.components.ConfermaPulisciDialog
import com.casati.dermcalc.ui.components.ParametroSlider
import com.casati.dermcalc.ui.components.SelettorePaziente
import com.casati.dermcalc.ui.screens.pazienti.PazienteViewModel
import kotlinx.coroutines.launch

@Composable
fun EasiScreen(
    modifier: Modifier = Modifier,
    viewModel: EasiViewModel = viewModel(
        factory = EasiViewModel.Factory(
            (LocalContext.current.applicationContext as DermCalcApplication).misurazioneRepository
        )
    ),
    pazienteViewModel: PazienteViewModel = viewModel(
        factory = PazienteViewModel.Factory(
            (LocalContext.current.applicationContext as DermCalcApplication).pazienteRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val pazienti by pazienteViewModel.pazienti.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var mostraDialogPulisci by remember { mutableStateOf(false) }
    val messaggioCalcoloCompletato = stringResource(R.string.messaggio_calcolo_completato)
    val messaggioMisurazioneSalvata = stringResource(R.string.messaggio_misurazione_salvata)

    CalcolatoreScaffold(
        titolo = stringResource(R.string.titolo_easi),
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
            SelettorePaziente(
                pazienti = pazienti,
                pazienteSelezionatoId = uiState.pazienteSelezionatoId,
                onPazienteSelezionato = viewModel::onPazienteSelezionatoChange
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(EasiDistretto.entries) { distretto ->
                    val valori = uiState.valori.getValue(distretto)
                    DistrettoCard(
                        distretto = distretto,
                        valori = valori,
                        onEritemaChange = { viewModel.onEritemaChange(distretto, it) },
                        onEdemaPapulazioneChange = { viewModel.onEdemaPapulazioneChange(distretto, it) },
                        onEscoriazioniChange = { viewModel.onEscoriazioniChange(distretto, it) },
                        onLichenificazioneChange = { viewModel.onLichenificazioneChange(distretto, it) },
                        onAreaChange = { viewModel.onAreaChange(distretto, it) }
                    )
                }
            }
            Button(
                onClick = {
                    viewModel.onCalcolaClick()
                    val messaggio = if (viewModel.uiState.value.pazienteSelezionatoId != null) {
                        messaggioMisurazioneSalvata
                    } else {
                        messaggioCalcoloCompletato
                    }
                    scope.launch { snackbarHostState.showSnackbar(messaggio) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.azione_calcola))
            }
            val risultato = uiState.risultato
            if (risultato != null) {
                Text(
                    text = stringResource(R.string.easi_risultato_formato, risultato),
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
    distretto: EasiDistretto,
    valori: EasiDistrettoValori,
    onEritemaChange: (Int) -> Unit,
    onEdemaPapulazioneChange: (Int) -> Unit,
    onEscoriazioniChange: (Int) -> Unit,
    onLichenificazioneChange: (Int) -> Unit,
    onAreaChange: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = stringResource(distretto.labelRes), style = MaterialTheme.typography.titleMedium)
            ParametroSlider(
                stringResource(R.string.param_eritema),
                valori.eritema,
                0..3,
                onEritemaChange,
                spiegazione = stringResource(R.string.spiegazione_eritema)
            )
            ParametroSlider(
                stringResource(R.string.easi_param_edema_papulazione),
                valori.edemaPapulazione,
                0..3,
                onEdemaPapulazioneChange,
                spiegazione = stringResource(R.string.spiegazione_easi_edema_papulazione)
            )
            ParametroSlider(
                stringResource(R.string.easi_param_escoriazioni),
                valori.escoriazioni,
                0..3,
                onEscoriazioniChange,
                spiegazione = stringResource(R.string.spiegazione_easi_escoriazioni)
            )
            ParametroSlider(
                stringResource(R.string.easi_param_lichenificazione),
                valori.lichenificazione,
                0..3,
                onLichenificazioneChange,
                spiegazione = stringResource(R.string.spiegazione_easi_lichenificazione)
            )
            ParametroSlider(
                stringResource(R.string.param_area),
                valori.area,
                0..6,
                onAreaChange,
                spiegazione = stringResource(R.string.spiegazione_area)
            )
        }
    }
}
