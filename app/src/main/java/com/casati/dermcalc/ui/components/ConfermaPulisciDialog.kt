package com.casati.dermcalc.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.casati.dermcalc.R

// Dialog di conferma prima di cancellare i valori inseriti, per evitare perdite accidentali di dati.
@Composable
fun ConfermaPulisciDialog(
    onConferma: () -> Unit,
    onAnnulla: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onAnnulla,
        title = { Text(stringResource(R.string.dialog_pulisci_titolo)) },
        text = { Text(stringResource(R.string.dialog_pulisci_messaggio)) },
        confirmButton = {
            TextButton(onClick = onConferma) { Text(stringResource(R.string.azione_conferma)) }
        },
        dismissButton = {
            TextButton(onClick = onAnnulla) { Text(stringResource(R.string.azione_annulla)) }
        }
    )
}
