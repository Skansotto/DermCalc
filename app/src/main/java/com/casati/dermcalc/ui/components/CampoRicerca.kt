package com.casati.dermcalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.ui.theme.Bordo
import com.casati.dermcalc.ui.theme.Inchiostro
import com.casati.dermcalc.ui.theme.Jakarta
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.TestoDebole
import com.casati.dermcalc.ui.theme.TestoLeggero

// Campo di ricerca condiviso dall'elenco pazienti e dal pannello di selezione,
// così cercare un paziente si fa allo stesso modo ovunque.
@Composable
fun CampoRicerca(
    valore: String,
    onValoreChange: (String) -> Unit,
    segnaposto: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Superficie)
            .border(1.dp, Bordo, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .border(1.8.dp, TestoLeggero, CircleShape)
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (valore.isEmpty()) {
                Text(
                    text = segnaposto,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TestoLeggero
                )
            }
            BasicTextField(
                value = valore,
                onValueChange = onValoreChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = Jakarta,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = Inchiostro
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (valore.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .clickable { onValoreChange("") },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "×", style = MaterialTheme.typography.titleLarge, color = TestoDebole)
            }
        }
    }
}
