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
import com.casati.dermcalc.ui.components.ConfermaDialog
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PazienteListScreen(
    modifier: Modifier = Modifier,
    viewModel: PazienteViewModel = viewModel(
        factory = PazienteViewModel.Factory(
            (LocalContext.current.applicationContext as DermCalcApplication).pazienteRepository
        )
    )
) {
    val pazienti by viewModel.pazienti.collectAsState()
    var mostraDialogAggiungi by remember { mutableStateOf(false) }
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
                        onEliminaClick = { pazienteDaEliminare = paziente }
                    )
                }
            }
        }
    }

    if (mostraDialogAggiungi) {
        AggiungiPazienteDialog(
            onConferma = { nome, dataNascita ->
                viewModel.aggiungiPaziente(nome, dataNascita)
                mostraDialogAggiungi = false
            },
            onAnnulla = { mostraDialogAggiungi = false }
        )
    }

    pazienteDaEliminare?.let { paziente ->
        ConfermaDialog(
            titolo = stringResource(R.string.pazienti_dialog_elimina_titolo),
            messaggio = stringResource(R.string.pazienti_dialog_elimina_messaggio, paziente.nome),
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
    onEliminaClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = paziente.nome, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = formattaData(paziente.dataNascita),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            TextButton(onClick = onEliminaClick) {
                Text(stringResource(R.string.azione_elimina))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AggiungiPazienteDialog(
    onConferma: (nome: String, dataNascita: Long) -> Unit,
    onAnnulla: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var mostraDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    AlertDialog(
        onDismissRequest = onAnnulla,
        title = { Text(stringResource(R.string.pazienti_dialog_aggiungi_titolo)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text(stringResource(R.string.pazienti_nome_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
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
            TextButton(
                onClick = { onConferma(nome, dataNascita ?: return@TextButton) },
                enabled = nome.isNotBlank() && dataNascita != null
            ) {
                Text(stringResource(R.string.azione_aggiungi))
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

private fun formattaData(millis: Long): String {
    val formato = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    return formato.format(Date(millis))
}
