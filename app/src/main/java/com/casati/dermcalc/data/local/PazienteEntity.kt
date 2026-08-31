package com.casati.dermcalc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pazienti")
data class PazienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val cognome: String,
    val sesso: Sesso,
    val dataNascita: Long
)
