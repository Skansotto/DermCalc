package com.casati.dermcalc.data.repository

import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.Sesso
import com.casati.dermcalc.data.local.TipoCalcolo
import org.json.JSONArray
import org.json.JSONObject

data class EsitoImportazione(val pazienti: Int, val misurazioni: Int)

// Il backup è un file JSON che resta in mano all'utente: l'archivio non viene mai
// sincronizzato con un server. L'importazione aggiunge i dati senza cancellare
// quelli già presenti, rinumerando i riferimenti tra paziente e misurazioni.
class BackupRepository(
    private val pazienteRepository: PazienteRepository,
    private val misurazioneRepository: MisurazioneRepository
) {

    suspend fun esporta(): String {
        val pazienti = pazienteRepository.leggiTutti()
        val misurazioni = misurazioneRepository.leggiTutte()

        val arrayPazienti = JSONArray()
        pazienti.forEach { p ->
            arrayPazienti.put(
                JSONObject()
                    .put("id", p.id)
                    .put("nome", p.nome)
                    .put("cognome", p.cognome)
                    .put("sesso", p.sesso.name)
                    .put("dataNascita", p.dataNascita)
            )
        }

        val arrayMisurazioni = JSONArray()
        misurazioni.forEach { m ->
            arrayMisurazioni.put(
                JSONObject()
                    .put("pazienteId", m.pazienteId)
                    .put("tipo", m.tipo.name)
                    .put("risultato", m.risultato)
                    .put("data", m.data)
            )
        }

        return JSONObject()
            .put("versione", VERSIONE_FORMATO)
            .put("esportatoIl", System.currentTimeMillis())
            .put("pazienti", arrayPazienti)
            .put("misurazioni", arrayMisurazioni)
            .toString(2)
    }

    suspend fun importa(contenuto: String): EsitoImportazione {
        val radice = JSONObject(contenuto)
        val arrayPazienti = radice.optJSONArray("pazienti") ?: JSONArray()
        val arrayMisurazioni = radice.optJSONArray("misurazioni") ?: JSONArray()

        // Gli id del file non possono essere riusati: si costruisce una mappa
        // dal vecchio id a quello assegnato dal database locale.
        val mappaId = mutableMapOf<Long, Long>()
        var pazientiImportati = 0
        for (i in 0 until arrayPazienti.length()) {
            val oggetto = arrayPazienti.optJSONObject(i) ?: continue
            val nome = oggetto.optString("nome").trim()
            val cognome = oggetto.optString("cognome").trim()
            if (nome.isEmpty() || cognome.isEmpty()) continue
            val sesso = runCatching { Sesso.valueOf(oggetto.optString("sesso")) }.getOrDefault(Sesso.ALTRO)
            val nuovoId = pazienteRepository.aggiungiPaziente(
                nome = nome,
                cognome = cognome,
                sesso = sesso,
                dataNascita = oggetto.optLong("dataNascita")
            )
            mappaId[oggetto.optLong("id", -1L)] = nuovoId
            pazientiImportati++
        }

        val daInserire = mutableListOf<MisurazioneEntity>()
        for (i in 0 until arrayMisurazioni.length()) {
            val oggetto = arrayMisurazioni.optJSONObject(i) ?: continue
            val pazienteId = mappaId[oggetto.optLong("pazienteId", -1L)] ?: continue
            val tipo = runCatching { TipoCalcolo.valueOf(oggetto.optString("tipo")) }.getOrNull() ?: continue
            daInserire += MisurazioneEntity(
                pazienteId = pazienteId,
                tipo = tipo,
                risultato = oggetto.optDouble("risultato", 0.0),
                data = oggetto.optLong("data", System.currentTimeMillis())
            )
        }
        if (daInserire.isNotEmpty()) {
            misurazioneRepository.inserisciTutte(daInserire)
        }

        return EsitoImportazione(pazienti = pazientiImportati, misurazioni = daInserire.size)
    }

    // L'ordine conta: le misurazioni hanno una foreign key sul paziente.
    suspend fun eliminaTutto() {
        misurazioneRepository.eliminaTutte()
        pazienteRepository.eliminaTutti()
    }

    // Scheda del singolo paziente, per condividerne i dati senza esportare l'intero archivio.
    fun schedaPaziente(paziente: PazienteEntity, misurazioni: List<MisurazioneEntity>): String {
        val array = JSONArray()
        misurazioni.forEach { m ->
            array.put(
                JSONObject()
                    .put("tipo", m.tipo.name)
                    .put("risultato", m.risultato)
                    .put("data", m.data)
            )
        }
        return JSONObject()
            .put("versione", VERSIONE_FORMATO)
            .put(
                "paziente",
                JSONObject()
                    .put("nome", paziente.nome)
                    .put("cognome", paziente.cognome)
                    .put("sesso", paziente.sesso.name)
                    .put("dataNascita", paziente.dataNascita)
            )
            .put("misurazioni", array)
            .toString(2)
    }

    private companion object {
        const val VERSIONE_FORMATO = 1
    }
}
