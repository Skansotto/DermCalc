package com.casati.dermcalc.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.casati.dermcalc.R

// Dialog di conferma generico, usato ovunque serva far confermare all'utente un'azione distruttiva.
@Composable
fun ConfermaDialog(
    titolo: String,
    messaggio: String,
    onConferma: () -> Unit,
    onAnnulla: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onAnnulla,
        title = { Text(titolo) },
        text = { Text(messaggio) },
        confirmButton = {
            TextButton(onClick = onConferma) { Text(stringResource(R.string.azione_conferma)) }
        },
        dismissButton = {
            TextButton(onClick = onAnnulla) { Text(stringResource(R.string.azione_annulla)) }
        }
    )
}
