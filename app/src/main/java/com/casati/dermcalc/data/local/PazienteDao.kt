package com.casati.dermcalc.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PazienteDao {

    @Insert
    suspend fun inserisci(paziente: PazienteEntity): Long

    @Update
    suspend fun aggiorna(paziente: PazienteEntity)

    @Delete
    suspend fun elimina(paziente: PazienteEntity)

    @Query("SELECT * FROM pazienti ORDER BY nome ASC")
    fun osservaTutti(): Flow<List<PazienteEntity>>

    @Query("SELECT * FROM pazienti WHERE id = :id")
    suspend fun trovaPerId(id: Long): PazienteEntity?
}
