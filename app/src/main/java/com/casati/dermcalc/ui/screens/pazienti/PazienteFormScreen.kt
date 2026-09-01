package com.casati.dermcalc.ui.screens.pazienti

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.Sesso
import com.casati.dermcalc.domain.CIFRE_DATA
import com.casati.dermcalc.domain.cifreDaData
import com.casati.dermcalc.domain.dataDaCifre
import com.casati.dermcalc.domain.soloCifreData
import com.casati.dermcalc.ui.components.AvvisoErrore
import com.casati.dermcalc.ui.components.CampoTesto
import com.casati.dermcalc.ui.components.ChipSelezione
import com.casati.dermcalc.ui.components.EtichettaDiSezione
import com.casati.dermcalc.ui.components.IntestazioneSchermata
import com.casati.dermcalc.ui.components.MascheraData
import com.casati.dermcalc.ui.components.PiedeAzione
import com.casati.dermcalc.ui.components.PulsanteAzione
import com.casati.dermcalc.ui.shared.SelezionePazienteViewModel
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.TestoDebole

// Stessa schermata per creare e modificare: cambiano solo il titolo, i valori
// iniziali e l'azione finale.
@Composable
fun PazienteFormScreen(
    viewModel: PazienteViewModel,
    selezioneViewModel: SelezionePazienteViewModel,
    pazienteEsistente: PazienteEntity?,
    onIndietro: () -> Unit,
    onSalvato: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pazienti by selezioneViewModel.pazienti.collectAsState()
    var nome by remember(pazienteEsistente) { mutableStateOf(pazienteEsistente?.nome.orEmpty()) }
    var cognome by remember(pazienteEsistente) { mutableStateOf(pazienteEsistente?.cognome.orEmpty()) }
    var dataNascita by remember(pazienteEsistente) {
        mutableStateOf(pazienteEsistente?.let { cifreDaData(it.dataNascita) }.orEmpty())
    }
    var sesso by remember(pazienteEsistente) { mutableStateOf(pazienteEsistente?.sesso ?: Sesso.FEMMINA) }
    var errore by remember { mutableStateOf<String?>(null) }

    val erroreNome = stringResource(R.string.pazienti_errore_nome)
    val erroreData = stringResource(R.string.pazienti_errore_data_formato)
    val erroreDuplicato = stringResource(R.string.pazienti_errore_duplicato)
    val messaggioSalvato = stringResource(R.string.pazienti_salvato_formato, "$nome $cognome".trim())
    val messaggioAggiornato = stringResource(R.string.pazienti_aggiornato)

    val campiValidi = nome.isNotBlank() && cognome.isNotBlank() && dataNascita.length == CIFRE_DATA

    Column(modifier = modifier.fillMaxSize().background(Sfondo)) {
        IntestazioneSchermata(
            titolo = stringResource(
                if (pazienteEsistente == null) R.string.titolo_nuovo_paziente
                else R.string.titolo_modifica_paziente
            ),
            onIndietro = onIndietro
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(0.dp))
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                EtichettaDiSezione(stringResource(R.string.pazienti_nome_label))
                CampoTesto(
                    valore = nome,
                    onValoreChange = { nome = it; errore = null },
                    segnaposto = stringResource(R.string.pazienti_segnaposto_nome)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                EtichettaDiSezione(stringResource(R.string.pazienti_cognome_label))
                CampoTesto(
                    valore = cognome,
                    onValoreChange = { cognome = it; errore = null },
                    segnaposto = stringResource(R.string.pazienti_segnaposto_cognome)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                EtichettaDiSezione(stringResource(R.string.pazienti_data_nascita_label))
                CampoTesto(
                    valore = dataNascita,
                    onValoreChange = { dataNascita = soloCifreData(it); errore = null },
                    segnaposto = stringResource(R.string.pazienti_data_nascita_segnaposto),
                    monospaziato = true,
                    tipoTastiera = KeyboardType.Number,
                    trasformazione = MascheraData
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EtichettaDiSezione(stringResource(R.string.pazienti_sesso_label))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Sesso.entries.forEach { opzione ->
                        ChipSelezione(
                            testo = stringResource(opzione.labelRes),
                            selezionato = sesso == opzione,
                            onClick = { sesso = opzione },
                            altezza = 46,
                            stile = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            errore?.let { AvvisoErrore(it) }
            Text(
                text = stringResource(R.string.pazienti_nota_privacy),
                style = MaterialTheme.typography.bodySmall,
                color = TestoDebole
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        PiedeAzione {
            PulsanteAzione(
                testo = stringResource(
                    if (pazienteEsistente == null) R.string.azione_salva else R.string.azione_salva
                ),
                abilitato = campiValidi,
                onClick = {
                    val esistenti = pazienti.map { it.paziente }
                    val millis = dataDaCifre(dataNascita)
                    val esito = if (pazienteEsistente == null) {
                        viewModel.aggiungiPaziente(nome, cognome, sesso, millis, esistenti) {
                            onSalvato(messaggioSalvato)
                        }
                    } else {
                        viewModel.aggiornaPaziente(
                            pazienteEsistente, nome, cognome, sesso, millis, esistenti
                        ) {
                            onSalvato(messaggioAggiornato)
                        }
                    }
                    errore = when (esito) {
                        EsitoSalvataggio.OK -> null
                        EsitoSalvataggio.NOME_MANCANTE -> erroreNome
                        EsitoSalvataggio.DATA_NON_VALIDA -> erroreData
                        EsitoSalvataggio.DUPLICATO -> erroreDuplicato
                    }
                }
            )
        }
    }
}
