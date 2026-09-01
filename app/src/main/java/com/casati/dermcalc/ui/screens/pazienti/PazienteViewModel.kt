package com.casati.dermcalc.ui.screens.pazienti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.SessioneCorrente
import com.casati.dermcalc.data.local.Sesso
import com.casati.dermcalc.data.repository.PazienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Esito della validazione del form paziente: distingue i motivi dell'errore così
// la schermata può mostrare un messaggio specifico invece di un generico "dati non validi".
enum class EsitoSalvataggio { OK, NOME_MANCANTE, DATA_NON_VALIDA, DUPLICATO }

class PazienteViewModel(
    private val repository: PazienteRepository,
    private val sessione: SessioneCorrente
) : ViewModel() {

    private val _ricerca = MutableStateFlow("")
    val ricerca: StateFlow<String> = _ricerca

    fun onRicercaChange(testo: String) {
        _ricerca.value = testo
    }

    // Il nuovo paziente diventa subito quello attivo: chi lo registra sta per misurarlo.
    fun aggiungiPaziente(
        nome: String,
        cognome: String,
        sesso: Sesso,
        dataNascita: Long?,
        esistenti: List<PazienteEntity>,
        onSalvato: (Long) -> Unit
    ): EsitoSalvataggio {
        val nomePulito = nome.trim()
        val cognomePulito = cognome.trim()
        if (nomePulito.length < 2 || cognomePulito.length < 2) return EsitoSalvataggio.NOME_MANCANTE
        if (dataNascita == null) return EsitoSalvataggio.DATA_NON_VALIDA
        if (esisteGia(esistenti, nomePulito, cognomePulito, dataNascita, null)) {
            return EsitoSalvataggio.DUPLICATO
        }
        viewModelScope.launch {
            val id = repository.aggiungiPaziente(nomePulito, cognomePulito, sesso, dataNascita)
            sessione.collega(id)
            onSalvato(id)
        }
        return EsitoSalvataggio.OK
    }

    fun aggiornaPaziente(
        paziente: PazienteEntity,
        nome: String,
        cognome: String,
        sesso: Sesso,
        dataNascita: Long?,
        esistenti: List<PazienteEntity>,
        onSalvato: () -> Unit
    ): EsitoSalvataggio {
        val nomePulito = nome.trim()
        val cognomePulito = cognome.trim()
        if (nomePulito.length < 2 || cognomePulito.length < 2) return EsitoSalvataggio.NOME_MANCANTE
        if (dataNascita == null) return EsitoSalvataggio.DATA_NON_VALIDA
        if (esisteGia(esistenti, nomePulito, cognomePulito, dataNascita, paziente.id)) {
            return EsitoSalvataggio.DUPLICATO
        }
        viewModelScope.launch {
            repository.aggiornaPaziente(
                paziente.copy(
                    nome = nomePulito,
                    cognome = cognomePulito,
                    sesso = sesso,
                    dataNascita = dataNascita
                )
            )
            onSalvato()
        }
        return EsitoSalvataggio.OK
    }

    fun eliminaPaziente(paziente: PazienteEntity, onEliminato: () -> Unit) {
        viewModelScope.launch {
            repository.eliminaPaziente(paziente)
            sessione.scollegaSe(paziente.id)
            onEliminato()
        }
    }

    fun collegaPaziente(pazienteId: Long?) {
        sessione.collega(pazienteId)
    }

    private fun esisteGia(
        esistenti: List<PazienteEntity>,
        nome: String,
        cognome: String,
        dataNascita: Long,
        idDaEscludere: Long?
    ): Boolean = esistenti.any {
        it.id != idDaEscludere &&
            it.nome.equals(nome, ignoreCase = true) &&
            it.cognome.equals(cognome, ignoreCase = true) &&
            it.dataNascita == dataNascita
    }

    class Factory(
        private val repository: PazienteRepository,
        private val sessione: SessioneCorrente
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PazienteViewModel(repository, sessione) as T
        }
    }
}
