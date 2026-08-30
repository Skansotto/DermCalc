package com.casati.dermcalc.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R

data class VoceDettaglioCalcolo(
    val etichetta: String,
    val sommaParametri: Int,
    val area: Int,
    val peso: Double,
    val subtotale: Double
)

// Mostra, distretto per distretto, la formula effettivamente applicata (somma parametri × area × peso),
// per rendere trasparente come si arriva al punteggio finale di PASI/EASI.
@Composable
fun DettaglioCalcoloDialog(
    voci: List<VoceDettaglioCalcolo>,
    totale: Double,
    onChiudi: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onChiudi,
        title = { Text(stringResource(R.string.dettaglio_calcolo_titolo)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.dettaglio_calcolo_formula_generale),
                    style = MaterialTheme.typography.bodySmall
                )
                voci.forEach { voce ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = voce.etichetta, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = stringResource(
                                R.string.dettaglio_calcolo_formula_voce,
                                voce.sommaParametri,
                                voce.area,
                                voce.peso,
                                voce.subtotale
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                HorizontalDivider()
                Text(
                    text = stringResource(R.string.dettaglio_calcolo_totale_formato, totale),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onChiudi) { Text(stringResource(R.string.azione_chiudi)) }
        }
    )
}
