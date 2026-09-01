package com.casati.dermcalc.data.repository

import com.casati.dermcalc.data.local.MisurazioneDao
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.TipoCalcolo
import kotlinx.coroutines.flow.Flow

class MisurazioneRepository(private val misurazioneDao: MisurazioneDao) {

    fun osservaMisurazioni(pazienteId: Long): Flow<List<MisurazioneEntity>> =
        misurazioneDao.osservaPerPaziente(pazienteId)

    fun osservaTutte(): Flow<List<MisurazioneEntity>> = misurazioneDao.osservaTutte()

    suspend fun salvaMisurazione(pazienteId: Long, tipo: TipoCalcolo, risultato: Double): Long =
        misurazioneDao.inserisci(
            MisurazioneEntity(
                pazienteId = pazienteId,
                tipo = tipo,
                risultato = risultato,
                data = System.currentTimeMillis()
            )
        )

    suspend fun eliminaMisurazione(misurazione: MisurazioneEntity) = misurazioneDao.elimina(misurazione)

    suspend fun leggiTutte(): List<MisurazioneEntity> = misurazioneDao.leggiTutte()

    suspend fun inserisciTutte(misurazioni: List<MisurazioneEntity>) =
        misurazioneDao.inserisciTutte(misurazioni)

    suspend fun eliminaTutte() = misurazioneDao.eliminaTutte()
}
