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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.ui.theme.DermCalcTheme
import java.util.Locale

private val DISTRETTI_VISIBILI = PasiDistretto.entries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasiScreen(
    modifier: Modifier = Modifier,
    viewModel: PasiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("PASI") }) }
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
                onClick = viewModel::onCalcolaClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcola")
            }
            if (uiState.risultato != null) {
                Text(
                    text = "PASI: ${String.format(Locale.getDefault(), "%.1f", uiState.risultato)} " +
                        "(${uiState.interpretazione})",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
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
            Text(text = distretto.label, style = MaterialTheme.typography.titleMedium)
            ParametroSlider("Eritema", valori.eritema, 0..4, onEritemaChange)
            ParametroSlider("Indurimento", valori.indurimento, 0..4, onIndurimentoChange)
            ParametroSlider("Desquamazione", valori.desquamazione, 0..4, onDesquamazioneChange)
            ParametroSlider("Area", valori.area, 0..6, onAreaChange)
        }
    }
}

@Composable
private fun ParametroSlider(
    etichetta: String,
    valore: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column {
        Text(text = "$etichetta: $valore")
        Slider(
            value = valore.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasiScreenPreview() {
    DermCalcTheme {
        PasiScreen()
    }
}
