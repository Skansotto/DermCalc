package com.casati.dermcalc.domain

import androidx.compose.ui.graphics.Color
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.ui.theme.Ambra
import com.casati.dermcalc.ui.theme.AmbraTenue
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoSegmento
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.Rosso
import com.casati.dermcalc.ui.theme.RossoCupo
import com.casati.dermcalc.ui.theme.RossoCupoTenue
import com.casati.dermcalc.ui.theme.RossoTenue
import com.casati.dermcalc.ui.theme.Verde
import com.casati.dermcalc.ui.theme.VerdeTenue
import kotlin.math.abs
import kotlin.math.roundToInt

// Fascia di severità: soglia superiore inclusa, colori e nota clinica associata.
data class BandaSeverita(
    val soglia: Double,
    val etichetta: String,
    val colore: Color,
    val tinta: Color,
    val segmento: Color,
    val nota: String
)

object Severita {

    const val PUNTEGGIO_MASSIMO = 72.0

    // Un punteggio esattamente 0 non è una forma lieve: è assenza di malattia.
    // Nella psoriasi la clearance completa (PASI 0) è un obiettivo terapeutico a sé,
    // nell'EASI lo strato "clear" è previsto dalle fasce pubblicate.
    val BANDE_PASI = listOf(
        BandaSeverita(
            soglia = 0.0,
            etichetta = "assente",
            colore = Indaco,
            tinta = IndacoTenue,
            segmento = IndacoSegmento,
            nota = "Nessuna lesione rilevata (PASI 0): clearance completa. È l'obiettivo più " +
                "ambizioso dei protocolli in uso, corrispondente a una risposta PASI 100."
        ),
        BandaSeverita(
            soglia = 4.99,
            etichetta = "lieve",
            colore = Verde,
            tinta = VerdeTenue,
            segmento = VerdeTenue,
            nota = "Psoriasi lieve (PASI < 5): nella maggior parte delle linee guida è gestibile con terapia " +
                "topica. Rivalutare a 8-12 settimane."
        ),
        BandaSeverita(
            soglia = 10.0,
            etichetta = "moderata",
            colore = Ambra,
            tinta = AmbraTenue,
            segmento = AmbraTenue,
            nota = "Psoriasi moderata (PASI 5-10): valutare fototerapia o terapia sistemica convenzionale. " +
                "Documentare BSA e DLQI a supporto."
        ),
        BandaSeverita(
            soglia = Double.MAX_VALUE,
            etichetta = "severa",
            colore = Rosso,
            tinta = RossoTenue,
            segmento = RossoTenue,
            nota = "Psoriasi severa (PASI > 10): criterio di accesso alla terapia sistemica o biologica nella " +
                "maggior parte dei protocolli. Obiettivo comune: PASI 75 a 16 settimane."
        )
    )

    // Fasce dello studio di interpretabilità di Leshem (2015), quelle di riferimento
    // per tradurre il punteggio EASI in una classe di severità.
    val BANDE_EASI = listOf(
        BandaSeverita(
            soglia = 0.0,
            etichetta = "assente",
            colore = Indaco,
            tinta = IndacoTenue,
            segmento = IndacoSegmento,
            nota = "Nessuna lesione rilevata (EASI 0): cute libera da dermatite. " +
                "Proseguire con emollienti e cura della barriera cutanea."
        ),
        BandaSeverita(
            soglia = 1.0,
            etichetta = "quasi assente",
            colore = Indaco,
            tinta = IndacoTenue,
            segmento = IndacoSegmento,
            nota = "Dermatite atopica quasi assente (EASI 0,1-1): mantenere emollienti e monitoraggio."
        ),
        BandaSeverita(
            soglia = 7.0,
            etichetta = "lieve",
            colore = Verde,
            tinta = VerdeTenue,
            segmento = VerdeTenue,
            nota = "Dermatite atopica lieve (EASI 1.1-7): terapia topica e cura della barriera cutanea. " +
                "Rivalutare a 4-8 settimane."
        ),
        BandaSeverita(
            soglia = 21.0,
            etichetta = "moderata",
            colore = Ambra,
            tinta = AmbraTenue,
            segmento = AmbraTenue,
            nota = "Dermatite atopica moderata (EASI 7.1-21): valutare topici di seconda linea, fototerapia o " +
                "terapia sistemica. Obiettivo comune: EASI 75."
        ),
        BandaSeverita(
            soglia = 50.0,
            etichetta = "severa",
            colore = Rosso,
            tinta = RossoTenue,
            segmento = RossoTenue,
            nota = "Dermatite atopica severa (EASI 21.1-50): indicazione a terapia sistemica o biologica secondo " +
                "i protocolli in uso."
        ),
        BandaSeverita(
            soglia = Double.MAX_VALUE,
            etichetta = "molto severa",
            colore = RossoCupo,
            tinta = RossoCupoTenue,
            segmento = RossoCupoTenue,
            nota = "Dermatite atopica molto severa (EASI > 50): quadro esteso e grave, gestione sistemica e " +
                "follow-up ravvicinato."
        )
    )

