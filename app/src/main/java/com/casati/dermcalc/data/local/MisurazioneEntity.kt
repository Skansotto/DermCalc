package com.casati.dermcalc.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "misurazioni",
    foreignKeys = [
        ForeignKey(
            entity = PazienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["pazienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pazienteId")]
)
data class MisurazioneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pazienteId: Long,
    val tipo: TipoCalcolo,
    val risultato: Double,
    val data: Long
)
