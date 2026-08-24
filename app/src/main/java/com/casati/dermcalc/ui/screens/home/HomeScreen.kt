package com.casati.dermcalc.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.theme.DermCalcTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBmi: () -> Unit,
    onNavigateToBsa: () -> Unit,
    onNavigateToPasi: () -> Unit,
    onNavigateToEasi: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Button(onClick = onNavigateToBmi, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.titolo_bmi))
            }
            Button(onClick = onNavigateToBsa, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.titolo_bsa))
            }
            Button(onClick = onNavigateToPasi, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.titolo_pasi))
            }
            Button(onClick = onNavigateToEasi, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.titolo_easi))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    DermCalcTheme {
        HomeScreen(
            onNavigateToBmi = {},
            onNavigateToBsa = {},
            onNavigateToPasi = {},
            onNavigateToEasi = {}
        )
    }
}
