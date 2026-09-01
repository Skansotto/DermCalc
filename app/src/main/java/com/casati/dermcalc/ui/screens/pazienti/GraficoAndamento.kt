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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.domain.formattaDataBreve
import com.casati.dermcalc.domain.formattaDataCompatta
import com.casati.dermcalc.ui.theme.Ambra
import com.casati.dermcalc.ui.theme.BordoTenue
import com.casati.dermcalc.ui.theme.GrigioPieno
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.MonoMedio
import com.casati.dermcalc.ui.theme.MonoMinuto
import com.casati.dermcalc.ui.theme.Rosso
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoLeggero
import com.casati.dermcalc.ui.theme.TestoMedio
import com.casati.dermcalc.ui.theme.Verde
import kotlin.math.ceil

// Scala e soglia clinica di riferimento per ciascun indice: la soglia tratteggiata
// è quella che nei protocolli separa il "sotto controllo" dal "da rivedere".
private data class ScalaIndice(val minimo: Double, val massimo: Double, val soglia: Double)

private fun scala(tipo: TipoCalcolo): ScalaIndice = when (tipo) {
    TipoCalcolo.PASI -> ScalaIndice(0.0, 20.0, 10.0)
    TipoCalcolo.EASI -> ScalaIndice(0.0, 24.0, 7.0)
    TipoCalcolo.BMI -> ScalaIndice(15.0, 40.0, 25.0)
    TipoCalcolo.BSA -> ScalaIndice(0.0, 60.0, 10.0)
}

@Composable
fun GraficoAndamento(serie: SerieGrafico, modifier: Modifier = Modifier) {
    if (!serie.haDati) {
        GraficoVuoto(serie, modifier)
        return
    }

    val valori = serie.punti.map { it.risultato }
    val base = scala(serie.tipo)
    val minimo: Double
    val massimo: Double
    if (serie.tipo == TipoCalcolo.BMI) {
        minimo = minOf(base.minimo, kotlin.math.floor(valori.min() - 2))
        massimo = maxOf(base.massimo, ceil(valori.max() + 2))
    } else {
        minimo = base.minimo
        massimo = maxOf(base.massimo, ceil(valori.max() / 4) * 4)
    }
    val intervallo = (massimo - minimo).coerceAtLeast(1.0)

    val ultima = serie.punti.last()
    val prima = serie.punti.first()
    val differenza = ultima.risultato - prima.risultato

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = Severita.formattaValore(serie.tipo, ultima.risultato) + Severita.unita(serie.tipo),
                style = MonoMedio.copy(fontSize = MaterialTheme.typography.headlineMedium.fontSize),
                color = Severita.colorePunteggio(serie.tipo, ultima.risultato)
            )
            Text(
                text = stringResource(
                    R.string.scheda_grafico_trend_formato,
                    (if (differenza > 0) "+" else "") + Severita.formattaValore(serie.tipo, differenza),
                    formattaDataBreve(prima.data)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    differenza < 0 -> Verde
                    differenza > 0 -> Rosso
                    else -> TestoDebole
                },
                modifier = Modifier.weight(1f)
            )
            Text(
                text = pluralStringResource(
                    R.plurals.scheda_grafico_span,
                    serie.punti.size,
                    serie.punti.size
                ),
                style = MonoMinuto,
                color = TestoLeggero
            )
        }

        Row(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            Column(
                modifier = Modifier.width(26.dp).height(120.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(massimo, (massimo + minimo) / 2, minimo).forEach { valore ->
                    Text(
                        text = valore.toInt().toString(),
                        style = MonoMinuto,
                        color = TestoLeggero,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(22.dp)
                    )
                }
            }
            Canvas(modifier = Modifier.weight(1f).height(120.dp)) {
                val altezzaUtile = size.height * 0.86f
                fun y(valore: Double): Float =
                    (altezzaUtile * (1 - ((valore - minimo) / intervallo)).toFloat())
                        .coerceIn(0f, altezzaUtile)

                listOf(0f, altezzaUtile / 2, altezzaUtile).forEach { yLinea ->
                    drawLine(
                        color = BordoTenue,
                        start = Offset(0f, yLinea),
                        end = Offset(size.width, yLinea),
                        strokeWidth = 1f
                    )
                }

                if (base.soglia > minimo && base.soglia < massimo) {
                    drawLine(
                        color = Ambra.copy(alpha = 0.45f),
                        start = Offset(0f, y(base.soglia)),
                        end = Offset(size.width, y(base.soglia)),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 12f))
                    )
                }

                val passo = if (serie.punti.size > 1) size.width / (serie.punti.size - 1) else 0f
                val punti = serie.punti.mapIndexed { indice, misurazione ->
                    Offset(passo * indice, y(misurazione.risultato))
                }
                for (i in 0 until punti.size - 1) {
                    drawLine(
                        color = Indaco,
                        start = punti[i],
                        end = punti[i + 1],
                        strokeWidth = 2.4.dp.toPx() / 2,
                        cap = StrokeCap.Round
                    )
                }
                punti.forEachIndexed { indice, punto ->
                    val ultimo = indice == punti.lastIndex
                    drawCircle(
                        color = if (ultimo) Indaco else Superficie,
                        radius = if (ultimo) 5f else 3.6f * 1.2f,
                        center = punto
                    )
                    drawCircle(
                        color = Indaco,
                        radius = if (ultimo) 5f else 3.6f * 1.2f,
                        center = punto,
                        style = Stroke(width = 2.2f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 26.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            serie.punti.forEach { misurazione ->
                Text(
                    text = formattaDataCompatta(misurazione.data),
                    style = MonoMinuto,
                    color = TestoLeggero
                )
            }
        }
    }
}

@Composable
private fun GraficoVuoto(serie: SerieGrafico, modifier: Modifier = Modifier) {
    val conteggio = serie.conteggiPerTipo[serie.tipo] ?: 0
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 26.dp, bottom = 30.dp, start = 10.dp, end = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Row(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BordoTenue)
                .padding(bottom = 14.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            listOf(12, 20, 8).forEach { altezza ->
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(altezza.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GrigioPieno)
                )
            }
        }
        Text(
            text = stringResource(
                if (conteggio == 1) R.string.scheda_grafico_vuoto_una_formato
                else R.string.scheda_grafico_vuoto_nessuna_formato,
                serie.tipo.name
            ),
            style = MaterialTheme.typography.titleSmall,
            color = TestoMedio,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.scheda_grafico_vuoto_descrizione),
            style = MaterialTheme.typography.bodySmall,
            color = TestoDebole,
            textAlign = TextAlign.Center
        )
    }
}
