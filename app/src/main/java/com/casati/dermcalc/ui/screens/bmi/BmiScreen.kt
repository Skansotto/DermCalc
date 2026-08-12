package com.casati.dermcalc.ui.screens.bmi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.ui.theme.DermCalcTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiScreen(
    modifier: Modifier = Modifier,
    viewModel: BmiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("BMI") }) }
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
                label = { Text("Peso (kg)") },
                isError = uiState.messaggioErrore != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.altezza,
                onValueChange = viewModel::onAltezzaChange,
                label = { Text("Altezza (m)") },
                isError = uiState.messaggioErrore != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = viewModel::onCalcolaClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcola")
            }
            uiState.messaggioErrore?.let { messaggio ->
                Text(
                    text = messaggio,
                    color = MaterialTheme.colorScheme.error
                )
            }
            uiState.risultato?.let { risultato ->
                Text(
                    text = "BMI: ${String.format(Locale.getDefault(), "%.2f", risultato)}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BmiScreenPreview() {
    DermCalcTheme {
        BmiScreen()
    }
}
