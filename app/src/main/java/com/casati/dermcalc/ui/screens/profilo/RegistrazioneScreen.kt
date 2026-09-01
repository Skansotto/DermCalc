package com.casati.dermcalc.ui.screens.profilo

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.components.AvvisoErrore
import com.casati.dermcalc.ui.components.CampoTesto
import com.casati.dermcalc.ui.components.EtichettaDiSezione
import com.casati.dermcalc.ui.components.Marchio
import com.casati.dermcalc.ui.components.PulsanteAzione
import com.casati.dermcalc.ui.components.PulsanteIndietro
import com.casati.dermcalc.ui.theme.Bordo
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.MonoGrande
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.SuperficieCalda
import com.casati.dermcalc.ui.theme.TestoLeggero
import com.casati.dermcalc.ui.theme.TestoMedio

// Prima apertura o modifica del profilo: stessi campi, intestazione diversa.
@Composable
fun RegistrazioneScreen(
    viewModel: ProfiloViewModel,
    inModifica: Boolean,
    onCompletato: () -> Unit,
    onIndietro: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val stato by viewModel.uiState.collectAsState()
    var nome by remember { mutableStateOf(if (inModifica) stato.profilo?.nome.orEmpty() else "") }
    var cognome by remember { mutableStateOf(if (inModifica) stato.profilo?.cognome.orEmpty() else "") }
    var struttura by remember { mutableStateOf(if (inModifica) stato.profilo?.struttura.orEmpty() else "") }
    var errore by remember { mutableStateOf<String?>(null) }
    val messaggioErrore = stringResource(R.string.registrazione_errore_nome)

    Column(modifier = modifier.fillMaxSize().background(Sfondo)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
        ) {
            if (inModifica) {
                Row(
                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PulsanteIndietro(onClick = { onIndietro?.invoke() })
                    Text(
                        text = stringResource(R.string.registrazione_titolo_modifica),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(top = 26.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Marchio(lato = 52, raggio = 16)
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(
                            text = stringResource(R.string.registrazione_titolo),
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = stringResource(R.string.registrazione_descrizione),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TestoMedio
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    EtichettaDiSezione(stringResource(R.string.registrazione_campo_nome))
                    CampoTesto(
                        valore = nome,
                        onValoreChange = { nome = it; errore = null },
                        segnaposto = stringResource(R.string.registrazione_segnaposto_nome)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    EtichettaDiSezione(stringResource(R.string.registrazione_campo_cognome))
                    CampoTesto(
                        valore = cognome,
                        onValoreChange = { cognome = it; errore = null },
                        segnaposto = stringResource(R.string.registrazione_segnaposto_cognome)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        EtichettaDiSezione(stringResource(R.string.registrazione_campo_struttura))
                        Text(
                            text = stringResource(R.string.registrazione_campo_facoltativo),
                            style = MaterialTheme.typography.bodySmall,
                            color = TestoLeggero
                        )
                    }
                    CampoTesto(
                        valore = struttura,
                        onValoreChange = { struttura = it },
                        segnaposto = stringResource(R.string.registrazione_segnaposto_struttura)
                    )
                }
                errore?.let { AvvisoErrore(it) }
            }

            if (!inModifica) {
                Text(
                    text = stringResource(R.string.registrazione_nota_pin),
                    style = MaterialTheme.typography.bodySmall,
                    color = TestoMedio,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth()
                        .background(SuperficieCalda, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Sfondo)
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Bordo))
            PulsanteAzione(
                testo = stringResource(
                    if (inModifica) R.string.registrazione_azione_salva
                    else R.string.registrazione_azione_continua
                ),
                abilitato = nome.isNotBlank() && cognome.isNotBlank(),
                onClick = {
                    if (viewModel.salvaProfilo(nome, cognome, struttura)) {
                        onCompletato()
                    } else {
                        errore = messaggioErrore
                    }
                },
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 16.dp)
            )
        }
    }
}
