package com.casati.dermcalc.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.casati.dermcalc.R

@Composable
fun ConfermaPulisciDialog(
    onConferma: () -> Unit,
    onAnnulla: () -> Unit
) {
    ConfermaDialog(
        titolo = stringResource(R.string.dialog_pulisci_titolo),
        messaggio = stringResource(R.string.dialog_pulisci_messaggio),
        onConferma = onConferma,
        onAnnulla = onAnnulla
    )
}
