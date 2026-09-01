package com.casati.dermcalc.ui.screens.pazienti

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.domain.calcolaEta
import com.casati.dermcalc.domain.formattaDataBreve
import com.casati.dermcalc.domain.formattaDataEstesa
import com.casati.dermcalc.domain.iniziali
import com.casati.dermcalc.domain.nomeCompleto
import com.casati.dermcalc.domain.variazione
import com.casati.dermcalc.ui.components.CartaBase
import com.casati.dermcalc.ui.components.ChipSelezione
import com.casati.dermcalc.ui.components.ChipVariazione
import com.casati.dermcalc.ui.components.EtichettaDiSezione
import com.casati.dermcalc.ui.components.IntestazioneSchermata
import com.casati.dermcalc.ui.components.Pannello
import com.casati.dermcalc.ui.components.PannelloInferiore
import com.casati.dermcalc.ui.components.Pastiglia
import com.casati.dermcalc.ui.components.PiedeAzione
import com.casati.dermcalc.ui.components.PulsanteAzione
import com.casati.dermcalc.ui.components.PulsanteSecondario
import com.casati.dermcalc.ui.components.VocePannello
import com.casati.dermcalc.ui.theme.Bordo
import com.casati.dermcalc.ui.theme.BordoTratteggio
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.Inchiostro
import com.casati.dermcalc.ui.theme.MonoMedio
import com.casati.dermcalc.ui.theme.MonoMinuto
import com.casati.dermcalc.ui.theme.MonoPiccolo
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoLeggero
import com.casati.dermcalc.ui.theme.TestoMedio

