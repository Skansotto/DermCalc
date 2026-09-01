package com.casati.dermcalc.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.ui.theme.BordoForte
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.TestoMedio

enum class SezionePrincipale(val etichetta: String) {
    CALCOLI("Calcoli"),
    PAZIENTI("Pazienti"),
    IMPOSTAZIONI("Impostazioni")
}

// Navigazione fissa in fondo allo schermo: resta ancorata mentre il contenuto scorre.
@Composable
fun BarraNavigazione(
    sezioneAttiva: SezionePrincipale,
    onSezioneClick: (SezionePrincipale) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BordoForte)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Sfondo)
                .padding(start = 10.dp, end = 10.dp, top = 9.dp, bottom = 12.dp)
        ) {
            SezionePrincipale.entries.forEach { sezione ->
                VoceNavigazione(
                    sezione = sezione,
                    attiva = sezione == sezioneAttiva,
                    onClick = { onSezioneClick(sezione) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun VoceNavigazione(
    sezione: SezionePrincipale,
    attiva: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sfondo by animateColorAsState(
        if (attiva) IndacoTenue else Color.Transparent,
        tween(200),
        label = "navBg"
    )
    val colore = if (attiva) Indaco else TestoMedio
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .width(76.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(19.dp))
                .background(sfondo),
            contentAlignment = Alignment.Center
        ) {
            IconaSezione(sezione, colore)
        }
        Text(
            text = sezione.etichetta,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (attiva) FontWeight.Bold else FontWeight.SemiBold
            ),
            color = colore
        )
    }
}

// Le icone sono disegnate a mano per restare coerenti col tratto sottile del design.
@Composable
private fun IconaSezione(sezione: SezionePrincipale, colore: Color) {
    Canvas(modifier = Modifier.size(25.dp)) {
        val u = size.width / 24f
        val tratto = Stroke(width = 1.7f * u, cap = StrokeCap.Round)
        when (sezione) {
            SezionePrincipale.CALCOLI -> {
                drawRoundRect(
                    color = colore,
                    topLeft = Offset(4 * u, 2.5f * u),
                    size = Size(16 * u, 19 * u),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.2f * u),
                    style = tratto
                )
                drawRoundRect(
                    color = colore,
                    topLeft = Offset(7.3f * u, 5.8f * u),
                    size = Size(9.4f * u, 3.4f * u),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.1f * u),
                    style = tratto
                )
                listOf(13.4f, 17.4f).forEach { y ->
                    listOf(8.2f, 12f, 15.8f).forEach { x ->
                        drawCircle(colore, radius = 1.35f * u, center = Offset(x * u, y * u))
                    }
                }
            }

            SezionePrincipale.PAZIENTI -> {
                drawCircle(
                    color = colore,
                    radius = 3.5f * u,
                    center = Offset(9.3f * u, 8.4f * u),
                    style = tratto
                )
                drawArc(
                    color = colore,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(3.2f * u, 14.3f * u),
                    size = Size(12.2f * u, 10.6f * u),
                    style = tratto
                )
                drawArc(
                    color = colore,
                    startAngle = -70f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(14.6f * u, 6.4f * u),
                    size = Size(4f * u, 5.6f * u),
                    style = tratto
                )
            }

            SezionePrincipale.IMPOSTAZIONI -> {
                drawLine(colore, Offset(3.6f * u, 7.8f * u), Offset(20.4f * u, 7.8f * u), 1.7f * u, StrokeCap.Round)
                drawLine(colore, Offset(3.6f * u, 16.2f * u), Offset(20.4f * u, 16.2f * u), 1.7f * u, StrokeCap.Round)
                drawCircle(Sfondo, radius = 2.7f * u, center = Offset(9.4f * u, 7.8f * u))
                drawCircle(colore, radius = 2.7f * u, center = Offset(9.4f * u, 7.8f * u), style = tratto)
                drawCircle(Sfondo, radius = 2.7f * u, center = Offset(15f * u, 16.2f * u))
                drawCircle(colore, radius = 2.7f * u, center = Offset(15f * u, 16.2f * u), style = tratto)
            }
        }
    }
}
