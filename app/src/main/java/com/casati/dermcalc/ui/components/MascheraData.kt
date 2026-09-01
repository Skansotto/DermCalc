package com.casati.dermcalc.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// Mostra "ggmmaaaa" come "gg/mm/aaaa" senza toccare il valore digitato: le barre
// sono aggiunte solo in fase di disegno, così il cursore resta dove l'utente lo
// ha lasciato anche correggendo una cifra in mezzo alla data.
object MascheraData : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val cifre = text.text.take(8)
        val visibile = buildString {
            append(cifre.take(2))
            if (cifre.length > 2) append('/').append(cifre.drop(2).take(2))
            if (cifre.length > 4) append('/').append(cifre.drop(4))
        }

        val mappatura = object : OffsetMapping {
            // Ogni barra inserita sposta in avanti di uno le posizioni successive.
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 2 -> offset
                offset <= 4 -> offset + 1
                else -> offset + 2
            }

            override fun transformedToOriginal(offset: Int): Int = when {
                offset <= 2 -> offset
                offset <= 5 -> offset - 1
                else -> offset - 2
            }.coerceIn(0, cifre.length)
        }

        return TransformedText(AnnotatedString(visibile), mappatura)
    }
}
