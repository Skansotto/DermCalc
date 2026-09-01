package com.casati.dermcalc.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.domain.BandaSeverita
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.ui.theme.Bordo
import com.casati.dermcalc.ui.theme.BordoForte
import com.casati.dermcalc.ui.theme.BordoMarcato
import com.casati.dermcalc.ui.theme.EtichettaSezione
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoBordo
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.Inchiostro
import com.casati.dermcalc.ui.theme.Jakarta
import com.casati.dermcalc.ui.theme.MonoPiccolo
import com.casati.dermcalc.ui.theme.PlexMono
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.SuperficieCalda
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoMedio
import com.casati.dermcalc.ui.theme.TestoSpento

// ─── Testo di sezione ────────────────────────────────────────────────────────

@Composable
fun EtichettaDiSezione(testo: String, modifier: Modifier = Modifier) {
    Text(
        text = testo.uppercase(),
        style = EtichettaSezione,
        color = TestoDebole,
        modifier = modifier
    )
}

// ─── Contenitori ─────────────────────────────────────────────────────────────

@Composable
fun CartaBase(
    modifier: Modifier = Modifier,
    raggio: Int = 18,
    sfondo: Color = Superficie,
    bordo: Color = Bordo,
    onClick: (() -> Unit)? = null,
    contenuto: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(raggio.dp)
    Column(
        modifier = modifier
            .clip(forma)
            .background(sfondo)
            .border(1.dp, bordo, forma)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        content = contenuto
    )
}

// ─── Pulsanti ────────────────────────────────────────────────────────────────

@Composable
fun PulsanteAzione(
    testo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    abilitato: Boolean = true,
    altezza: Int = 54
) {
    val sfondo = if (abilitato) Indaco else BordoMarcato
    val testoColore = if (abilitato) Superficie else TestoDebole
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(altezza.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(sfondo)
            .clickable(enabled = abilitato, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = testo, style = MaterialTheme.typography.labelLarge, color = testoColore)
    }
}

@Composable
fun PulsanteSecondario(
    testo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    altezza: Int = 52,
    colore: Color = Inchiostro
) {
    Box(
        modifier = modifier
            .height(altezza.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Superficie)
            .border(1.dp, BordoMarcato, RoundedCornerShape(15.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = testo,
            style = MaterialTheme.typography.titleMedium,
            color = colore,
            maxLines = 1
        )
    }
}

@Composable
fun PulsanteCompatto(
    testo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sfondo: Color = Superficie,
    bordo: Color = BordoMarcato,
    colore: Color = TestoMedio
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(sfondo)
            .border(1.dp, bordo, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = testo,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = colore,
            maxLines = 1
        )
    }
}

@Composable
fun PulsanteIndietro(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "←", style = TextStyle(fontFamily = Jakarta), fontSize = MaterialTheme.typography.headlineSmall.fontSize, color = Inchiostro)
    }
}

// ─── Avatar / badge ──────────────────────────────────────────────────────────

@Composable
fun Pastiglia(
    testo: String,
    sfondo: Color,
    colore: Color,
    modifier: Modifier = Modifier,
    lato: Int = 34,
    raggio: Int = 11,
    stile: TextStyle = MonoPiccolo
) {
    Box(
        modifier = modifier
            .size(lato.dp)
            .clip(RoundedCornerShape(raggio.dp))
            .background(sfondo),
        contentAlignment = Alignment.Center
    ) {
        Text(text = testo, style = stile, color = colore, maxLines = 1)
    }
}

@Composable
fun BadgeSeverita(
    testo: String,
    colore: Color,
    sfondo: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(sfondo)
            .padding(horizontal = 11.dp, vertical = 6.dp)
    ) {
        Text(
            text = testo,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = colore
        )
    }
}

@Composable
fun ChipVariazione(testo: String, colore: Color, tinta: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(tinta)
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(text = testo, style = com.casati.dermcalc.ui.theme.MonoMinuto, color = colore)
    }
}

