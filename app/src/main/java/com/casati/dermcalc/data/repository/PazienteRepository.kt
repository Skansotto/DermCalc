package com.casati.dermcalc.data.repository

import com.casati.dermcalc.data.local.PazienteDao
import com.casati.dermcalc.data.local.PazienteEntity
import kotlinx.coroutines.flow.Flow

class PazienteRepository(private val pazienteDao: PazienteDao) {

    fun osservaPazienti(): Flow<List<PazienteEntity>> = pazienteDao.osservaTutti()

    suspend fun aggiungiPaziente(nome: String, dataNascita: Long): Long =
        pazienteDao.inserisci(PazienteEntity(nome = nome, dataNascita = dataNascita))

    suspend fun eliminaPaziente(paziente: PazienteEntity) = pazienteDao.elimina(paziente)
}
