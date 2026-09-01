package com.casati.dermcalc.ui.screens.pazienti

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.domain.calcolaEta
import com.casati.dermcalc.domain.iniziali
import com.casati.dermcalc.domain.nomeCompleto
import com.casati.dermcalc.domain.variazione
import com.casati.dermcalc.ui.components.CampoRicerca
import com.casati.dermcalc.ui.components.ChipVariazione
import com.casati.dermcalc.ui.components.Pastiglia
import com.casati.dermcalc.ui.components.PulsanteAzione
import com.casati.dermcalc.ui.shared.PazienteSelezionabile
import com.casati.dermcalc.ui.shared.SelezionePazienteViewModel
import com.casati.dermcalc.ui.theme.Bordo
import com.casati.dermcalc.ui.theme.BordoTenue
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.MonoMedio
import com.casati.dermcalc.ui.theme.MonoMinuto
import com.casati.dermcalc.ui.theme.MonoPiccolo
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.SuperficieCalda
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoLeggero
import com.casati.dermcalc.ui.theme.TestoMedio
import com.casati.dermcalc.ui.theme.TestoSpento
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import com.casati.dermcalc.ui.theme.Inchiostro
import com.casati.dermcalc.ui.theme.Jakarta

@Composable
fun PazienteListScreen(
    viewModel: PazienteViewModel,
    selezioneViewModel: SelezionePazienteViewModel,
    pazienteCollegatoId: Long?,
    onPazienteClick: (Long) -> Unit,
    onNuovoPaziente: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pazienti by selezioneViewModel.pazienti.collectAsState()
    val ricerca by viewModel.ricerca.collectAsState()
    val filtrati = pazienti.filter { voce ->
        ricerca.isBlank() || voce.paziente.nomeCompleto.contains(ricerca.trim(), ignoreCase = true)
    }

    Column(modifier = modifier.fillMaxSize().background(Sfondo)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.titolo_pazienti),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = pluralStringResource(
                    R.plurals.pazienti_conteggio,
                    pazienti.size,
                    pazienti.size
                ),
                style = MonoPiccolo,
                color = TestoDebole
            )
        }

        CampoRicerca(
            valore = ricerca,
            onValoreChange = viewModel::onRicercaChange,
            segnaposto = stringResource(R.string.pazienti_cerca),
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 12.dp)
        )

        if (filtrati.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SuperficieCalda),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "◍", style = MonoMedio, color = TestoSpento)
                }
                Text(
                    text = stringResource(
                        if (pazienti.isEmpty()) R.string.pazienti_vuoto_titolo
                        else R.string.pazienti_nessun_risultato
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                if (pazienti.isEmpty()) {
                    Text(
                        text = stringResource(R.string.pazienti_vuoto_descrizione),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TestoDebole,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtrati, key = { it.paziente.id }) { voce ->
                    RigaPaziente(
                        voce = voce,
                        attivo = voce.paziente.id == pazienteCollegatoId,
                        onClick = { onPazienteClick(voce.paziente.id) }
                    )
                }
            }
        }

        PulsanteAzione(
            testo = "+  " + stringResource(R.string.pazienti_azione_nuovo),
            altezza = 50,
            onClick = onNuovoPaziente,
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 12.dp)
        )
    }
}

@Composable
private fun RigaPaziente(
    voce: PazienteSelezionabile,
    attivo: Boolean,
    onClick: () -> Unit
) {
    val ultima = voce.ultimaMisurazione
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Superficie)
            .border(1.dp, Bordo, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Pastiglia(
            testo = voce.paziente.iniziali,
            sfondo = if (attivo) IndacoTenue else SuperficieCalda,
            colore = if (attivo) Indaco else TestoMedio,
            lato = 40,
            raggio = 13,
            stile = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = voce.paziente.nomeCompleto,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(
                    R.string.pazienti_meta_formato,
                    calcolaEta(voce.paziente.dataNascita),
                    voce.paziente.sesso.sigla
                ),
                style = MonoMinuto,
                color = TestoDebole
            )
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = ultima?.tipo?.name.orEmpty(),
                    style = MonoMinuto,
                    color = TestoLeggero
                )
                Text(
                    text = ultima?.let { Severita.formattaValore(it.tipo, it.risultato) } ?: "—",
                    style = MonoMedio,
                    color = ultima?.let { Severita.colorePunteggio(it.tipo, it.risultato) } ?: TestoSpento
                )
            }
            if (ultima != null) {
                val delta = variazione(voce.deltaUltima, ultima.tipo)
                ChipVariazione(delta.etichetta, delta.colore, delta.tinta)
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(BordoTenue)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "nuovo", style = MonoMinuto, color = TestoDebole)
                }
            }
        }
    }
}