// ─── Selettori ───────────────────────────────────────────────────────────────

// Selettore per punteggi discreti: un pulsante per ogni valore ammesso, così il
// valore selezionato resta leggibile senza dover interpretare la posizione di uno slider.
@Composable
fun SelettoreSegmentato(
    valore: Int,
    massimo: Int,
    onValoreChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (v in 0..massimo) {
            val selezionato = v == valore
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selezionato) Indaco else Sfondo)
                    .border(
                        1.dp,
                        if (selezionato) Indaco else BordoForte,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onValoreChange(v) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = v.toString(),
                    style = com.casati.dermcalc.ui.theme.MonoMedio.copy(fontSize = MaterialTheme.typography.labelLarge.fontSize),
                    color = if (selezionato) Superficie else TestoMedio
                )
            }
        }
    }
}

@Composable
fun ChipSelezione(
    testo: String,
    selezionato: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    altezza: Int = 34,
    stile: TextStyle = MonoPiccolo,
    suffisso: String? = null
) {
    val sfondo by animateColorAsState(if (selezionato) Indaco else Superficie, tween(160), label = "chipBg")
    Row(
        modifier = modifier
            .height(altezza.dp)
            .clip(RoundedCornerShape(if (altezza >= 40) 12.dp else 10.dp))
            .background(sfondo)
            .border(
                1.dp,
                if (selezionato) Indaco else BordoForte,
                RoundedCornerShape(if (altezza >= 40) 12.dp else 10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = if (altezza >= 40) 13.dp else 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(
            text = testo,
            style = stile,
            color = if (selezionato) Superficie else TestoMedio,
            maxLines = 1
        )
        if (suffisso != null) {
            Text(
                text = suffisso,
                style = com.casati.dermcalc.ui.theme.MonoMinuto,
                color = if (selezionato) Superficie.copy(alpha = 0.65f) else TestoMedio.copy(alpha = 0.65f),
                maxLines = 1
            )
        }
    }
}

// ─── Barra di severità ───────────────────────────────────────────────────────

// Barra a segmenti: lo sfondo mostra le fasce cliniche dell'indice, la barra piena
// il punteggio corrente. Le due informazioni insieme dicono "quanto" e "quanto è grave".
@Composable
fun BarraSeverita(
    punteggio: Double,
    bande: List<BandaSeverita>,
    colore: Color,
    modifier: Modifier = Modifier,
    altezza: Int = 5,
    raggio: Int = 3
) {
    val frazione by animateFloatAsState(
        targetValue = (punteggio / Severita.PUNTEGGIO_MASSIMO).coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(350),
        label = "gauge"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(altezza.dp)
            .clip(RoundedCornerShape(raggio.dp))
            .background(SuperficieCalda)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            var precedente = 0.0
            bande.forEach { banda ->
                val fine = minOf(Severita.PUNTEGGIO_MASSIMO, banda.soglia)
                val peso = ((fine - precedente) / Severita.PUNTEGGIO_MASSIMO).toFloat()
                if (peso > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(peso)
                            .fillMaxHeight()
                            .background(banda.segmento)
                    )
                }
                precedente = fine
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(frazione)
                .fillMaxHeight()
                .clip(RoundedCornerShape(raggio.dp))
                .background(colore)
        )
    }
}

// ─── Campi di testo ──────────────────────────────────────────────────────────

@Composable
fun CampoTesto(
    valore: String,
    onValoreChange: (String) -> Unit,
    segnaposto: String,
    modifier: Modifier = Modifier,
    monospaziato: Boolean = false,
    tipoTastiera: KeyboardType = KeyboardType.Text,
    trasformazione: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = valore,
        onValueChange = onValoreChange,
        visualTransformation = trasformazione,
        placeholder = {
            Text(
                text = segnaposto,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = if (monospaziato) PlexMono else Jakarta
                ),
                color = TestoSpento
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = if (monospaziato) PlexMono else Jakarta,
            color = Inchiostro
        ),
        keyboardOptions = KeyboardOptions(keyboardType = tipoTastiera),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Superficie,
            unfocusedContainerColor = Superficie,
            focusedBorderColor = Indaco,
            unfocusedBorderColor = BordoForte,
            cursorColor = Indaco
        ),
        modifier = modifier.fillMaxWidth()
    )
}

