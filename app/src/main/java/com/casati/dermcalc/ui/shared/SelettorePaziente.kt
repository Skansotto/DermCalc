package com.casati.dermcalc.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.domain.calcolaEta
import com.casati.dermcalc.domain.iniziali
import com.casati.dermcalc.domain.nomeCompleto
import com.casati.dermcalc.ui.components.Pastiglia
import com.casati.dermcalc.ui.components.Pannello
import com.casati.dermcalc.ui.components.VocePannello
import com.casati.dermcalc.ui.theme.Bordo
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.MonoPiccolo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.SuperficieCalda
import com.casati.dermcalc.ui.theme.TestoDebole

// Riga compatta in cima ai calcolatori: mostra a chi verrà attribuita la misurazione
// e apre il selettore. Toccarla è l'unico modo per uscire dalla modalità anonima.
@Composable
fun RigaPazienteCollegato(
    pazienti: List<PazienteSelezionabile>,
    pazienteCollegatoId: Long?,
    onApriSelettore: () -> Unit,
    modifier: Modifier = Modifier,
    valoreDestra: String = ""
) {
    val collegato = pazienti.firstOrNull { it.paziente.id == pazienteCollegatoId }?.paziente
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Superficie, RoundedCornerShape(13.dp))
            .border(1.dp, Bordo, RoundedCornerShape(13.dp))
            .clickable(onClick = onApriSelettore)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Pastiglia(
            testo = collegato?.iniziali ?: "—",
            sfondo = if (collegato != null) IndacoTenue else SuperficieCalda,
            colore = if (collegato != null) Indaco else TestoDebole,
            lato = 28,
            raggio = 9
        )
        Text(
            text = collegato?.nomeCompleto ?: stringResource(R.string.home_senza_paziente),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (valoreDestra.isNotBlank()) {
            Text(text = valoreDestra, style = MonoPiccolo, color = TestoDebole)
        }
        Text(
            text = "▾",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Indaco
        )
    }
}

// Costruisce il pannello di selezione: la prima voce è sempre il calcolo anonimo,
// così restare senza paziente è una scelta esplicita e non un ripiego.
fun pannelloSelezionePaziente(
    pazienti: List<PazienteSelezionabile>,
    pazienteCollegatoId: Long?,
    titolo: String,
    etichettaAnonimo: String,
    descrizioneAnonimo: String,
    segnapostoRicerca: String,
    etaFormatter: (Int, String) -> String,
    onSeleziona: (Long?) -> Unit
): Pannello.Elenco {
    val voci = buildList {
        add(
            VocePannello(
                etichetta = etichettaAnonimo,
                sottotitolo = descrizioneAnonimo,
                glifo = "—",
                selezionata = pazienteCollegatoId == null,
                onClick = { onSeleziona(null) }
            )
        )
        pazienti.forEach { voce ->
            val ultima = voce.ultimaMisurazione
            add(
                VocePannello(
                    etichetta = voce.paziente.nomeCompleto,
                    sottotitolo = etaFormatter(
                        calcolaEta(voce.paziente.dataNascita),
                        voce.paziente.sesso.sigla
                    ),
                    glifo = voce.paziente.iniziali,
                    valoreDestra = ultima?.let {
                        it.tipo.name + " " + Severita.formattaValore(it.tipo, it.risultato)
                    }.orEmpty(),
                    selezionata = voce.paziente.id == pazienteCollegatoId,
                    onClick = { onSeleziona(voce.paziente.id) }
                )
            )
        }
    }
    // La ricerca compare quando c'è almeno un paziente in archivio: senza pazienti
    // il pannello ha solo la voce anonima e il campo non avrebbe nulla da filtrare.
    return Pannello.Elenco(
        titolo = titolo,
        voci = voci,
        segnapostoRicerca = segnapostoRicerca.takeIf { pazienti.isNotEmpty() }
    )
}

fun etichettaUltimaMisurazione(tipo: TipoCalcolo, valore: Double?): String =
    valore?.let { "ultimo " + Severita.formattaValore(tipo, it) }.orEmpty()