    fun bande(tipo: TipoCalcolo): List<BandaSeverita> =
        if (tipo == TipoCalcolo.EASI) BANDE_EASI else BANDE_PASI

    fun banda(tipo: TipoCalcolo, punteggio: Double): BandaSeverita {
        val lista = bande(tipo)
        return lista.firstOrNull { punteggio <= it.soglia } ?: lista.last()
    }

    // Colore del punteggio: PASI/EASI seguono le bande cliniche, BMI e BSA hanno soglie proprie.
    fun colorePunteggio(tipo: TipoCalcolo, valore: Double): Color = when (tipo) {
        TipoCalcolo.BMI -> when {
            valore >= 18.5 && valore < 25 -> Verde
            valore < 30 -> Ambra
            else -> Rosso
        }
        TipoCalcolo.BSA -> when {
            valore <= 10 -> Verde
            valore <= 30 -> Ambra
            else -> Rosso
        }
        else -> banda(tipo, valore).colore
    }

    fun tintaPunteggio(tipo: TipoCalcolo, valore: Double): Color = when (colorePunteggio(tipo, valore)) {
        Verde -> VerdeTenue
        Ambra -> AmbraTenue
        Rosso -> RossoTenue
        Indaco -> IndacoTenue
        RossoCupo -> RossoCupoTenue
        else -> IndacoTenue
    }

    // La BSA si esprime in punti percentuali interi, ma le facce anteriore e posteriore
    // valgono mezzo distretto: il mezzo punto si mostra solo quando c'è davvero.
    fun formattaValore(tipo: TipoCalcolo, valore: Double): String = when {
        tipo != TipoCalcolo.BSA -> String.format("%.1f", valore)
        valore % 1.0 == 0.0 -> valore.roundToInt().toString()
        else -> String.format("%.1f", valore)
    }

    fun unita(tipo: TipoCalcolo): String = when (tipo) {
        TipoCalcolo.BSA -> "%"
        else -> ""
    }
}

// Variazione rispetto alla misurazione precedente dello stesso tipo: in dermatologia
// una diminuzione è un miglioramento, quindi il verde è riservato ai delta negativi.
data class Variazione(
    val etichetta: String,
    val colore: Color,
    val tinta: Color
)

fun variazione(delta: Double?, tipo: TipoCalcolo): Variazione = when {
    delta == null -> Variazione("prima", com.casati.dermcalc.ui.theme.TestoDebole, com.casati.dermcalc.ui.theme.SuperficieCalda)
    abs(delta) < 0.05 -> Variazione("=", com.casati.dermcalc.ui.theme.TestoDebole, com.casati.dermcalc.ui.theme.SuperficieCalda)
    delta < 0 -> Variazione(segnata(delta, tipo), Verde, VerdeTenue)
    else -> Variazione(segnata(delta, tipo), Rosso, RossoTenue)
}

private fun segnata(delta: Double, tipo: TipoCalcolo): String {
    val prefisso = if (delta > 0) "+" else ""
    return prefisso + Severita.formattaValore(tipo, delta)
}
