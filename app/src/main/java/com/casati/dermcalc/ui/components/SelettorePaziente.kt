package com.casati.dermcalc.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.PazienteEntity

// Selettore condiviso da tutte le calcolatrici per collegare una misurazione a un paziente,
// oppure lasciarla anonima (nessun collegamento salvato nello storico).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelettorePaziente(
    pazienti: List<PazienteEntity>,
    pazienteSelezionatoId: Long?,
    onPazienteSelezionato: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var espanso by remember { mutableStateOf(false) }
    val etichettaAnonima = stringResource(R.string.selettore_paziente_anonimo)
    val etichettaSelezionata = pazienti.find { it.id == pazienteSelezionatoId }?.nome ?: etichettaAnonima

    ExposedDropdownMenuBox(
        expanded = espanso,
        onExpandedChange = { espanso = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = etichettaSelezionata,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.selettore_paziente_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = espanso) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        DropdownMenu(
            expanded = espanso,
            onDismissRequest = { espanso = false }
        ) {
            DropdownMenuItem(
                text = { Text(etichettaAnonima) },
                onClick = {
                    onPazienteSelezionato(null)
                    espanso = false
                }
            )
            pazienti.forEach { paziente ->
                DropdownMenuItem(
                    text = { Text(paziente.nome) },
                    onClick = {
                        onPazienteSelezionato(paziente.id)
                        espanso = false
                    }
                )
            }
        }
    }
}
