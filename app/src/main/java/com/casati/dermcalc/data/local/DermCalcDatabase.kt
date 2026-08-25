package com.casati.dermcalc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [PazienteEntity::class, MisurazioneEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DermCalcDatabase : RoomDatabase() {

    abstract fun pazienteDao(): PazienteDao
    abstract fun misurazioneDao(): MisurazioneDao

    companion object {
        @Volatile
        private var istanza: DermCalcDatabase? = null

        fun getInstance(context: Context): DermCalcDatabase {
            return istanza ?: synchronized(this) {
                istanza ?: Room.databaseBuilder(
                    context.applicationContext,
                    DermCalcDatabase::class.java,
                    "dermcalc.db"
                ).build().also { istanza = it }
            }
        }
    }
}
