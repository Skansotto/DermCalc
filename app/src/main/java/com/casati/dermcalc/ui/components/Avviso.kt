package com.casati.dermcalc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.ui.theme.Inchiostro
import kotlinx.coroutines.delay

// Conferma testuale che compare in fondo allo schermo dopo un'azione andata a buon fine
// e scompare da sola: non richiede all'utente di chiuderla.
class StatoAvviso {
    val messaggio: MutableState<String> = mutableStateOf("")

    fun mostra(testo: String) {
        messaggio.value = testo
    }
}

@Composable
fun rememberStatoAvviso(): StatoAvviso = remember { StatoAvviso() }

@Composable
fun BoxScope.Avviso(stato: StatoAvviso) {
    val testo = stato.messaggio.value
    LaunchedEffect(testo) {
        if (testo.isNotEmpty()) {
            delay(2400)
            stato.messaggio.value = ""
        }
    }
    AnimatedVisibility(
        visible = testo.isNotEmpty(),
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Inchiostro, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = testo,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = com.casati.dermcalc.ui.theme.Sfondo
            )
        }
    }
}
