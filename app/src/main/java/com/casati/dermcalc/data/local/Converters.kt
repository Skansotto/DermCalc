package com.casati.dermcalc.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun daTipoCalcolo(tipo: TipoCalcolo): String = tipo.name

    @TypeConverter
    fun aTipoCalcolo(valore: String): TipoCalcolo = TipoCalcolo.valueOf(valore)

    @TypeConverter
    fun daSesso(sesso: Sesso): String = sesso.name

    @TypeConverter
    fun aSesso(valore: String): Sesso = Sesso.valueOf(valore)
}
