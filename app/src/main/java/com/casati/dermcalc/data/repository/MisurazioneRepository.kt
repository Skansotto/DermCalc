package com.casati.dermcalc.data.repository

import com.casati.dermcalc.data.local.MisurazioneDao
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.TipoCalcolo
import kotlinx.coroutines.flow.Flow

class MisurazioneRepository(private val misurazioneDao: MisurazioneDao) {

    fun osservaMisurazioni(pazienteId: Long): Flow<List<MisurazioneEntity>> =
        misurazioneDao.osservaPerPaziente(pazienteId)

    suspend fun salvaMisurazione(pazienteId: Long, tipo: TipoCalcolo, risultato: Double) {
        misurazioneDao.inserisci(
            MisurazioneEntity(
                pazienteId = pazienteId,
                tipo = tipo,
                risultato = risultato,
                data = System.currentTimeMillis()
            )
        )
    }

    suspend fun eliminaMisurazione(misurazione: MisurazioneEntity) = misurazioneDao.elimina(misurazione)
}