// ─── Contatore a passi ───────────────────────────────────────────────────────

// Valore numerico con incremento e decremento: sostituisce l'inserimento da tastiera
// per grandezze che si aggiustano di poco alla volta (peso, altezza, palmi).
@Composable
fun ContatoreValore(
    etichetta: String,
    valore: String,
    unita: String,
    onDecrementa: () -> Unit,
    onIncrementa: () -> Unit,
    modifier: Modifier = Modifier
) {
    CartaBase(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                EtichettaDiSezione(etichetta)
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(text = valore, style = com.casati.dermcalc.ui.theme.MonoGrande, color = Inchiostro)
                    Text(
                        text = unita,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TestoDebole,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
            PulsanteQuadrato("−", onDecrementa, Sfondo, BordoForte, Inchiostro)
            PulsanteQuadrato("+", onIncrementa, IndacoTenue, IndacoBordo, Indaco)
        }
    }
}

@Composable
fun PulsanteQuadrato(
    simbolo: String,
    onClick: () -> Unit,
    sfondo: Color,
    bordo: Color,
    colore: Color,
    lato: Int = 46
) {
    Box(
        modifier = Modifier
            .size(lato.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(sfondo)
            .border(1.dp, bordo, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = simbolo,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Normal),
            color = colore
        )
    }
}

// ─── Interruttore ────────────────────────────────────────────────────────────

@Composable
fun Interruttore(attivo: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val traccia by animateColorAsState(if (attivo) Indaco else BordoMarcato, tween(200), label = "track")
    val offset by animateFloatAsState(if (attivo) 22f else 3f, tween(200), label = "knob")
    Box(
        modifier = modifier
            .width(46.dp)
            .height(27.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(traccia)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .padding(start = offset.dp, top = 3.dp)
                .size(21.dp)
                .clip(CircleShape)
                .background(Superficie)
        )
    }
}

// ─── Riga di elenco ──────────────────────────────────────────────────────────

@Composable
fun RigaElenco(
    titolo: String,
    sottotitolo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    coloreTitolo: Color = Inchiostro,
    glifo: String? = null,
    tintaGlifo: Color = IndacoTenue,
    coloreGlifo: Color = Indaco,
    mostraLinea: Boolean = true
) {
    Column(modifier = modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (glifo != null) {
                Pastiglia(glifo, tintaGlifo, coloreGlifo)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titolo,
                    style = MaterialTheme.typography.titleMedium,
                    color = coloreTitolo,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = sottotitolo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TestoDebole,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(text = "›", style = MaterialTheme.typography.titleLarge, color = TestoSpento)
        }
        if (mostraLinea) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(com.casati.dermcalc.ui.theme.BordoTenue)
            )
        }
    }
}

// ─── Avviso in linea ─────────────────────────────────────────────────────────

@Composable
fun AvvisoErrore(messaggio: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(com.casati.dermcalc.ui.theme.RossoTenue)
            .padding(horizontal = 13.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(com.casati.dermcalc.ui.theme.Rosso),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "!",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Superficie
            )
        }
        Text(
            text = messaggio,
            style = MaterialTheme.typography.labelMedium,
            color = com.casati.dermcalc.ui.theme.Rosso
        )
    }
}

@Composable
fun NotaInformativa(messaggio: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(IndacoTenue)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 1.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(Indaco),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "i",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Superficie
            )
        }
        Text(text = messaggio, style = MaterialTheme.typography.bodySmall, color = Indaco)
    }
}
