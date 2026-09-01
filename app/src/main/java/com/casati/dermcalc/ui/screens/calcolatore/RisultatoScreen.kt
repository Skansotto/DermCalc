package com.casati.dermcalc.ui.screens.calcolatore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.domain.nomeCompleto
import com.casati.dermcalc.ui.components.CartaBase
import com.casati.dermcalc.ui.components.EtichettaDiSezione
import com.casati.dermcalc.ui.components.IntestazioneSchermata
import com.casati.dermcalc.ui.components.Pannello
import com.casati.dermcalc.ui.components.PannelloInferiore
import com.casati.dermcalc.ui.components.Pastiglia
import com.casati.dermcalc.ui.components.PiedeAzione
import com.casati.dermcalc.ui.components.PulsanteAzione
import com.casati.dermcalc.ui.components.PulsanteSecondario
import com.casati.dermcalc.ui.shared.SelezionePazienteViewModel
import com.casati.dermcalc.ui.shared.pannelloSelezionePaziente
import com.casati.dermcalc.ui.theme.BordoTenue
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.Inchiostro
import com.casati.dermcalc.ui.theme.MonoMedio
import com.casati.dermcalc.ui.theme.MonoMinuto
import com.casati.dermcalc.ui.theme.PlexMono
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoLeggero
import com.casati.dermcalc.ui.theme.TestoTenue
import com.casati.dermcalc.ui.theme.GrigioPieno

