package com.casati.dermcalc.domain

import com.casati.dermcalc.data.local.TipoCalcolo

// Distretti corporei condivisi da PASI ed EASI: cambiano i parametri clinici,
// non la struttura del calcolo né i pesi.
enum class Distretto(val sigla: String, val etichetta: String, val peso: Double) {
    TESTA("H", "Testa e collo", 0.1),
    ARTI_SUPERIORI("U", "Arti superiori", 0.2),
    TRONCO("T", "Tronco", 0.3),
    ARTI_INFERIORI("L", "Arti inferiori", 0.4)
}

// Parametro clinico valutato su ogni distretto, con la scala descrittiva mostrata
// nella guida contestuale.
data class ParametroClinico(
    val etichetta: String,
    val nomeInglese: String,
    val massimo: Int,
    val spiegazione: String,
    val scala: List<String>
)

object Indice {

    val PARAMETRI_PASI = listOf(
        ParametroClinico(
            etichetta = "Eritema",
            nomeInglese = "Erythema",
            massimo = 4,
            spiegazione = "Intensità del rossore della placca rispetto alla cute sana circostante. " +
                "Valutare con luce naturale, senza pressione sulla lesione.",
            scala = listOf("nessun rossore", "rosa tenue", "rosso definito", "rosso intenso", "rosso molto scuro")
        ),
        ParametroClinico(
            etichetta = "Indurimento",
            nomeInglese = "Induration",
            massimo = 4,
            spiegazione = "Spessore e infiltrazione della placca rispetto al piano cutaneo. " +
                "Si valuta al tatto, non a vista.",
            scala = listOf("piana", "appena rilevata", "rilievo netto", "placca spessa", "placca molto spessa")
        ),
        ParametroClinico(
            etichetta = "Desquamazione",
            nomeInglese = "Desquamation",
            massimo = 4,
            spiegazione = "Quantità di squame sulla superficie della placca. " +
                "Considerare la porzione prevalente del distretto.",
            scala = listOf("assente", "squame fini", "squame evidenti", "squame spesse", "squame molto spesse")
        )
    )

    val PARAMETRI_EASI = listOf(
        ParametroClinico(
            etichetta = "Eritema",
            nomeInglese = "Erythema",
            massimo = 3,
            spiegazione = "Rossore infiammatorio delle lesioni eczematose nel distretto.",
            scala = listOf("assente", "lieve", "moderato", "severo")
        ),
        ParametroClinico(
            etichetta = "Edema / papule",
            nomeInglese = "Edema / papulation",
            massimo = 3,
            spiegazione = "Rilievo e gonfiore delle lesioni, valutato al tatto.",
            scala = listOf("assente", "lieve", "moderato", "severo")
        ),
        ParametroClinico(
            etichetta = "Escoriazioni",
            nomeInglese = "Excoriation",
            massimo = 3,
            spiegazione = "Lesioni da grattamento: erosioni superficiali e croste.",
            scala = listOf("assenti", "lievi", "moderate", "severe")
        ),
        ParametroClinico(
            etichetta = "Lichenificazione",
            nomeInglese = "Lichenification",
            massimo = 3,
            spiegazione = "Ispessimento cutaneo con accentuazione delle linee di superficie, " +
                "da grattamento cronico.",
            scala = listOf("assente", "lieve", "moderata", "severa")
        )
    )

    val ETICHETTE_AREA = listOf("0%", "1–9", "10–29", "30–49", "50–69", "70–89", "90–100")

    val SCALA_AREA = listOf(
        "0% — nessuna lesione",
        "1–9% del distretto",
        "10–29%",
        "30–49%",
        "50–69%",
        "70–89%",
        "90–100%"
    )

    const val SPIEGAZIONE_AREA = "Percentuale del singolo distretto interessata dalle lesioni, " +
        "convertita in un punteggio discreto da 0 a 6 secondo le linee guida."

    fun parametri(tipo: TipoCalcolo): List<ParametroClinico> =
        if (tipo == TipoCalcolo.EASI) PARAMETRI_EASI else PARAMETRI_PASI

    fun nomeEsteso(tipo: TipoCalcolo): String = when (tipo) {
        TipoCalcolo.PASI -> "Psoriasis Area and Severity Index"
        TipoCalcolo.EASI -> "Eczema Area and Severity Index"
        TipoCalcolo.BMI -> "Body Mass Index · kg/m²"
        TipoCalcolo.BSA -> "Superficie corporea coinvolta · regola del nove"
    }
}

// Valori inseriti per un distretto: un punteggio per ogni parametro dell'indice e l'area.
data class ValoriDistretto(
    val parametri: List<Int>,
    val area: Int = 0
) {
    val somma: Int get() = parametri.sum()
}

// Contributo di un distretto al punteggio totale: peso × somma parametri × area.
data class ContributoDistretto(
    val distretto: Distretto,
    val valori: ValoriDistretto,
    val subtotale: Double
) {
    val formula: String
        get() = String.format("%.1f", distretto.peso) + " × (" +
            valori.parametri.joinToString("+") + ") × " + valori.area +
            " = " + String.format("%.2f", subtotale)
}

fun calcolaContributi(tipo: TipoCalcolo, valori: Map<Distretto, ValoriDistretto>): List<ContributoDistretto> =
    Distretto.entries.map { distretto ->
        val v = valori[distretto] ?: ValoriDistretto(List(Indice.parametri(tipo).size) { 0 })
        ContributoDistretto(
            distretto = distretto,
            valori = v,
            subtotale = distretto.peso * v.somma * v.area
        )
    }
