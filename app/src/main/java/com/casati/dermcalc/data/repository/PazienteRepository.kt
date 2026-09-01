package com.casati.dermcalc.data.repository

import com.casati.dermcalc.data.local.PazienteDao
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.Sesso
import kotlinx.coroutines.flow.Flow

class PazienteRepository(private val pazienteDao: PazienteDao) {

    fun osservaPazienti(): Flow<List<PazienteEntity>> = pazienteDao.osservaTutti()

    fun osservaPaziente(id: Long): Flow<PazienteEntity?> = pazienteDao.osservaPerId(id)

    suspend fun aggiungiPaziente(nome: String, cognome: String, sesso: Sesso, dataNascita: Long): Long =
        pazienteDao.inserisci(
            PazienteEntity(nome = nome, cognome = cognome, sesso = sesso, dataNascita = dataNascita)
        )

    suspend fun aggiornaPaziente(paziente: PazienteEntity) = pazienteDao.aggiorna(paziente)

    suspend fun eliminaPaziente(paziente: PazienteEntity) = pazienteDao.elimina(paziente)

    suspend fun trovaPaziente(id: Long): PazienteEntity? = pazienteDao.trovaPerId(id)

    suspend fun leggiTutti(): List<PazienteEntity> = pazienteDao.leggiTutti()

    suspend fun eliminaTutti() = pazienteDao.eliminaTutti()
}
