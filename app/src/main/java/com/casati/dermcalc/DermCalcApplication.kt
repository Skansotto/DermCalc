package com.casati.dermcalc

import android.app.Application
import com.casati.dermcalc.data.local.DermCalcDatabase
import com.casati.dermcalc.data.repository.PazienteRepository

class DermCalcApplication : Application() {

    private val database by lazy { DermCalcDatabase.getInstance(this) }
    val pazienteRepository by lazy { PazienteRepository(database.pazienteDao()) }
}
