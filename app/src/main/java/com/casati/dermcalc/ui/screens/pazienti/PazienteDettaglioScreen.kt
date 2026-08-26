package com.casati.dermcalc.ui.screens.pazienti

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.DermCalcApplication
import com.casati.dermcalc.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PazienteDettaglioScreen(
    pazienteId: Long,
    modifier: Modifier = Modifier,
    viewModel: PazienteDettaglioViewModel = viewModel(
        factory = PazienteDettaglioViewModel.Factory(
            pazienteId,
            (LocalContext.current.applicationContext as DermCalcApplication).pazienteRepository
        )
    )
) {
    val paziente by viewModel.paziente.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.titolo_dettaglio_paziente)) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            paziente?.let { p ->
                Text(text = p.nome, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = stringResource(R.string.pazienti_data_nascita_formato, formattaData(p.dataNascita)),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            HorizontalDivider()
            Text(
                text = stringResource(R.string.dettaglio_storico_titolo),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.dettaglio_storico_vuoto),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
