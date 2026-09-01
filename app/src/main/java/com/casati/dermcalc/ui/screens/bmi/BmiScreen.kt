package com.casati.dermcalc.ui.screens.bmi

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.domain.calcolaEta
import com.casati.dermcalc.domain.nomeCompleto
import com.casati.dermcalc.ui.components.CartaBase
import com.casati.dermcalc.ui.components.ContatoreValore
import com.casati.dermcalc.ui.components.IntestazioneSchermata
import com.casati.dermcalc.ui.components.Pannello
import com.casati.dermcalc.ui.components.PannelloInferiore
import com.casati.dermcalc.ui.components.PiedeAzione
import com.casati.dermcalc.ui.components.PulsanteAzione
import com.casati.dermcalc.ui.shared.RigaPazienteCollegato
import com.casati.dermcalc.ui.shared.SelezionePazienteViewModel
import com.casati.dermcalc.ui.shared.pannelloSelezionePaziente
import com.casati.dermcalc.ui.theme.AmbraTenue
import com.casati.dermcalc.ui.theme.Inchiostro
import com.casati.dermcalc.ui.theme.IndacoSegmento
import com.casati.dermcalc.ui.theme.MonoMinuto
import com.casati.dermcalc.ui.theme.RossoTenue
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoLeggero
import com.casati.dermcalc.ui.theme.VerdeTenue

@Composable
fun BmiScreen(
    viewModel: BmiViewModel,
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

    Column(modifier = modifier.fillMaxSize().background(Sfondo)) {
        IntestazioneSchermata(
            titolo = stringResource(R.string.titolo_bmi),
            sottotitolo = com.casati.dermcalc.domain.Indice.nomeEsteso(TipoCalcolo.BMI),
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
                onApriSelettore = {
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
                },
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 8.dp)
            )

            val colore by animateColorAsState(stato.colore, tween(300), label = "bmiColore")
            Column(
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp, top = 12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(stato.tinta)
                    .padding(vertical = 26.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = String.format("%.1f", stato.valore),
                        style = MaterialTheme.typography.displayMedium,
                        color = colore
                    )
                    Text(
                        text = stringResource(R.string.bmi_unita),
                        style = MonoMinuto,
                        color = colore.copy(alpha = 0.55f),
                        modifier = Modifier.padding(bottom = 6.dp)
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

            ScalaBmi(
                posizione = stato.posizioneScala,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp)
            )

            Column(
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ContatoreValore(
                    etichetta = stringResource(R.string.bmi_peso),
                    valore = String.format("%.1f", stato.peso),
                    unita = stringResource(R.string.bmi_unita_peso),
                    onDecrementa = viewModel::onPesoDecrementa,
                    onIncrementa = viewModel::onPesoIncrementa
                )
                ContatoreValore(
                    etichetta = stringResource(R.string.bmi_altezza),
                    valore = stato.altezzaCm.toString(),
                    unita = stringResource(R.string.bmi_unita_altezza),
                    onDecrementa = viewModel::onAltezzaDecrementa,
                    onIncrementa = viewModel::onAltezzaIncrementa
                )
            }

            Text(
                text = stringResource(R.string.bmi_nota),
                style = MaterialTheme.typography.bodySmall,
                color = TestoDebole,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp)
            )
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
                    } else {
                        viewModel.salva { pazienteId, valore ->
                            onSalvato(
                                pazienteId,
                                contesto.getString(
                                    R.string.risultato_salvato_formato,
                                    TipoCalcolo.BMI.name,
                                    Severita.formattaValore(TipoCalcolo.BMI, valore),
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

// Scala di riferimento del BMI: le fasce restano visibili anche quando il valore
// cambia, così si legge subito quanto si è lontani dalla soglia successiva.
@Composable
private fun ScalaBmi(posizione: Float, modifier: Modifier = Modifier) {
    CartaBase(modifier = modifier, raggio = 16) {
        Column(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("15", "18.5", "25", "30", "40").forEach {
                    Text(text = it, style = MonoMinuto, color = TestoLeggero)
                }
            }
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(18.dp)) {
                val larghezza = maxWidth
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    SegmentoScala(0.14f, IndacoSegmento)
                    SegmentoScala(0.26f, VerdeTenue)
                    SegmentoScala(0.20f, AmbraTenue)
                    SegmentoScala(0.40f, RossoTenue)
                }
                val offset by animateFloatAsState(posizione, tween(300), label = "bmiMarker")
                Box(
                    modifier = Modifier
                        .padding(start = (larghezza * offset) - 2.dp)
                        .width(4.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Inchiostro)
                )
            }
        }
    }
}

@Composable
private fun RowScope.SegmentoScala(peso: Float, colore: Color) {
    Box(modifier = Modifier.weight(peso).fillMaxHeight().background(colore))
}
