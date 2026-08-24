package com.casati.dermcalc.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.casati.dermcalc.R

@Composable
fun ParametroSlider(
    etichetta: String,
    valore: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column {
        Text(text = stringResource(R.string.formato_parametro_slider, etichetta, valore))
        Slider(
            value = valore.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1
        )
    }
}
