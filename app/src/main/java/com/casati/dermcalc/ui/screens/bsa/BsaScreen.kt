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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.ui.theme.DermCalcTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BsaScreen(
    modifier: Modifier = Modifier,
    viewModel: BsaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("BSA") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Seleziona le aree corporee coinvolte:",
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
                        Text(text = "${regione.label} (${regione.percentuale}%)")
                    }
                }
            }
            if (uiState.regioniSelezionate.isEmpty()) {
                Text(
                    text = "Seleziona almeno un'area per calcolare la BSA.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = "BSA: ${uiState.totale}%",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BsaScreenPreview() {
    DermCalcTheme {
        BsaScreen()
    }
}
