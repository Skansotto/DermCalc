package com.casati.dermcalc.ui.screens.impostazioni

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.ProfiloMedico
import com.casati.dermcalc.domain.formattaDataFile
import com.casati.dermcalc.ui.components.CartaBase
import com.casati.dermcalc.ui.components.EtichettaDiSezione
import com.casati.dermcalc.ui.components.Interruttore
import com.casati.dermcalc.ui.components.NotaInformativa
import com.casati.dermcalc.ui.components.Pannello
import com.casati.dermcalc.ui.components.PannelloInferiore
import com.casati.dermcalc.ui.components.Pastiglia
import com.casati.dermcalc.ui.components.PulsanteCompatto
import com.casati.dermcalc.ui.components.RigaElenco
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.Rosso
import com.casati.dermcalc.ui.theme.RossoTenue
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoSpento

@Composable
fun ImpostazioniScreen(
    viewModel: ImpostazioniViewModel,
    profilo: ProfiloMedico?,
    onModificaProfilo: () -> Unit,
    onCambiaPin: () -> Unit,
    onAvviso: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stato by viewModel.uiState.collectAsState()
    val biometriaAttiva by viewModel.biometriaAttiva.collectAsState()
    var pannello by remember { mutableStateOf<Pannello?>(null) }
    val context = LocalContext.current

    val nomeFileBackup = stringResource(
        R.string.file_backup_formato,
        formattaDataFile(System.currentTimeMillis())
    )
    val messaggioEsportato = stringResource(R.string.impostazioni_esportato_formato, nomeFileBackup)
    val messaggioEsportazioneAnnullata = stringResource(R.string.impostazioni_esportazione_annullata)
    val messaggioImportazioneAnnullata = stringResource(R.string.impostazioni_importazione_annullata)
    val messaggioImportazioneFallita = stringResource(R.string.impostazioni_importazione_fallita)
    val messaggioArchivioSvuotato = stringResource(R.string.impostazioni_archivio_svuotato)

    // L'utente sceglie dove salvare il backup con il selettore di sistema:
    // il file resta fuori dall'app e sotto il suo controllo.
    var contenutoDaEsportare by remember { mutableStateOf<String?>(null) }
    val selettoreSalvataggio = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val contenuto = contenutoDaEsportare
        if (uri != null && contenuto != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { it.write(contenuto.toByteArray()) }
            }
            onAvviso(messaggioEsportato)
        } else {
            onAvviso(messaggioEsportazioneAnnullata)
        }
        contenutoDaEsportare = null
    }

    val selettoreApertura = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            onAvviso(messaggioImportazioneAnnullata)
            return@rememberLauncherForActivityResult
        }
        val contenuto = runCatching {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
        }.getOrNull()
        if (contenuto == null) {
            onAvviso(messaggioImportazioneFallita)
        } else {
            viewModel.importa(contenuto) { esito ->
                onAvviso(
                    if (esito == null) {
                        messaggioImportazioneFallita
                    } else {
                        context.getString(
                            R.string.impostazioni_importato_formato,
                            esito.pazienti,
                            esito.misurazioni
                        )
                    }
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Sfondo)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.titolo_impostazioni),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 8.dp)
        )

        CartaBase(modifier = Modifier.padding(horizontal = 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Pastiglia(
                    testo = profilo?.iniziali ?: "Dc",
                    sfondo = Indaco,
                    colore = Superficie,
                    lato = 48,
                    raggio = 15,
                    stile = MaterialTheme.typography.titleLarge
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profilo?.nomeCompleto.orEmpty(),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = profilo?.strutturaVisibile.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TestoDebole,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                PulsanteCompatto(
                    testo = stringResource(R.string.azione_modifica),
                    sfondo = Sfondo,
                    onClick = onModificaProfilo
                )
            }
        }

        NotaInformativa(
            messaggio = stringResource(R.string.impostazioni_nota_account),
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 10.dp)
        )

        EtichettaDiSezione(
            testo = stringResource(R.string.impostazioni_sezione_dati),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 8.dp)
        )
        CartaBase(modifier = Modifier.padding(horizontal = 18.dp)) {
            RigaElenco(
                titolo = stringResource(R.string.impostazioni_esporta),
                sottotitolo = stringResource(
                    R.string.impostazioni_esporta_descrizione_formato,
                    pluralStringResource(
                        R.plurals.impostazioni_conteggio_pazienti,
                        stato.numeroPazienti,
                        stato.numeroPazienti
                    ),
                    pluralStringResource(
                        R.plurals.impostazioni_conteggio_misurazioni,
                        stato.numeroMisurazioni,
                        stato.numeroMisurazioni
                    )
                ),
                glifo = "↥",
                onClick = {
                    viewModel.esporta { contenuto ->
                        contenutoDaEsportare = contenuto
                        selettoreSalvataggio.launch(nomeFileBackup)
                    }
                }
            )
            RigaElenco(
                titolo = stringResource(R.string.impostazioni_importa),
                sottotitolo = stringResource(R.string.impostazioni_importa_descrizione),
                glifo = "↧",
                onClick = {
                    pannello = Pannello.Conferma(
                        titolo = context.getString(R.string.impostazioni_dialog_importa_titolo),
                        corpo = context.getString(R.string.impostazioni_dialog_importa_messaggio),
                        etichettaConferma = context.getString(R.string.impostazioni_dialog_importa_conferma),
                        onConferma = {
                            pannello = null
                            selettoreApertura.launch(arrayOf("application/json", "text/plain", "*/*"))
                        }
                    )
                }
            )
            RigaElenco(
                titolo = stringResource(R.string.impostazioni_elimina_tutto),
                sottotitolo = stringResource(R.string.impostazioni_elimina_tutto_descrizione),
                glifo = "⌫",
                coloreTitolo = Rosso,
                tintaGlifo = RossoTenue,
                coloreGlifo = Rosso,
                mostraLinea = false,
                onClick = {
                    pannello = Pannello.Conferma(
                        titolo = context.getString(R.string.impostazioni_dialog_elimina_titolo),
                        corpo = context.getString(R.string.impostazioni_dialog_elimina_messaggio),
                        etichettaConferma = context.getString(R.string.impostazioni_dialog_elimina_conferma),
                        distruttiva = true,
                        onConferma = {
                            viewModel.eliminaTuttiIDati {
                                pannello = null
                                onAvviso(messaggioArchivioSvuotato)
                            }
                        }
                    )
                }
            )
        }

        EtichettaDiSezione(
            testo = stringResource(R.string.impostazioni_sezione_sicurezza),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 8.dp)
        )
        CartaBase(modifier = Modifier.padding(horizontal = 18.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCambiaPin)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.impostazioni_cambia_pin),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.impostazioni_cambia_pin_descrizione),
                        style = MaterialTheme.typography.bodySmall,
                        color = TestoDebole
                    )
                }
                Text(text = "›", style = MaterialTheme.typography.titleLarge, color = TestoSpento)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(com.casati.dermcalc.ui.theme.BordoTenue)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.cambiaBiometria() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.impostazioni_biometria),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.impostazioni_biometria_descrizione),
                        style = MaterialTheme.typography.bodySmall,
                        color = TestoDebole
                    )
                }
                Interruttore(attivo = biometriaAttiva, onClick = viewModel::cambiaBiometria)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    pannello?.let { corrente ->
        PannelloInferiore(pannello = corrente, onChiudi = { pannello = null })
    }
}
