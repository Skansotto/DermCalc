package com.casati.dermcalc.ui.screens.bsa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.components.CalcolatoreScaffold
import com.casati.dermcalc.ui.components.ConfermaPulisciDialog
import com.casati.dermcalc.ui.theme.DermCalcTheme

@Composable
fun BsaScreen(
    modifier: Modifier = Modifier,
    viewModel: BsaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostraDialogPulisci by remember { mutableStateOf(false) }

    CalcolatoreScaffold(
        titolo = stringResource(R.string.titolo_bsa),
        snackbarHostState = snackbarHostState,
        onPulisciClick = { mostraDialogPulisci = true },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.bsa_istruzioni),
                style = MaterialTheme.typography.bodyLarge
            )
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(BsaRegion.entries) { regione ->
                    val selezionata = regione in uiState.regioniSelezionate
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = selezionata,
                            onCheckedChange = { checked -> viewModel.onRegioneToggle(regione, checked) }
                        )
                        Text(
                            text = stringResource(
                                R.string.bsa_regione_formato,
                                stringResource(regione.labelRes),
                                regione.percentuale
                            )
                        )
                    }
                }
            }
            if (uiState.regioniSelezionate.isEmpty()) {
                Text(
                    text = stringResource(R.string.bsa_area_vuota),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = stringResource(R.string.bsa_risultato_formato, uiState.totale),
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
private fun BsaScreenPreview() {
    DermCalcTheme {
        BsaScreen()
    }
}
