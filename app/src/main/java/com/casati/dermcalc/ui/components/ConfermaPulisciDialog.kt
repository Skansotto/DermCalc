package com.casati.dermcalc.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

// Dialog di conferma prima di cancellare i valori inseriti, per evitare perdite accidentali di dati.
@Composable
fun ConfermaPulisciDialog(
    onConferma: () -> Unit,
    onAnnulla: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onAnnulla,
        title = { Text("Pulisci i dati") },
        text = { Text("Vuoi cancellare tutti i valori inseriti in questa schermata?") },
        confirmButton = {
            TextButton(onClick = onConferma) { Text("Conferma") }
        },
        dismissButton = {
            TextButton(onClick = onAnnulla) { Text("Annulla") }
        }
    )
}
