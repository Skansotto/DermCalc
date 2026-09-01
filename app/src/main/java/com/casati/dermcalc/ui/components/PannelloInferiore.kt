package com.casati.dermcalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.theme.Bordo
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.Inchiostro
import com.casati.dermcalc.ui.theme.MonoMinuto
import com.casati.dermcalc.ui.theme.MonoPiccolo
import com.casati.dermcalc.ui.theme.Rosso
import com.casati.dermcalc.ui.theme.RossoTenue
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.SuperficieCalda
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoLeggero
import com.casati.dermcalc.ui.theme.TestoMedio

// Voce di un pannello a comparsa: usata sia per scegliere un paziente sia per
// elencare le azioni disponibili su un elemento.
data class VocePannello(
    val etichetta: String,
    val sottotitolo: String,
    val glifo: String,
    val valoreDestra: String = "",
    val distruttiva: Boolean = false,
    val selezionata: Boolean = false,
    val onClick: () -> Unit
)

// Contenuti possibili del pannello inferiore, l'unico contenitore modale del design.
sealed interface Pannello {

    val titolo: String

    data class Informazione(
        override val titolo: String,
        val nomeInglese: String,
        val corpo: String,
        val scala: List<String> = emptyList()
    ) : Pannello

    data class Elenco(
        override val titolo: String,
        val voci: List<VocePannello>,
        // Con molte voci il pannello diventa una lista da scorrere: la ricerca
        // filtra per etichetta senza cambiare il resto del comportamento.
        val segnapostoRicerca: String? = null
    ) : Pannello

    data class Conferma(
        override val titolo: String,
        val corpo: String,
        val etichettaConferma: String,
        val distruttiva: Boolean = false,
        val onConferma: () -> Unit
    ) : Pannello
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PannelloInferiore(
    pannello: Pannello,
    onChiudi: () -> Unit
) {
    val stato = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onChiudi,
        sheetState = stato,
        containerColor = Superficie,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 14.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(com.casati.dermcalc.ui.theme.BordoTratteggio, RoundedCornerShape(2.dp))
                        .padding(horizontal = 18.dp, vertical = 2.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 26.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = pannello.titolo,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Inchiostro,
                    modifier = Modifier.weight(1f, fill = false)
                )
                val sottotitolo = (pannello as? Pannello.Informazione)?.nomeInglese.orEmpty()
                if (sottotitolo.isNotBlank()) {
                    Text(text = sottotitolo, style = MonoPiccolo, color = TestoLeggero)
                }
            }

            when (pannello) {
                is Pannello.Informazione -> ContenutoInformazione(pannello)
                is Pannello.Elenco -> ContenutoElenco(pannello, onChiudi)
                is Pannello.Conferma -> ContenutoConferma(pannello, onChiudi)
            }
        }
    }
}

@Composable
private fun ContenutoInformazione(pannello: Pannello.Informazione) {
    Column(modifier = Modifier.padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (pannello.corpo.isNotBlank()) {
            Text(text = pannello.corpo, style = MaterialTheme.typography.bodyLarge, color = TestoMedio)
        }
        if (pannello.scala.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                pannello.scala.forEachIndexed { valore, descrizione ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Pastiglia(
                            testo = valore.toString(),
                            sfondo = SuperficieCalda,
                            colore = TestoMedio,
                            lato = 24,
                            raggio = 7,
                            stile = MonoMinuto
                        )
                        Text(
                            text = descrizione,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TestoMedio
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContenutoElenco(pannello: Pannello.Elenco, onChiudi: () -> Unit) {
    var ricerca by remember { mutableStateOf("") }
    val voci = if (ricerca.isBlank()) {
        pannello.voci
    } else {
        pannello.voci.filter { it.etichetta.contains(ricerca.trim(), ignoreCase = true) }
    }

    Column(modifier = Modifier.padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        pannello.segnapostoRicerca?.let { segnaposto ->
            CampoRicerca(
                valore = ricerca,
                onValoreChange = { ricerca = it },
                segnaposto = segnaposto,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }
        if (voci.isEmpty()) {
            Text(
                text = stringResource(R.string.pazienti_nessun_risultato),
                style = MaterialTheme.typography.bodyMedium,
                color = TestoDebole,
                modifier = Modifier.padding(vertical = 18.dp)
            )
        }
        voci.forEach { voce ->
            val coloreTesto = if (voce.distruttiva) Rosso else Inchiostro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (voce.selezionata) IndacoTenue else Sfondo,
                        RoundedCornerShape(14.dp)
                    )
                    .border(
                        1.dp,
                        if (voce.selezionata) Indaco else Bordo,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(onClick = voce.onClick)
                    .padding(horizontal = 13.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Pastiglia(
                    testo = voce.glifo,
                    sfondo = when {
                        voce.distruttiva -> RossoTenue
                        voce.selezionata -> Indaco
                        else -> SuperficieCalda
                    },
                    colore = when {
                        voce.distruttiva -> Rosso
                        voce.selezionata -> Superficie
                        else -> TestoMedio
                    }
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = voce.etichetta,
                        style = MaterialTheme.typography.titleMedium,
                        color = coloreTesto,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = voce.sottotitolo,
                        style = MaterialTheme.typography.bodySmall,
                        color = TestoDebole,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val destra = voce.valoreDestra.ifBlank { if (voce.selezionata) "✓" else "" }
                if (destra.isNotBlank()) {
                    Text(text = destra, style = MonoPiccolo, color = coloreTesto)
                }
            }
        }
        PulsanteSecondario(
            testo = "Chiudi",
            onClick = onChiudi,
            altezza = 46,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 9.dp)
        )
    }
}

@Composable
private fun ContenutoConferma(pannello: Pannello.Conferma, onChiudi: () -> Unit) {
    Column(modifier = Modifier.padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(text = pannello.corpo, style = MaterialTheme.typography.bodyLarge, color = TestoMedio)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PulsanteSecondario(
                testo = "Annulla",
                onClick = onChiudi,
                altezza = 48,
                modifier = Modifier.weight(1f)
            )
            Box(modifier = Modifier.weight(1f)) {
                PulsanteConferma(
                    testo = pannello.etichettaConferma,
                    colore = if (pannello.distruttiva) Rosso else Indaco,
                    onClick = pannello.onConferma
                )
            }
        }
    }
}

@Composable
private fun PulsanteConferma(testo: String, colore: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colore, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = testo, style = MaterialTheme.typography.titleMedium, color = Superficie)
    }
}
