package com.casati.dermcalc.ui.screens.bsa

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.domain.formattaPercentuale
import com.casati.dermcalc.domain.nomeCompleto
import com.casati.dermcalc.ui.components.CartaBase
import com.casati.dermcalc.ui.components.ChipSelezione
import com.casati.dermcalc.ui.components.EtichettaDiSezione
import com.casati.dermcalc.ui.components.IntestazioneSchermata
import com.casati.dermcalc.ui.components.Pannello
import com.casati.dermcalc.ui.components.PannelloInferiore
import com.casati.dermcalc.ui.components.PiedeAzione
import com.casati.dermcalc.ui.components.PulsanteAzione
import com.casati.dermcalc.ui.components.PulsanteQuadrato
import com.casati.dermcalc.ui.shared.RigaPazienteCollegato
import com.casati.dermcalc.ui.shared.SelezionePazienteViewModel
import com.casati.dermcalc.ui.shared.pannelloSelezionePaziente
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoBordo
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.Inchiostro
import com.casati.dermcalc.ui.theme.BordoForte
import com.casati.dermcalc.ui.theme.MonoMedio
import com.casati.dermcalc.ui.theme.MonoPiccolo
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.TestoDebole

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BsaScreen(
    viewModel: BsaViewModel,
    selezioneViewModel: SelezionePazienteViewModel,
    onIndietro: () -> Unit,
    onSalvato: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stato by viewModel.uiState.collectAsState()
    val pazienti by selezioneViewModel.pazienti.collectAsState()
    val pazienteCollegatoId by viewModel.pazienteCollegatoId.collectAsState()
    var pannello by remember { mutableStateOf<Pannello?>(null) }
    val contesto = androidx.compose.ui.platform.LocalContext.current

    val titoloSelettore = stringResource(R.string.selettore_titolo)
    val etichettaAnonimo = stringResource(R.string.selettore_anonimo)
    val descrizioneAnonimo = stringResource(R.string.selettore_anonimo_descrizione)
    val segnapostoRicerca = stringResource(R.string.pazienti_cerca)
    val formatoMeta = stringResource(R.string.pazienti_meta_formato)

    val apriSelettore = {
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

    Column(modifier = modifier.fillMaxSize().background(Sfondo)) {
        IntestazioneSchermata(
            titolo = stringResource(R.string.titolo_bsa),
            sottotitolo = com.casati.dermcalc.domain.Indice.nomeEsteso(TipoCalcolo.BSA),
            onIndietro = onIndietro
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            RigaPazienteCollegato(
                pazienti = pazienti,
                pazienteCollegatoId = pazienteCollegatoId,
                onApriSelettore = apriSelettore,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 8.dp)
            )

            val colore by animateColorAsState(stato.colore, tween(300), label = "bsaColore")
            Column(
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp, top = 12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(stato.tinta)
                    .padding(vertical = 24.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = Severita.formattaValore(TipoCalcolo.BSA, stato.totale),
                        style = MaterialTheme.typography.displayMedium,
                        color = colore
                    )
                    Text(
                        text = "%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colore.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colore)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(stato.categoriaRes),
                        style = MaterialTheme.typography.titleSmall,
                        color = Superficie
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EtichettaDiSezione(stringResource(R.string.bsa_sezione_distretti))
                Text(
                    text = stringResource(R.string.azione_azzera),
                    style = MaterialTheme.typography.labelMedium,
                    color = Indaco,
                    modifier = Modifier.clickable { viewModel.azzera() }
                )
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                BsaRegion.entries.forEach { regione ->
                    ChipSelezione(
                        testo = stringResource(regione.labelRes),
                        selezionato = regione in stato.regioniSelezionate,
                        onClick = { viewModel.onRegioneToggle(regione) },
                        altezza = 40,
                        stile = MaterialTheme.typography.labelMedium,
                        suffisso = formattaPercentuale(regione.percentuale)
                    )
                }
            }

            CartaBase(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.bsa_regola_palmo_titolo),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = stringResource(R.string.bsa_regola_palmo_descrizione),
                            style = MaterialTheme.typography.bodySmall,
                            color = TestoDebole
                        )
                    }
                    PulsanteQuadrato("−", viewModel::onPalmiDecrementa, Sfondo, BordoForte, Inchiostro, lato = 40)
                    Text(
                        text = stato.palmi.toString(),
                        style = MonoMedio,
                        color = Inchiostro,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(22.dp)
                    )
                    PulsanteQuadrato("+", viewModel::onPalmiIncrementa, IndacoTenue, IndacoBordo, Indaco, lato = 40)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        val nomePaziente = pazienti.firstOrNull { it.paziente.id == pazienteCollegatoId }
            ?.paziente?.nomeCompleto.orEmpty()
        PiedeAzione {
            PulsanteAzione(
                testo = stringResource(
                    if (pazienteCollegatoId != null) R.string.azione_salva_misurazione
                    else R.string.azione_collega_e_salva
                ),
                onClick = {
                    if (pazienteCollegatoId == null) {
                        apriSelettore()
                    } else {
                        viewModel.salva { pazienteId, valore ->
                            onSalvato(
                                pazienteId,
                                contesto.getString(
                                    R.string.risultato_salvato_formato,
                                    TipoCalcolo.BSA.name,
                                    Severita.formattaValore(TipoCalcolo.BSA, valore) + "%",
                                    nomePaziente
                                )
                            )
                        }
                    }
                }
            )
        }
    }

    pannello?.let { corrente ->
        PannelloInferiore(pannello = corrente, onChiudi = { pannello = null })
    }
}
