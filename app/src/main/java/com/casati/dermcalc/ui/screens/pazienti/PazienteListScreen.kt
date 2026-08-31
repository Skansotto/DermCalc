package com.casati.dermcalc.ui.screens.pazienti

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casati.dermcalc.DermCalcApplication
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.Sesso
import com.casati.dermcalc.ui.components.ConfermaDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PazienteListScreen(
    onPazienteClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PazienteViewModel = viewModel(
        factory = PazienteViewModel.Factory(
            (LocalContext.current.applicationContext as DermCalcApplication).pazienteRepository
        )
    )
) {
    val pazienti by viewModel.pazienti.collectAsState()
    var mostraDialogAggiungi by remember { mutableStateOf(false) }
    var pazienteDaModificare by remember { mutableStateOf<PazienteEntity?>(null) }
    var pazienteDaEliminare by remember { mutableStateOf<PazienteEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(stringResource(R.string.titolo_pazienti)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostraDialogAggiungi = true }) {
                Text("+")
            }
        }
    ) { innerPadding ->
        if (pazienti.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.pazienti_vuoto),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pazienti, key = { it.id }) { paziente ->
                    PazienteRiga(
                        paziente = paziente,
                        onClick = { onPazienteClick(paziente.id) },
                        onModificaClick = { pazienteDaModificare = paziente },
                        onEliminaClick = { pazienteDaEliminare = paziente }
                    )
                }
            }
        }
    }

    if (mostraDialogAggiungi) {
        PazienteFormDialog(
            pazienteIniziale = null,
            onConferma = { nome, cognome, sesso, dataNascita ->
                viewModel.aggiungiPaziente(nome, cognome, sesso, dataNascita)
                mostraDialogAggiungi = false
            },
            onAnnulla = { mostraDialogAggiungi = false }
        )
    }

    pazienteDaModificare?.let { paziente ->
        PazienteFormDialog(
            pazienteIniziale = paziente,
            onConferma = { nome, cognome, sesso, dataNascita ->
                viewModel.aggiornaPaziente(paziente, nome, cognome, sesso, dataNascita)
                pazienteDaModificare = null
            },
            onAnnulla = { pazienteDaModificare = null }
        )
    }

    pazienteDaEliminare?.let { paziente ->
        ConfermaDialog(
            titolo = stringResource(R.string.pazienti_dialog_elimina_titolo),
            messaggio = stringResource(R.string.pazienti_dialog_elimina_messaggio, paziente.nomeCompleto),
            onConferma = {
                viewModel.eliminaPaziente(paziente)
                pazienteDaEliminare = null
            },
            onAnnulla = { pazienteDaEliminare = null }
        )
    }
}

@Composable
private fun PazienteRiga(
    paziente: PazienteEntity,
    onClick: () -> Unit,
    onModificaClick: () -> Unit,
    onEliminaClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = paziente.nomeCompleto, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.pazienti_eta_formato, calcolaEta(paziente.dataNascita)),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row {
                TextButton(onClick = onModificaClick) {
                    Text(stringResource(R.string.azione_modifica))
                }
                TextButton(onClick = onEliminaClick) {
                    Text(stringResource(R.string.azione_elimina))
                }
            }
        }
    }
}

// Dialog condiviso per creare un nuovo paziente o modificarne uno esistente:
// con pazienteIniziale nullo si comporta come form di creazione, altrimenti precompila i campi.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PazienteFormDialog(
    pazienteIniziale: PazienteEntity?,
    onConferma: (nome: String, cognome: String, sesso: Sesso, dataNascita: Long) -> Unit,
    onAnnulla: () -> Unit
) {
    var nome by remember { mutableStateOf(pazienteIniziale?.nome.orEmpty()) }
    var cognome by remember { mutableStateOf(pazienteIniziale?.cognome.orEmpty()) }
    var sesso by remember { mutableStateOf(pazienteIniziale?.sesso) }
    var mostraDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = pazienteIniziale?.dataNascita)

    AlertDialog(
        onDismissRequest = onAnnulla,
        title = {
            Text(
                stringResource(
                    if (pazienteIniziale == null) {
                        R.string.pazienti_dialog_aggiungi_titolo
                    } else {
                        R.string.pazienti_dialog_modifica_titolo
                    }
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text(stringResource(R.string.pazienti_nome_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cognome,
                    onValueChange = { cognome = it },
                    label = { Text(stringResource(R.string.pazienti_cognome_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.pazienti_sesso_label),
                    style = MaterialTheme.typography.labelMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = sesso == Sesso.MASCHIO,
                        onClick = { sesso = Sesso.MASCHIO },
                        label = { Text(stringResource(R.string.pazienti_sesso_maschio)) }
                    )
                    FilterChip(
                        selected = sesso == Sesso.FEMMINA,
                        onClick = { sesso = Sesso.FEMMINA },
                        label = { Text(stringResource(R.string.pazienti_sesso_femmina)) }
                    )
                }
                Text(
                    text = stringResource(R.string.pazienti_data_nascita_label),
                    style = MaterialTheme.typography.labelMedium
                )
                TextButton(onClick = { mostraDatePicker = true }) {
                    Text(
                        datePickerState.selectedDateMillis?.let { formattaData(it) }
                            ?: stringResource(R.string.pazienti_data_non_selezionata)
                    )
                }
            }
        },
        confirmButton = {
            val dataNascita = datePickerState.selectedDateMillis
            val sessoScelto = sesso
            TextButton(
                onClick = {
                    onConferma(nome, cognome, sessoScelto ?: return@TextButton, dataNascita ?: return@TextButton)
                },
                enabled = nome.isNotBlank() && cognome.isNotBlank() && sessoScelto != null && dataNascita != null
            ) {
                Text(
                    stringResource(
                        if (pazienteIniziale == null) R.string.azione_aggiungi else R.string.azione_salva
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onAnnulla) { Text(stringResource(R.string.azione_annulla)) }
        }
    )

    if (mostraDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostraDatePicker = false },
            confirmButton = {
                TextButton(onClick = { mostraDatePicker = false }) {
                    Text(stringResource(R.string.azione_conferma))
                }
            },
            dismissButton = {
                TextButton(onClick = { mostraDatePicker = false }) {
                    Text(stringResource(R.string.azione_annulla))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
