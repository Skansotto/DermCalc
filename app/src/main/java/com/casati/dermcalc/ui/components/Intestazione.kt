package com.casati.dermcalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.ui.theme.Bordo
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.TestoDebole

// Intestazione delle schermate di dettaglio: freccia indietro, titolo e sottotitolo.
@Composable
fun IntestazioneSchermata(
    titolo: String,
    onIndietro: () -> Unit,
    modifier: Modifier = Modifier,
    sottotitolo: String? = null,
    azioni: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PulsanteIndietro(onClick = onIndietro)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titolo,
                style = if (sottotitolo != null) {
                    MaterialTheme.typography.headlineSmall
                } else {
                    MaterialTheme.typography.titleLarge
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (sottotitolo != null) {
                Text(
                    text = sottotitolo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TestoDebole,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        azioni()
    }
}

// Piede fisso con l'azione principale della schermata: resta ancorato mentre il
// contenuto scorre, così il pulsante di salvataggio è sempre raggiungibile.
@Composable
fun PiedeAzione(
    modifier: Modifier = Modifier,
    contenuto: @Composable RowScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth().background(Sfondo)) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Bordo))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            content = contenuto
        )
    }
}
