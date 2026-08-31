package com.casati.dermcalc.ui.screens.pazienti

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.DermCalcApplication
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.ui.components.ConfermaDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PazienteDettaglioScreen(
    pazienteId: Long,
    onPazienteEliminato: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PazienteDettaglioViewModel = viewModel(
        factory = PazienteDettaglioViewModel.Factory(
            pazienteId,
            (LocalContext.current.applicationContext as DermCalcApplication).pazienteRepository,
            (LocalContext.current.applicationContext as DermCalcApplication).misurazioneRepository
        )
    )
) {
    val paziente by viewModel.paziente.collectAsState()
    val misurazioniFiltrate by viewModel.misurazioniFiltrate.collectAsState()
    val filtroStorico by viewModel.filtroStorico.collectAsState()
    val andamento by viewModel.andamento.collectAsState()
    val serieGraficoSelezionata by viewModel.serieGraficoSelezionata.collectAsState()
    val pazienteEliminato by viewModel.pazienteEliminato.collectAsState()
    var mostraDialogModifica by remember { mutableStateOf(false) }
    var mostraDialogElimina by remember { mutableStateOf(false) }
    var misurazioneDaEliminare by remember { mutableStateOf<MisurazioneVoce?>(null) }

    LaunchedEffect(pazienteEliminato) {
        if (pazienteEliminato) {
            onPazienteEliminato()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.titolo_dettaglio_paziente)) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            paziente?.let { p ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = p.nomeCompleto, style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = stringResource(
                                R.string.pazienti_data_nascita_formato,
                                formattaData(p.dataNascita)
                            ),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.pazienti_eta_formato, calcolaEta(p.dataNascita)),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row {
                        TextButton(onClick = { mostraDialogModifica = true }) {
                            Text(stringResource(R.string.azione_modifica))
                        }
                        TextButton(onClick = { mostraDialogElimina = true }) {
                            Text(stringResource(R.string.azione_elimina))
                        }
                    }
                }
            }
            HorizontalDivider()
            Text(
                text = stringResource(R.string.andamento_titolo),
                style = MaterialTheme.typography.titleMedium
            )
            if (andamento.isEmpty()) {
                Text(
                    text = stringResource(R.string.andamento_vuoto),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(andamento, key = { it.tipo }) { serie ->
                        FilterChip(
                            selected = serie.tipo == serieGraficoSelezionata?.tipo,
                            onClick = { viewModel.selezionaGraficoTipo(serie.tipo) },
                            label = { Text(stringResource(serie.tipo.labelRes())) }
                        )
                    }
                }
                serieGraficoSelezionata?.let { serie ->
                    AndamentoChart(
                        serie = serie,
                        colore = when (serie.tipo) {
                            TipoCalcolo.PASI -> MaterialTheme.colorScheme.primary
                            TipoCalcolo.EASI -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                }
            }
            HorizontalDivider()
            Text(
                text = stringResource(R.string.dettaglio_storico_titolo),
                style = MaterialTheme.typography.titleMedium
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = filtroStorico == null,
                        onClick = { viewModel.impostaFiltroStorico(null) },
                        label = { Text(stringResource(R.string.storico_filtro_tutti)) }
                    )
                }
                items(TipoCalcolo.entries.toList()) { tipo ->
                    FilterChip(
                        selected = filtroStorico == tipo,
                        onClick = { viewModel.impostaFiltroStorico(tipo) },
                        label = { Text(stringResource(tipo.labelRes())) }
                    )
                }
            }
            if (misurazioniFiltrate.isEmpty()) {
                Text(
                    text = stringResource(R.string.dettaglio_storico_vuoto),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(misurazioniFiltrate, key = { it.misurazione.id }) { voce ->
                        MisurazioneRiga(
                            voce = voce,
                            onEliminaClick = { misurazioneDaEliminare = voce }
                        )
                    }
                }
            }
        }
    }

    if (mostraDialogModifica) {
        paziente?.let { p ->
            PazienteFormDialog(
                pazienteIniziale = p,
                onConferma = { nome, cognome, sesso, dataNascita ->
                    viewModel.aggiornaPaziente(nome, cognome, sesso, dataNascita)
                    mostraDialogModifica = false
                },
                onAnnulla = { mostraDialogModifica = false }
            )
        }
    }

    if (mostraDialogElimina) {
        paziente?.let { p ->
            ConfermaDialog(
                titolo = stringResource(R.string.pazienti_dialog_elimina_titolo),
                messaggio = stringResource(R.string.pazienti_dialog_elimina_messaggio, p.nomeCompleto),
                onConferma = {
                    viewModel.eliminaPaziente()
                    mostraDialogElimina = false
                },
                onAnnulla = { mostraDialogElimina = false }
            )
        }
    }

    misurazioneDaEliminare?.let { voce ->
        ConfermaDialog(
            titolo = stringResource(R.string.misurazione_dialog_elimina_titolo),
            messaggio = stringResource(
                R.string.misurazione_dialog_elimina_messaggio,
                stringResource(voce.misurazione.tipo.labelRes()),
                formattaData(voce.misurazione.data)
            ),
            onConferma = {
                viewModel.eliminaMisurazione(voce.misurazione)
                misurazioneDaEliminare = null
            },
            onAnnulla = { misurazioneDaEliminare = null }
        )
    }
}

@Composable
private fun MisurazioneRiga(voce: MisurazioneVoce, onEliminaClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.storico_voce_risultato_formato,
                        stringResource(voce.misurazione.tipo.labelRes()),
                        voce.misurazione.risultato
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onEliminaClick) {
                    Text(stringResource(R.string.azione_elimina))
                }
            }
            Text(
                text = stringResource(R.string.storico_voce_data_formato, formattaData(voce.misurazione.data)),
                style = MaterialTheme.typography.bodySmall
            )
            val differenza = voce.differenza
            val dataPrecedente = voce.dataPrecedente
            if (differenza != null && dataPrecedente != null) {
                Text(
                    text = stringResource(
                        R.string.storico_differenza_formato,
                        differenza,
                        formattaData(dataPrecedente)
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun TipoCalcolo.labelRes(): Int = when (this) {
    TipoCalcolo.BMI -> R.string.titolo_bmi
    TipoCalcolo.BSA -> R.string.titolo_bsa
    TipoCalcolo.PASI -> R.string.titolo_pasi
    TipoCalcolo.EASI -> R.string.titolo_easi
}
