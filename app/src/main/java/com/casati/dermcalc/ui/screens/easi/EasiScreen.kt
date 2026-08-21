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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.ui.components.ParametroSlider
import com.casati.dermcalc.ui.theme.DermCalcTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasiScreen(
    modifier: Modifier = Modifier,
    viewModel: EasiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("EASI") }) }
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
            // TODO: Implementazione bottone predisposto per il calcolo finale
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcola")
            }
        }
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
            Text(text = distretto.label, style = MaterialTheme.typography.titleMedium)
            ParametroSlider("Eritema", valori.eritema, 0..3, onEritemaChange)
            ParametroSlider("Edema/Papulazione", valori.edemaPapulazione, 0..3, onEdemaPapulazioneChange)
            ParametroSlider("Escoriazioni", valori.escoriazioni, 0..3, onEscoriazioniChange)
            ParametroSlider("Lichenificazione", valori.lichenificazione, 0..3, onLichenificazioneChange)
            ParametroSlider("Area", valori.area, 0..6, onAreaChange)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EasiScreenPreview() {
    DermCalcTheme {
        EasiScreen()
    }
}
