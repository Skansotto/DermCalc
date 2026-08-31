package com.casati.dermcalc.ui.screens.pazienti

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.TipoCalcolo

@Composable
fun AndamentoChart(serie: SerieAndamento, colore: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(colore)
            )
            Text(
                text = stringResource(serie.tipo.labelRes()),
                style = MaterialTheme.typography.titleSmall
            )
        }
        val minimo = serie.punti.minOf { it.risultato }
        val massimo = serie.punti.maxOf { it.risultato }
        val range = (massimo - minimo).let { if (it == 0.0) 1.0 else it }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(8.dp)
        ) {
            val larghezza = size.width
            val altezza = size.height
            val passo = if (serie.punti.size > 1) larghezza / (serie.punti.size - 1) else 0f

            val punti = serie.punti.mapIndexed { indice, punto ->
                val x = passo * indice
                val yNormalizzata = ((punto.risultato - minimo) / range).toFloat()
                val y = altezza - (yNormalizzata * altezza)
                Offset(x, y)
            }

            for (i in 0 until punti.size - 1) {
                drawLine(
                    color = colore,
                    start = punti[i],
                    end = punti[i + 1],
                    strokeWidth = 4f
                )
            }
            punti.forEach { offset ->
                drawCircle(color = colore, radius = 6f, center = offset, style = Fill)
            }
        }
    }
}

private fun TipoCalcolo.labelRes(): Int = when (this) {
    TipoCalcolo.BMI -> R.string.titolo_bmi
    TipoCalcolo.BSA -> R.string.titolo_bsa
    TipoCalcolo.PASI -> R.string.titolo_pasi
    TipoCalcolo.EASI -> R.string.titolo_easi
}
