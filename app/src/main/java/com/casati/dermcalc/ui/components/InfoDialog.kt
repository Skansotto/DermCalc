package com.casati.dermcalc.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.casati.dermcalc.R

// Dialog puramente informativo (nessuna azione distruttiva), usato per le guide contestuali.
@Composable
fun InfoDialog(
    titolo: String,
    messaggio: String,
    onChiudi: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onChiudi,
        title = { Text(titolo) },
        text = { Text(messaggio) },
        confirmButton = {
            TextButton(onClick = onChiudi) { Text(stringResource(R.string.azione_chiudi)) }
        }
    )
}
