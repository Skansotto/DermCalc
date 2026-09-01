package com.casati.dermcalc.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.theme.BordoForte
import com.casati.dermcalc.ui.theme.GrigioPieno
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.Inchiostro
import com.casati.dermcalc.ui.theme.MonoGrande
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.SuperficieCalda
import com.casati.dermcalc.ui.theme.TestoMedio

// Indicatore del PIN: mostra quante cifre sono state inserite senza rivelarle.
@Composable
fun IndicatorePin(cifreInserite: Int, lunghezza: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(14.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(lunghezza) { indice ->
            val pieno = indice < cifreInserite
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (pieno) Indaco else Color.Transparent)
                    .border(1.5.dp, if (pieno) Indaco else GrigioPieno, CircleShape)
            )
        }
    }
}

// Tastierino numerico dedicato: sostituisce la tastiera di sistema, che in ambulatorio
// coprirebbe metà schermo e rallenterebbe lo sblocco.
@Composable
fun TastierinoPin(
    onCifra: (String) -> Unit,
    onCancella: () -> Unit,
    onInvio: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9")).forEach { riga ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                riga.forEach { cifra -> TastoCifra(cifra) { onCifra(cifra) } }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            TastoBase(sfondo = SuperficieCalda, onClick = onCancella) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "⌫", style = MaterialTheme.typography.headlineSmall, color = TestoMedio)
                    Text(
                        text = stringResource(R.string.auth_azione_cancella),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TestoMedio
                    )
                }
            }
            TastoCifra("0") { onCifra("0") }
            TastoBase(sfondo = Indaco, bordo = Indaco, onClick = onInvio) {
                Text(
                    text = stringResource(R.string.auth_azione_invio),
                    style = MaterialTheme.typography.titleMedium,
                    color = Superficie
                )
            }
        }
    }
}

@Composable
private fun TastoCifra(cifra: String, onClick: () -> Unit) {
    TastoBase(sfondo = Superficie, onClick = onClick) {
        Text(text = cifra, style = MonoGrande, color = Inchiostro)
    }
}

@Composable
private fun TastoBase(
    sfondo: Color,
    onClick: () -> Unit,
    bordo: Color = BordoForte,
    contenuto: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .width(72.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(sfondo)
            .border(1.dp, bordo, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        contenuto()
    }
}
