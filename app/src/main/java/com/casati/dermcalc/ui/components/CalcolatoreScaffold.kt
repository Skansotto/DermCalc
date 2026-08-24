package com.casati.dermcalc.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.casati.dermcalc.R

// Scaffold comune a tutte le calcolatrici: TopBar con azione "Pulisci" e host per gli Snackbar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalcolatoreScaffold(
    titolo: String,
    snackbarHostState: SnackbarHostState,
    onPulisciClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(titolo) },
                actions = {
                    TextButton(onClick = onPulisciClick) {
                        Text(stringResource(R.string.azione_pulisci))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = content
    )
}
