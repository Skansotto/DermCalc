package com.casati.dermcalc.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MisurazioneDao {

    @Insert
    suspend fun inserisci(misurazione: MisurazioneEntity): Long

    @Insert
    suspend fun inserisciTutte(misurazioni: List<MisurazioneEntity>)

    @Update
    suspend fun aggiorna(misurazione: MisurazioneEntity)

    @Delete
    suspend fun elimina(misurazione: MisurazioneEntity)

    @Query("DELETE FROM misurazioni")
    suspend fun eliminaTutte()

    @Query("SELECT * FROM misurazioni WHERE pazienteId = :pazienteId ORDER BY data DESC")
    fun osservaPerPaziente(pazienteId: Long): Flow<List<MisurazioneEntity>>

    @Query("SELECT * FROM misurazioni ORDER BY data DESC")
    fun osservaTutte(): Flow<List<MisurazioneEntity>>

    @Query("SELECT * FROM misurazioni ORDER BY data ASC")
    suspend fun leggiTutte(): List<MisurazioneEntity>
}
