package com.casati.dermcalc.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.ProfiloMedico
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.domain.formattaDataCompatta
import com.casati.dermcalc.domain.formattaOggiEsteso
import com.casati.dermcalc.domain.iniziali
import com.casati.dermcalc.domain.nomeCompleto
import com.casati.dermcalc.domain.variazione
import com.casati.dermcalc.ui.components.CartaBase
import com.casati.dermcalc.ui.components.ChipVariazione
import com.casati.dermcalc.ui.components.EtichettaDiSezione
import com.casati.dermcalc.ui.components.Pannello
import com.casati.dermcalc.ui.components.PannelloInferiore
import com.casati.dermcalc.ui.components.Pastiglia
import com.casati.dermcalc.ui.components.PulsanteCompatto
import com.casati.dermcalc.ui.shared.SelezionePazienteViewModel
import com.casati.dermcalc.ui.shared.pannelloSelezionePaziente
import com.casati.dermcalc.ui.theme.Bordo
import com.casati.dermcalc.ui.theme.BordoMarcato
import com.casati.dermcalc.ui.theme.GrigioPieno
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.MonoMedio
import com.casati.dermcalc.ui.theme.MonoMinuto
import com.casati.dermcalc.ui.theme.MonoPiccolo
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.SuperficieCalda
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoLeggero
import com.casati.dermcalc.ui.theme.TestoMedio

private data class VoceCalcolatore(
    val tipo: TipoCalcolo,
    val glifo: String,
    val descrizioneRes: Int,
    val rangeRes: Int,
    val evidenziato: Boolean
)

