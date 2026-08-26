package com.casati.dermcalc.data.repository

import com.casati.dermcalc.data.local.MisurazioneDao
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.TipoCalcolo

class MisurazioneRepository(private val misurazioneDao: MisurazioneDao) {

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
}
