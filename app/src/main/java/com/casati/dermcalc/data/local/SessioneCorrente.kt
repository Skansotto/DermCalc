package com.casati.dermcalc.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Paziente a cui vengono collegate le misurazioni in corso. È condiviso da tutti i
// calcolatori: si sceglie una volta e resta attivo finché non lo si cambia. Nullo
// significa calcolo anonimo, che non viene salvato nello storico.
class SessioneCorrente {

    private val _pazienteCollegatoId = MutableStateFlow<Long?>(null)
    val pazienteCollegatoId: StateFlow<Long?> = _pazienteCollegatoId

    fun collega(pazienteId: Long?) {
        _pazienteCollegatoId.value = pazienteId
    }

    fun scollegaSe(pazienteId: Long) {
        if (_pazienteCollegatoId.value == pazienteId) {
            _pazienteCollegatoId.value = null
        }
    }
}
