package com.casati.dermcalc.ui.screens.calcolatore

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.domain.ContributoDistretto
import com.casati.dermcalc.domain.Indice
import com.casati.dermcalc.domain.ParametroClinico
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.ui.components.BadgeSeverita
import com.casati.dermcalc.ui.components.BarraSeverita
import com.casati.dermcalc.ui.components.CartaBase
import com.casati.dermcalc.ui.components.ChipSelezione
import com.casati.dermcalc.ui.components.Pannello
import com.casati.dermcalc.ui.components.PannelloInferiore
import com.casati.dermcalc.ui.components.Pastiglia
import com.casati.dermcalc.ui.components.PiedeAzione
import com.casati.dermcalc.ui.components.PulsanteAzione
import com.casati.dermcalc.ui.components.PulsanteCompatto
import com.casati.dermcalc.ui.components.PulsanteIndietro
import com.casati.dermcalc.ui.components.SelettoreSegmentato
import com.casati.dermcalc.ui.shared.RigaPazienteCollegato
import com.casati.dermcalc.ui.shared.SelezionePazienteViewModel
import com.casati.dermcalc.ui.shared.etichettaUltimaMisurazione
import com.casati.dermcalc.ui.shared.pannelloSelezionePaziente
import com.casati.dermcalc.ui.theme.Bordo
import com.casati.dermcalc.ui.theme.BordoForte
import com.casati.dermcalc.ui.theme.BordoTenue
import com.casati.dermcalc.ui.theme.GrigioPieno
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.Inchiostro
import com.casati.dermcalc.ui.theme.MonoMedio
import com.casati.dermcalc.ui.theme.MonoMinuto
import com.casati.dermcalc.ui.theme.MonoPiccolo
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.SuperficieCalda
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoLeggero
import com.casati.dermcalc.ui.theme.TestoMedio
import com.casati.dermcalc.ui.theme.TestoSpento

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalcolatoreScreen(
    viewModel: CalcolatoreViewModel,
    selezioneViewModel: SelezionePazienteViewModel,
    onIndietro: () -> Unit,
    onVediRisultato: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stato by viewModel.uiState.collectAsState()
    val pazienti by selezioneViewModel.pazienti.collectAsState()
    val pazienteCollegatoId by viewModel.pazienteCollegatoId.collectAsState()
    var pannello by remember { mutableStateOf<Pannello?>(null) }

    val parametri = Indice.parametri(stato.tipo)
    val banda = Severita.banda(stato.tipo, stato.totale)
    val colore by animateColorAsState(banda.colore, tween(300), label = "severita")

    val titoloSelettore = stringResource(R.string.selettore_titolo)
    val etichettaAnonimo = stringResource(R.string.selettore_anonimo)
    val descrizioneAnonimo = stringResource(R.string.selettore_anonimo_descrizione)
    val segnapostoRicerca = stringResource(R.string.pazienti_cerca)
    val formatoMeta = stringResource(R.string.pazienti_meta_formato)

    Column(modifier = modifier.fillMaxSize().background(Sfondo)) {

        // Intestazione fissa: totale e severità restano leggibili mentre si scorre
        // l'elenco dei distretti, senza dover tornare in cima per vedere il risultato.
        Column(modifier = Modifier.fillMaxWidth().background(Sfondo)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PulsanteIndietro(onClick = onIndietro)
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stato.tipo.name, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = Indice.nomeEsteso(stato.tipo),
                        style = MaterialTheme.typography.bodySmall,
                        color = TestoDebole,
                        maxLines = 1
                    )
                }
                PulsanteCompatto(
                    testo = stringResource(R.string.azione_azzera),
                    onClick = viewModel::azzera
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = String.format("%.1f", stato.totale),
                        style = MaterialTheme.typography.displaySmall,
                        color = colore
                    )
                    Text(
                        text = stringResource(R.string.calcolatore_su_totale),
                        style = MonoPiccolo,
                        color = TestoLeggero,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                BadgeSeverita(testo = banda.etichetta, colore = banda.colore, sfondo = banda.tinta)
            }
            BarraSeverita(
                punteggio = stato.totale,
                bande = Severita.bande(stato.tipo),
                colore = colore,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 12.dp)
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BordoForte))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            val ultima = selezioneViewModel.ultimaDelTipo(pazienteCollegatoId, stato.tipo)
            RigaPazienteCollegato(
                pazienti = pazienti,
                pazienteCollegatoId = pazienteCollegatoId,
                valoreDestra = etichettaUltimaMisurazione(stato.tipo, ultima?.risultato),
                onApriSelettore = {
                    pannello = pannelloSelezionePaziente(
                        pazienti = pazienti,
                        pazienteCollegatoId = pazienteCollegatoId,
                        titolo = titoloSelettore,
                        etichettaAnonimo = etichettaAnonimo,
                        descrizioneAnonimo = descrizioneAnonimo,
                        segnapostoRicerca = segnapostoRicerca,
                        etaFormatter = { eta, sesso -> String.format(formatoMeta, eta, sesso) },
                        onSeleziona = { id ->
                            viewModel.collegaPaziente(id)
                            pannello = null
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))
            stato.contributi.forEach { contributo ->
                CartaDistretto(
                    contributo = contributo,
                    parametri = parametri,
                    onParametroChange = { indice, valore ->
                        viewModel.onParametroChange(contributo.distretto, indice, valore)
                    },
                    onAreaChange = { valore -> viewModel.onAreaChange(contributo.distretto, valore) },
                    onInfoParametro = { parametro ->
                        pannello = Pannello.Informazione(
                            titolo = parametro.etichetta,
                            nomeInglese = parametro.nomeInglese,
                            corpo = parametro.spiegazione,
                            scala = parametro.scala
                        )
                    },
                    onInfoArea = {
                        pannello = Pannello.Informazione(
                            titolo = "Area coinvolta",
                            nomeInglese = "A · 0–6",
                            corpo = Indice.SPIEGAZIONE_AREA,
                            scala = Indice.SCALA_AREA
                        )
                    },
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        PiedeAzione {
            PulsanteAzione(
                testo = stringResource(R.string.azione_vedi_risultato),
                onClick = onVediRisultato
            )
        }
    }

    pannello?.let { corrente ->
        PannelloInferiore(pannello = corrente, onChiudi = { pannello = null })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CartaDistretto(
    contributo: ContributoDistretto,
    parametri: List<ParametroClinico>,
    onParametroChange: (Int, Int) -> Unit,
    onAreaChange: (Int) -> Unit,
    onInfoParametro: (ParametroClinico) -> Unit,
    onInfoArea: () -> Unit,
    modifier: Modifier = Modifier
) {
    val attivo = contributo.subtotale > 0
    CartaBase(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 14.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Pastiglia(
                testo = contributo.distretto.sigla,
                sfondo = if (attivo) IndacoTenue else BordoTenue,
                colore = if (attivo) Indaco else TestoLeggero,
                lato = 30,
                raggio = 9
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contributo.distretto.etichetta,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(
                        R.string.calcolatore_peso_formato,
                        String.format("%.1f", contributo.distretto.peso)
                    ),
                    style = MonoMinuto,
                    color = TestoLeggero
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.2f", contributo.subtotale),
                    style = MonoMedio,
                    color = if (attivo) Inchiostro else TestoSpento
                )
                Text(
                    text = stringResource(R.string.calcolatore_subtotale),
                    style = MaterialTheme.typography.bodySmall,
                    color = TestoLeggero
                )
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BordoTenue))

        Column(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            parametri.forEachIndexed { indice, parametro ->
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(text = parametro.etichetta, style = MaterialTheme.typography.titleSmall)
                        BottoneInfo { onInfoParametro(parametro) }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = stringResource(
                                R.string.calcolatore_parametro_valore_formato,
                                contributo.valori.parametri[indice],
                                parametro.massimo
                            ),
                            style = MonoPiccolo,
                            color = TestoLeggero
                        )
                    }
                    SelettoreSegmentato(
                        valore = contributo.valori.parametri[indice],
                        massimo = parametro.massimo,
                        onValoreChange = { onParametroChange(indice, it) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BordoTenue))

            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = stringResource(R.string.calcolatore_area_titolo),
                        style = MaterialTheme.typography.titleSmall
                    )
                    BottoneInfo(onClick = onInfoArea)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(
                            R.string.calcolatore_area_valore_formato,
                            contributo.valori.area
                        ),
                        style = MonoPiccolo,
                        color = TestoLeggero
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Indice.ETICHETTE_AREA.forEachIndexed { valore, etichetta ->
                        ChipSelezione(
                            testo = etichetta,
                            selezionato = contributo.valori.area == valore,
                            onClick = { onAreaChange(valore) }
                        )
                    }
                }
            }
        }
    }
}

// Pallino informativo accanto a ogni parametro: apre la guida clinica senza
// occupare spazio nella riga.
@Composable
private fun BottoneInfo(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(17.dp)
            .clip(CircleShape)
            .border(1.dp, GrigioPieno, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "i", style = MonoMinuto, color = TestoDebole)
    }
}