@Composable
fun RisultatoScreen(
    viewModel: CalcolatoreViewModel,
    selezioneViewModel: SelezionePazienteViewModel,
    onIndietro: () -> Unit,
    onChiudi: () -> Unit,
    onSalvato: (Long, String) -> Unit,
    onRefertoPdf: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val stato by viewModel.uiState.collectAsState()
    val pazienti by selezioneViewModel.pazienti.collectAsState()
    val pazienteCollegatoId by viewModel.pazienteCollegatoId.collectAsState()
    var mostraFormula by remember { mutableStateOf(false) }
    var pannello by remember { mutableStateOf<Pannello?>(null) }

    val contesto = androidx.compose.ui.platform.LocalContext.current
    val banda = Severita.banda(stato.tipo, stato.totale)
    val bande = Severita.bande(stato.tipo)
    val contributoMassimo = stato.contributi.maxOfOrNull { it.subtotale }?.coerceAtLeast(0.001) ?: 1.0

    val titoloSelettore = stringResource(R.string.selettore_titolo)
    val etichettaAnonimo = stringResource(R.string.selettore_anonimo)
    val descrizioneAnonimo = stringResource(R.string.selettore_anonimo_descrizione)
    val segnapostoRicerca = stringResource(R.string.pazienti_cerca)
    val formatoMeta = stringResource(R.string.pazienti_meta_formato)

    Column(modifier = modifier.fillMaxSize().background(Sfondo)) {
        IntestazioneSchermata(
            titolo = stringResource(R.string.titolo_risultato_formato, stato.tipo.name),
            onIndietro = onIndietro
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp, top = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(banda.tinta)
                    .padding(top = 26.dp, bottom = 22.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.risultato_totale_formato, stato.tipo.name).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = banda.colore.copy(alpha = 0.75f)
                )
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = String.format("%.1f", stato.totale),
                        style = MaterialTheme.typography.displayLarge,
                        color = banda.colore
                    )
                    Text(
                        text = "/72",
                        style = MonoMedio,
                        color = banda.colore.copy(alpha = 0.55f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(banda.colore)
                        .padding(horizontal = 15.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = stringResource(R.string.risultato_severita_formato, banda.etichetta),
                        style = MaterialTheme.typography.titleMedium,
                        color = Superficie
                    )
                }
            }

            CartaBase(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp)) {
                Column(
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 16.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    com.casati.dermcalc.ui.components.BarraSeverita(
                        punteggio = stato.totale,
                        bande = bande,
                        colore = banda.colore,
                        altezza = 12,
                        raggio = 6
                    )
                    Text(
                        text = stringResource(
                            R.string.risultato_scala_formato,
                            // La soglia dell'assenza di malattia è 0 e non va elencata
                            // fra i confini fra una classe e l'altra.
                            bande.dropLast(1)
                                .filter { it.soglia > 0 }
                                .joinToString(" · ") { kotlin.math.round(it.soglia).toInt().toString() }
                        ),
                        style = MonoMinuto,
                        color = TestoLeggero
                    )
                    Text(
                        text = banda.nota,
                        style = MaterialTheme.typography.bodySmall,
                        color = TestoTenue
                    )
                }
            }

            EtichettaDiSezione(
                testo = stringResource(R.string.risultato_contributo_titolo),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 8.dp)
            )
            Column(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stato.contributi.forEach { contributo ->
                    val attivo = contributo.subtotale > 0
                    CartaBase(raggio = 14) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(9.dp)
                            ) {
                                Pastiglia(
                                    testo = contributo.distretto.sigla,
                                    sfondo = if (attivo) IndacoTenue else BordoTenue,
                                    colore = if (attivo) Indaco else TestoLeggero,
                                    lato = 24,
                                    raggio = 7,
                                    stile = MonoMinuto
                                )
                                Text(
                                    text = contributo.distretto.etichetta,
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = String.format("%.2f", contributo.subtotale),
                                    style = MonoMedio,
                                    color = Inchiostro
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(BordoTenue)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth((contributo.subtotale / contributoMassimo).toFloat())
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Indaco)
                                )
                            }
                            Text(text = contributo.formula, style = MonoMinuto, color = TestoDebole)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp)) {
                CartaBase(raggio = 14, onClick = { mostraFormula = !mostraFormula }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.azione_dettaglio_calcolo),
                            style = MaterialTheme.typography.titleSmall,
                            color = Indaco
                        )
                        Text(
                            text = if (mostraFormula) "▲" else "▼",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TestoDebole
                        )
                    }
                }
                AnimatedVisibility(visible = mostraFormula) {
                    Text(
                        text = stato.dettaglioFormula,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = PlexMono),
                        color = GrigioPieno,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Inchiostro)
                            .padding(15.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        val nomePaziente = pazienti.firstOrNull { it.paziente.id == pazienteCollegatoId }
            ?.paziente?.nomeCompleto.orEmpty()
        PiedeAzione {
            if (pazienteCollegatoId != null) {
                PulsanteSecondario(
                    testo = stringResource(R.string.azione_referto_pdf),
                    onClick = { pazienteCollegatoId?.let(onRefertoPdf) },
                    modifier = Modifier.weight(1f)
                )
                Box(modifier = Modifier.weight(1.4f)) {
                    PulsanteAzione(
                        testo = stringResource(R.string.azione_salva_diario),
                        altezza = 52,
                        onClick = {
                            viewModel.salva { pazienteId, tipo, valore ->
                                onSalvato(
                                    pazienteId,
                                    contesto.getString(
                                        R.string.risultato_salvato_formato,
                                        tipo.name,
                                        Severita.formattaValore(tipo, valore),
                                        nomePaziente
                                    )
                                )
                            }
                        }
                    )
                }
            } else {
                PulsanteSecondario(
                    testo = stringResource(R.string.azione_chiudi),
                    onClick = onChiudi,
                    modifier = Modifier.weight(1f)
                )
                Box(modifier = Modifier.weight(1.4f)) {
                    PulsanteAzione(
                        testo = stringResource(R.string.azione_collega_paziente),
                        altezza = 52,
                        onClick = {
                            pannello = pannelloSelezionePaziente(
                                pazienti = pazienti,
                                pazienteCollegatoId = null,
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
                }
            }
        }
    }

    pannello?.let { corrente ->
        PannelloInferiore(pannello = corrente, onChiudi = { pannello = null })
    }
}
