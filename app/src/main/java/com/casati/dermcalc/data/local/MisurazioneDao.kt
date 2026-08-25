package com.casati.dermcalc.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MisurazioneDao {

    @Insert
    suspend fun inserisci(misurazione: MisurazioneEntity): Long

    @Delete
    suspend fun elimina(misurazione: MisurazioneEntity)

    @Query("SELECT * FROM misurazioni WHERE pazienteId = :pazienteId ORDER BY data DESC")
    fun osservaPerPaziente(pazienteId: Long): Flow<List<MisurazioneEntity>>
}
