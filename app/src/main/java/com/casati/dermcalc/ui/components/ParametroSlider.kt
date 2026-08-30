package com.casati.dermcalc.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.casati.dermcalc.R

@Composable
fun ParametroSlider(
    etichetta: String,
    valore: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    spiegazione: String? = null
) {
    var mostraInfo by remember { mutableStateOf(false) }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.formato_parametro_slider, etichetta, valore))
            if (spiegazione != null) {
                IconButton(onClick = { mostraInfo = true }) {
                    Text(stringResource(R.string.icona_info))
                }
            }
        }
        Slider(
            value = valore.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1
        )
    }

    if (mostraInfo && spiegazione != null) {
        InfoDialog(
            titolo = etichetta,
            messaggio = spiegazione,
            onChiudi = { mostraInfo = false }
        )
    }
}
