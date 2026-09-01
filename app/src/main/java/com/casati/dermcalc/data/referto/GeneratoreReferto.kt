package com.casati.dermcalc.data.referto

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.ProfiloMedico
import com.casati.dermcalc.domain.Severita
import com.casati.dermcalc.domain.calcolaEta
import com.casati.dermcalc.domain.formattaDataBreve
import com.casati.dermcalc.domain.formattaDataEstesa
import com.casati.dermcalc.domain.formattaDataFile
import com.casati.dermcalc.domain.nomeCompleto
import java.io.File

// Referto in PDF generato sul dispositivo: il file finisce nella cache condivisa
// dell'app e viene passato ad altre app tramite FileProvider, senza mai transitare
// da un server.
object GeneratoreReferto {

    private const val LARGHEZZA = 595
    private const val ALTEZZA = 842
    private const val MARGINE = 48f

    fun generaPdf(
        context: Context,
        paziente: PazienteEntity,
        misurazioni: List<MisurazioneEntity>,
        profilo: ProfiloMedico?
    ): android.net.Uri? = runCatching {
        val documento = PdfDocument()
        val pagina = documento.startPage(PdfDocument.PageInfo.Builder(LARGHEZZA, ALTEZZA, 1).create())
        val canvas = pagina.canvas

        val titolo = Paint().apply { textSize = 22f; isFakeBoldText = true; isAntiAlias = true }
        val sezione = Paint().apply { textSize = 11f; isFakeBoldText = true; isAntiAlias = true }
        val corpo = Paint().apply { textSize = 12f; isAntiAlias = true }
        val tenue = Paint().apply { textSize = 10f; isAntiAlias = true; color = 0xFF8C877E.toInt() }
        val linea = Paint().apply { strokeWidth = 1f; color = 0xFFE4DFD7.toInt() }

        var y = MARGINE + 10f
        canvas.drawText("Referto dermatologico", MARGINE, y, titolo)
        y += 18f
        canvas.drawText("DermCalc · " + formattaDataEstesa(System.currentTimeMillis()), MARGINE, y, tenue)
        if (profilo != null) {
            y += 14f
            canvas.drawText(profilo.nomeCompleto + " · " + profilo.strutturaVisibile, MARGINE, y, tenue)
        }

        y += 24f
        canvas.drawLine(MARGINE, y, LARGHEZZA - MARGINE, y, linea)
        y += 24f
        canvas.drawText("PAZIENTE", MARGINE, y, sezione)
        y += 18f
        canvas.drawText(paziente.nomeCompleto, MARGINE, y, corpo)
        y += 16f
        canvas.drawText(
            "${calcolaEta(paziente.dataNascita)} anni · ${paziente.sesso.sigla} · " +
                formattaDataBreve(paziente.dataNascita),
            MARGINE,
            y,
            corpo
        )

        y += 30f
        canvas.drawLine(MARGINE, y, LARGHEZZA - MARGINE, y, linea)
        y += 24f
        canvas.drawText("MISURAZIONI", MARGINE, y, sezione)
        y += 20f

        if (misurazioni.isEmpty()) {
            canvas.drawText("Nessuna misurazione registrata", MARGINE, y, corpo)
        } else {
            misurazioni.sortedByDescending { it.data }.forEach { misurazione ->
                if (y > ALTEZZA - MARGINE) return@forEach
                canvas.drawText(formattaDataEstesa(misurazione.data), MARGINE, y, corpo)
                canvas.drawText(misurazione.tipo.name, MARGINE + 200f, y, corpo)
                canvas.drawText(
                    Severita.formattaValore(misurazione.tipo, misurazione.risultato) +
                        Severita.unita(misurazione.tipo),
                    MARGINE + 280f,
                    y,
                    corpo
                )
                y += 18f
            }
        }

        documento.finishPage(pagina)

        val cartella = File(context.cacheDir, "referti").apply { mkdirs() }
        val file = File(
            cartella,
            "referto-${paziente.cognome.lowercase()}-${formattaDataFile(System.currentTimeMillis())}.pdf"
        )
        file.outputStream().use { documento.writeTo(it) }
        documento.close()

        FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }.getOrNull()
}