private val CALCOLATORI = listOf(
    VoceCalcolatore(TipoCalcolo.PASI, "P", R.string.calcolatore_pasi_descrizione, R.string.calcolatore_range_indice, true),
    VoceCalcolatore(TipoCalcolo.EASI, "E", R.string.calcolatore_easi_descrizione, R.string.calcolatore_range_indice, true),
    VoceCalcolatore(TipoCalcolo.BMI, "B", R.string.calcolatore_bmi_descrizione, R.string.calcolatore_range_bmi, false),
    VoceCalcolatore(TipoCalcolo.BSA, "S", R.string.calcolatore_bsa_descrizione, R.string.calcolatore_range_bsa, false)
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    selezioneViewModel: SelezionePazienteViewModel,
    profilo: ProfiloMedico?,
    pazienteCollegatoId: Long?,
    onCollegaPaziente: (Long?) -> Unit,
    onApriCalcolatore: (TipoCalcolo) -> Unit,
    onApriPaziente: (Long) -> Unit,
    onApriImpostazioni: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stato by viewModel.uiState.collectAsState()
    val pazienti by selezioneViewModel.pazienti.collectAsState()
    var pannello by remember { mutableStateOf<Pannello?>(null) }

    val titoloSelettore = stringResource(R.string.selettore_titolo)
    val etichettaAnonimo = stringResource(R.string.selettore_anonimo)
    val descrizioneAnonimo = stringResource(R.string.selettore_anonimo_descrizione)
    val segnapostoRicerca = stringResource(R.string.pazienti_cerca)
    val formatoMeta = stringResource(R.string.pazienti_meta_formato)
    val collegato = pazienti.firstOrNull { it.paziente.id == pazienteCollegatoId }?.paziente

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Sfondo)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_saluto_formato, profilo?.cognome.orEmpty()),
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = formattaOggiEsteso(), style = MonoPiccolo, color = TestoDebole)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(IndacoTenue)
                    .clickable(onClick = onApriImpostazioni),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profilo?.iniziali ?: "Dc",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Indaco
                )
            }
        }

        // Contesto del calcolo: dice sempre se si sta lavorando in anonimo o su un paziente.
        val sfondoContesto by animateColorAsState(
            if (collegato != null) Indaco else SuperficieCalda,
            tween(250),
            label = "ctxBg"
        )
        Row(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 14.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(sfondoContesto)
                .padding(horizontal = 13.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Pastiglia(
                testo = collegato?.iniziali ?: "—",
                sfondo = if (collegato != null) Superficie.copy(alpha = 0.16f) else BordoMarcato,
                colore = if (collegato != null) Superficie else TestoDebole,
                lato = 36
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        if (collegato != null) R.string.home_paziente_attivo
                        else R.string.home_modalita_anonima
                    ).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (collegato != null) Superficie.copy(alpha = 0.6f) else TestoDebole
                )
                Text(
                    text = collegato?.nomeCompleto ?: stringResource(R.string.home_senza_paziente),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (collegato != null) Superficie else com.casati.dermcalc.ui.theme.Inchiostro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            PulsanteCompatto(
                testo = stringResource(
                    if (collegato != null) R.string.home_azione_cambia else R.string.home_azione_collega
                ),
                sfondo = Color.Transparent,
                bordo = if (collegato != null) Superficie.copy(alpha = 0.3f) else GrigioPieno,
                colore = if (collegato != null) Superficie else TestoMedio,
                onClick = {
                    pannello = pannelloSelezionePaziente(
                        pazienti = pazienti,
                        pazienteCollegatoId = pazienteCollegatoId,
                        titolo = titoloSelettore,
                        etichettaAnonimo = etichettaAnonimo,
                        descrizioneAnonimo = descrizioneAnonimo,
                        segnapostoRicerca = segnapostoRicerca,
                        etaFormatter = { eta, sesso -> String.format(formatoMeta, eta, sesso) },
                        onSeleziona = { id ->
                            onCollegaPaziente(id)
                            pannello = null
                        }
                    )
                }
            )
        }

        EtichettaDiSezione(
            testo = stringResource(R.string.home_sezione_calcolatori),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CALCOLATORI.chunked(2).forEach { riga ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    riga.forEach { voce ->
                        CartaCalcolatore(
                            voce = voce,
                            onClick = { onApriCalcolatore(voce.tipo) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EtichettaDiSezione(stringResource(R.string.home_sezione_recenti))
            Text(
                text = stringResource(R.string.home_conteggio_archivio_formato, stato.totaleMisurazioni),
                style = MonoMinuto,
                color = TestoLeggero
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (stato.recenti.isEmpty()) {
                Text(
                    text = stringResource(R.string.home_recenti_vuoto),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TestoDebole,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Superficie)
                        .border(1.dp, Bordo, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                )
            } else {
                stato.recenti.forEach { recente ->
                    RigaMisurazioneRecente(
                        recente = recente,
                        onClick = { onApriPaziente(recente.paziente.id) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    pannello?.let { corrente ->
        PannelloInferiore(pannello = corrente, onChiudi = { pannello = null })
    }
}

@Composable
private fun CartaCalcolatore(
    voce: VoceCalcolatore,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CartaBase(modifier = modifier, onClick = onClick) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Pastiglia(
                    testo = voce.glifo,
                    sfondo = if (voce.evidenziato) IndacoTenue else SuperficieCalda,
                    colore = if (voce.evidenziato) Indaco else TestoMedio
                )
                Text(text = stringResource(voce.rangeRes), style = MonoMinuto, color = TestoLeggero)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = voce.tipo.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = stringResource(voce.descrizioneRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = TestoDebole
                )
            }
        }
    }
}

@Composable
private fun RigaMisurazioneRecente(recente: MisurazioneRecente, onClick: () -> Unit) {
    val tipo = recente.misurazione.tipo
    val delta = variazione(recente.delta, tipo)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Superficie)
            .border(1.dp, Bordo, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Pastiglia(
            testo = recente.paziente.iniziali,
            sfondo = SuperficieCalda,
            colore = TestoMedio,
            lato = 32,
            raggio = 10
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = recente.paziente.nomeCompleto,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = tipo.name + " · " + formattaDataCompatta(recente.misurazione.data),
                style = MonoMinuto,
                color = TestoDebole
            )
        }
        Text(
            text = Severita.formattaValore(tipo, recente.misurazione.risultato),
            style = MonoMedio,
            color = Severita.colorePunteggio(tipo, recente.misurazione.risultato)
        )
        ChipVariazione(delta.etichetta, delta.colore, delta.tinta)
    }
}