@Composable
fun PazienteDettaglioScreen(
    viewModel: PazienteDettaglioViewModel,
    onIndietro: () -> Unit,
    onModificaPaziente: (Long) -> Unit,
    onNuovaMisurazione: (Long) -> Unit,
    onImpostaAttivo: (Long) -> Unit,
    onEsportaScheda: (PazienteEntity, List<MisurazioneEntity>) -> Unit,
    onRefertoPdf: (Long) -> Unit,
    onEliminato: (String) -> Unit,
    onMisurazioneEliminata: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stato by viewModel.uiState.collectAsState()
    var pannello by remember { mutableStateOf<Pannello?>(null) }
    val paziente = stato.paziente

    val messaggioEliminato = stringResource(R.string.pazienti_eliminato)
    val messaggioMisurazioneEliminata = stringResource(R.string.misurazione_eliminata)

    Column(modifier = modifier.fillMaxSize().background(Sfondo)) {
        IntestazioneSchermata(
            titolo = stringResource(R.string.titolo_scheda_paziente),
            onIndietro = onIndietro,
            azioni = {
                if (paziente != null) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .clickable {
                                pannello = pannelloAzioniPaziente(
                                    paziente = paziente,
                                    misurazioni = stato.storico.map { it.misurazione },
                                    onImpostaAttivo = {
                                        onImpostaAttivo(paziente.id)
                                        pannello = null
                                    },
                                    onModifica = {
                                        onModificaPaziente(paziente.id)
                                        pannello = null
                                    },
                                    onEsporta = { misurazioni ->
                                        onEsportaScheda(paziente, misurazioni)
                                        pannello = null
                                    },
                                    onElimina = {
                                        pannello = Pannello.Conferma(
                                            titolo = "Eliminare il paziente?",
                                            corpo = "Vengono rimossi ${paziente.nomeCompleto} e tutte le sue " +
                                                "misurazioni. L'operazione non è reversibile.",
                                            etichettaConferma = "Elimina",
                                            distruttiva = true,
                                            onConferma = {
                                                viewModel.eliminaPaziente {
                                                    pannello = null
                                                    onEliminato(messaggioEliminato)
                                                }
                                            }
                                        )
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⋯", style = MaterialTheme.typography.titleLarge, color = TestoMedio)
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (paziente != null) {
                Row(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Pastiglia(
                        testo = paziente.iniziali,
                        sfondo = IndacoTenue,
                        colore = Indaco,
                        lato = 56,
                        raggio = 18,
                        stile = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = paziente.nomeCompleto,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = stringResource(
                                R.string.pazienti_meta_estesa_formato,
                                calcolaEta(paziente.dataNascita),
                                paziente.sesso.sigla,
                                formattaDataBreve(paziente.dataNascita)
                            ),
                            style = MonoMinuto,
                            color = TestoDebole
                        )
                    }
                }

                RiepilogoIndici(
                    storico = stato.storico,
                    modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp)
                )

                CartaBase(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp)) {
                    Column(
                        modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 16.dp, bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.scheda_andamento_titolo),
                                style = MaterialTheme.typography.titleSmall
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                TipoCalcolo.ORDINE_CLINICO.forEach { tipo ->
                                    val presenti = stato.serie?.conteggiPerTipo?.get(tipo) ?: 0
                                    ChipSelezione(
                                        testo = tipo.name,
                                        selezionato = stato.serie?.tipo == tipo,
                                        onClick = { viewModel.selezionaTipoGrafico(tipo) },
                                        altezza = 28,
                                        stile = MonoMinuto,
                                        modifier = Modifier.alpha(if (presenti > 0) 1f else 0.55f)
                                    )
                                }
                            }
                        }
                        stato.serie?.let { GraficoAndamento(serie = it) }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EtichettaDiSezione(stringResource(R.string.scheda_storico_titolo))
                    Text(
                        text = pluralStringResource(
                            R.plurals.scheda_storico_conteggio,
                            stato.storicoFiltrato.size,
                            stato.storicoFiltrato.size
                        ),
                        style = MonoMinuto,
                        color = TestoLeggero
                    )
                }

                // Filtro a scelta multipla: compare solo se c'è più di un indice
                // da distinguere, altrimenti sarebbe una riga inutile.
                if (stato.tipiPresenti.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 18.dp, end = 18.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ChipSelezione(
                            testo = stringResource(R.string.scheda_filtro_tutti),
                            selezionato = !stato.filtroAttivo,
                            onClick = viewModel::azzeraFiltri,
                            altezza = 32,
                            stile = MonoPiccolo
                        )
                        stato.tipiPresenti.forEach { tipo ->
                            ChipSelezione(
                                testo = tipo.name,
                                selezionato = tipo in stato.filtri,
                                onClick = { viewModel.commutaFiltro(tipo) },
                                altezza = 32,
                                stile = MonoPiccolo
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (stato.storicoFiltrato.isEmpty()) {
                        Text(
                            text = stringResource(
                                if (stato.filtroAttivo) R.string.scheda_storico_filtro_vuoto
                                else R.string.scheda_storico_vuoto
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TestoDebole,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Superficie)
                                .border(1.dp, BordoTratteggio, RoundedCornerShape(14.dp))
                                .padding(22.dp)
                        )
                    } else {
                        stato.storicoFiltrato.forEach { voce ->
                            RigaStorico(
                                voce = voce,
                                onAzioni = {
                                    pannello = pannelloAzioniMisurazione(
                                        voce = voce,
                                        nomePaziente = paziente.nome,
                                        onModifica = {
                                            onNuovaMisurazione(paziente.id)
                                            pannello = null
                                        },
                                        onElimina = {
                                            pannello = Pannello.Conferma(
                                                titolo = "Eliminare la misurazione?",
                                                corpo = voce.misurazione.tipo.name + " " +
                                                    Severita.formattaValore(
                                                        voce.misurazione.tipo,
                                                        voce.misurazione.risultato
                                                    ) + " del " +
                                                    formattaDataEstesa(voce.misurazione.data) +
                                                    ". L'operazione non è reversibile.",
                                                etichettaConferma = "Elimina",
                                                distruttiva = true,
                                                onConferma = {
                                                    viewModel.eliminaMisurazione(voce.misurazione)
                                                    pannello = null
                                                    onMisurazioneEliminata(messaggioMisurazioneEliminata)
                                                }
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (paziente != null) {
            PiedeAzione {
                PulsanteSecondario(
                    testo = stringResource(R.string.azione_referto_pdf),
                    onClick = { onRefertoPdf(paziente.id) },
                    modifier = Modifier.weight(1f)
                )
                Box(modifier = Modifier.weight(1.4f)) {
                    PulsanteAzione(
                        testo = stringResource(R.string.azione_nuova_misurazione),
                        altezza = 52,
                        onClick = { onNuovaMisurazione(paziente.id) }
                    )
                }
            }
        }
    }

    pannello?.let { corrente ->
        PannelloInferiore(pannello = corrente, onChiudi = { pannello = null })
    }
}

// Tre riquadri con l'ultimo valore per indice: il colpo d'occhio che serve
// prima di entrare nel dettaglio dello storico.
@Composable
private fun RiepilogoIndici(storico: List<VoceStorico>, modifier: Modifier = Modifier) {
    val perTipo = TipoCalcolo.ORDINE_CLINICO.mapNotNull { tipo ->
        val voci = storico.filter { it.misurazione.tipo == tipo }
        val ultima = voci.firstOrNull() ?: return@mapNotNull null
        Triple(tipo, ultima.misurazione, ultima.delta)
    }.take(3)

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        perTipo.forEach { (tipo, misurazione, delta) ->
            val variazione = variazione(delta, tipo)
            CartaBase(modifier = Modifier.weight(1f), raggio = 14) {
                Column(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(text = tipo.name, style = MonoMinuto, color = TestoLeggero)
                    Text(
                        text = Severita.formattaValore(tipo, misurazione.risultato),
                        style = MonoMedio.copy(fontSize = MaterialTheme.typography.headlineMedium.fontSize),
                        color = Severita.colorePunteggio(tipo, misurazione.risultato)
                    )
                    Text(
                        text = variazione.etichetta,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = variazione.colore
                    )
                }
            }
        }
        if (perTipo.size < 3) {
            CartaBase(modifier = Modifier.weight(1f), raggio = 14) {
                Column(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.scheda_stat_misure),
                        style = MonoMinuto,
                        color = TestoLeggero
                    )
                    Text(
                        text = storico.size.toString(),
                        style = MonoMedio.copy(fontSize = MaterialTheme.typography.headlineMedium.fontSize),
                        color = Inchiostro
                    )
                    Text(
                        text = stringResource(R.string.scheda_stat_totale),
                        style = MaterialTheme.typography.bodySmall,
                        color = TestoDebole
                    )
                }
            }
        }
    }
}

@Composable
private fun RigaStorico(voce: VoceStorico, onAzioni: () -> Unit) {
    val tipo = voce.misurazione.tipo
    val delta = variazione(voce.delta, tipo)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Superficie)
            .border(1.dp, Bordo, RoundedCornerShape(14.dp))
            .padding(start = 14.dp, end = 8.dp, top = 11.dp, bottom = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Pastiglia(
            testo = tipo.name,
            sfondo = Severita.tintaPunteggio(tipo, voce.misurazione.risultato),
            colore = Severita.colorePunteggio(tipo, voce.misurazione.risultato),
            lato = 38,
            raggio = 11,
            stile = MonoMinuto
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formattaDataEstesa(voce.misurazione.data),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = voce.precedente?.let {
                    stringResource(
                        R.string.scheda_confronto_formato,
                        tipo.name,
                        Severita.formattaValore(tipo, it.risultato),
                        formattaDataBreve(it.data)
                    )
                } ?: stringResource(R.string.scheda_nessun_confronto),
                style = MonoMinuto,
                color = TestoDebole
            )
        }
        Text(
            text = Severita.formattaValore(tipo, voce.misurazione.risultato),
            style = MonoMedio,
            color = Severita.colorePunteggio(tipo, voce.misurazione.risultato)
        )
        ChipVariazione(delta.etichetta, delta.colore, delta.tinta, modifier = Modifier.width(44.dp))
        Box(
            modifier = Modifier
                .size(width = 30.dp, height = 34.dp)
                .clip(RoundedCornerShape(9.dp))
                .clickable(onClick = onAzioni),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⋯", style = MaterialTheme.typography.titleMedium, color = TestoLeggero)
        }
    }
}

private fun pannelloAzioniPaziente(
    paziente: PazienteEntity,
    misurazioni: List<MisurazioneEntity>,
    onImpostaAttivo: () -> Unit,
    onModifica: () -> Unit,
    onEsporta: (List<MisurazioneEntity>) -> Unit,
    onElimina: () -> Unit
): Pannello.Elenco = Pannello.Elenco(
    titolo = paziente.nomeCompleto,
    voci = listOf(
        VocePannello(
            etichetta = "Imposta come paziente attivo",
            sottotitolo = "I nuovi calcoli si salvano qui",
            glifo = "✓",
            onClick = onImpostaAttivo
        ),
        VocePannello(
            etichetta = "Modifica anagrafica",
            sottotitolo = "Correggi nome, sesso e data di nascita",
            glifo = "✎",
            onClick = onModifica
        ),
        VocePannello(
            etichetta = "Esporta scheda",
            sottotitolo = "File JSON con anagrafica e misurazioni",
            glifo = "↥",
            onClick = { onEsporta(misurazioni) }
        ),
        VocePannello(
            etichetta = "Elimina paziente",
            sottotitolo = "Rimuove il paziente e il suo storico",
            glifo = "⌫",
            distruttiva = true,
            onClick = onElimina
        )
    )
)

private fun pannelloAzioniMisurazione(
    voce: VoceStorico,
    nomePaziente: String,
    onModifica: () -> Unit,
    onElimina: () -> Unit
): Pannello.Elenco = Pannello.Elenco(
    titolo = voce.misurazione.tipo.name + " " +
        Severita.formattaValore(voce.misurazione.tipo, voce.misurazione.risultato),
    voci = listOf(
        VocePannello(
            etichetta = "Nuova misurazione",
            sottotitolo = "Riapre il calcolatore su questo paziente",
            glifo = "✎",
            onClick = onModifica
        ),
        VocePannello(
            etichetta = "Elimina misurazione",
            sottotitolo = "Rimuove il dato dal diario di $nomePaziente",
            glifo = "⌫",
            distruttiva = true,
            onClick = onElimina
        )
    )
)
