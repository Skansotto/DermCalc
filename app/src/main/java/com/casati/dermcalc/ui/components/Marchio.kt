package com.casati.dermcalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.Superficie

// Marchio dell'app: il dermatoscopio, cioè la lente con cui si guarda la lesione
// da vicino. Stesso segno dell'icona sul dispositivo, così le due si riconoscono.
@Composable
fun Marchio(
    modifier: Modifier = Modifier,
    lato: Int = 60,
    raggio: Int = 19,
    sfondo: Color = Indaco,
    colore: Color = Superficie
) {
    Box(
        modifier = modifier
            .size(lato.dp)
            .clip(RoundedCornerShape(raggio.dp))
            .background(sfondo),
        contentAlignment = Alignment.Center
    ) {
        // Il disegno ha un margine proprio dentro il suo riquadro: il fattore tiene
        // conto di quello, così il segno pesa sulla mattonella quanto nel marchio.
        SegnoDermatoscopio(
            colore = colore,
            lato = (lato * 0.82f).toInt()
        )
    }
}

// Solo il segno, senza contenitore: per intestazioni e liste dove il fondo c'è già.
@Composable
fun SegnoDermatoscopio(
    colore: Color = Indaco,
    lato: Int = 24,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(R.drawable.ic_dermatoscopio),
        contentDescription = null,
        colorFilter = ColorFilter.tint(colore),
        modifier = modifier.size(lato.dp)
    )
}
